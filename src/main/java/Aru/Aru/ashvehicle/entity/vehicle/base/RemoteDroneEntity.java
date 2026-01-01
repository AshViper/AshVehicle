package Aru.Aru.ashvehicle.entity.vehicle.base;

import com.atsuishio.superbwarfare.entity.vehicle.base.GeoVehicleEntity;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.item.Monitor;
import com.atsuishio.superbwarfare.tools.EntityFindUtil;
import com.atsuishio.superbwarfare.tools.VectorTool;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

/**
 * Base class for remotely controlled UAVs via monitor.
 * Player "sits" in drone when using monitor - this ensures chunk loading.
 * Camera switches to drone via CameraMixin.
 */
public abstract class RemoteDroneEntity extends GeoVehicleEntity {

    // Synced data
    public static final EntityDataAccessor<Boolean> LINKED = SynchedEntityData.defineId(RemoteDroneEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> CONTROLLER = SynchedEntityData.defineId(RemoteDroneEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Boolean> GEAR_DOWN = SynchedEntityData.defineId(RemoteDroneEntity.class, EntityDataSerializers.BOOLEAN);
    
    // Operator position (where they stood when started controlling)
    public static final EntityDataAccessor<Float> OPERATOR_X = SynchedEntityData.defineId(RemoteDroneEntity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> OPERATOR_Y = SynchedEntityData.defineId(RemoteDroneEntity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> OPERATOR_Z = SynchedEntityData.defineId(RemoteDroneEntity.class, EntityDataSerializers.FLOAT);

    public double lastTickSpeed;

    public RemoteDroneEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(LINKED, false);
        this.entityData.define(CONTROLLER, "");
        this.entityData.define(GEAR_DOWN, true);
        this.entityData.define(OPERATOR_X, 0f);
        this.entityData.define(OPERATOR_Y, 0f);
        this.entityData.define(OPERATOR_Z, 0f);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Linked", this.entityData.get(LINKED));
        compound.putString("Controller", this.entityData.get(CONTROLLER));
        compound.putBoolean("GearDown", this.entityData.get(GEAR_DOWN));
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Linked")) this.entityData.set(LINKED, compound.getBoolean("Linked"));
        if (compound.contains("Controller")) this.entityData.set(CONTROLLER, compound.getString("Controller"));
        if (compound.contains("GearDown")) this.entityData.set(GEAR_DOWN, compound.getBoolean("GearDown"));
    }


    @Override
    public void baseTick() {
        super.baseTick();
        lastTickSpeed = this.getDeltaMovement().length();

        if (!this.level().isClientSide && this.entityData.get(LINKED)) {
            Player controller = getController();
            if (controller != null) {
                ItemStack stack = controller.getMainHandItem();
                boolean isUsing = stack.is(ModItems.MONITOR.get()) 
                    && stack.getOrCreateTag().getBoolean("Using")
                    && stack.getOrCreateTag().getBoolean("Linked")
                    && stack.getOrCreateTag().getString(Monitor.LINKED_DRONE).equals(this.getStringUUID());
                
                // Mount player in drone when using monitor
                if (isUsing && !this.hasPassenger(controller)) {
                    // Save operator position
                    this.entityData.set(OPERATOR_X, (float) controller.getX());
                    this.entityData.set(OPERATOR_Y, (float) controller.getY());
                    this.entityData.set(OPERATOR_Z, (float) controller.getZ());
                    controller.startRiding(this, true);
                }
                
                // Dismount when stopped using
                if (!isUsing && this.hasPassenger(controller)) {
                    ejectController(controller);
                }
            }
        }
        
        this.refreshDimensions();
    }

    /**
     * Eject controller and return to original position
     */
    public void ejectController(Player controller) {
        controller.stopRiding();
        double x = this.entityData.get(OPERATOR_X);
        double y = this.entityData.get(OPERATOR_Y);
        double z = this.entityData.get(OPERATOR_Z);
        if (x != 0 || y != 0 || z != 0) {
            controller.teleportTo(x, y, z);
        }
        // Reset monitor Using state
        ItemStack stack = controller.getMainHandItem();
        if (stack.is(ModItems.MONITOR.get())) {
            stack.getOrCreateTag().putBoolean("Using", false);
        }
    }

    public Player getController() {
        String uuid = this.entityData.get(CONTROLLER);
        if (uuid == null || uuid.isEmpty()) return null;
        return EntityFindUtil.findPlayer(this.level(), uuid);
    }

    public boolean isLinked() {
        return this.entityData.get(LINKED);
    }
    
    public boolean isGearDown() {
        return this.entityData.get(GEAR_DOWN);
    }
    
    public void toggleGear() {
        this.entityData.set(GEAR_DOWN, !this.entityData.get(GEAR_DOWN));
    }

    public Vec3 getOperatorPosition() {
        return new Vec3(
            this.entityData.get(OPERATOR_X),
            this.entityData.get(OPERATOR_Y),
            this.entityData.get(OPERATOR_Z)
        );
    }

    // ==================== INTERACTION ====================

    @Override
    public @NotNull InteractionResult interact(Player player, @NotNull InteractionHand hand) {
        // Только для главной руки
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }
        
        ItemStack stack = player.getMainHandItem();
        
        if (stack.is(ModItems.MONITOR.get())) {
            if (!player.isCrouching()) {
                return linkDrone(player, stack);
            } else {
                return unlinkDrone(player, stack);
            }
        }
        
        return InteractionResult.PASS;
    }

    private InteractionResult linkDrone(Player player, ItemStack stack) {
        if (this.entityData.get(LINKED)) {
            player.displayClientMessage(Component.translatable("tips.superbwarfare.drone.already_linked")
                .withStyle(ChatFormatting.RED), true);
            return InteractionResult.sidedSuccess(this.level().isClientSide());
        }
        
        if (stack.getOrCreateTag().getBoolean("Linked")) {
            player.displayClientMessage(Component.translatable("tips.superbwarfare.monitor.already_linked")
                .withStyle(ChatFormatting.RED), true);
            return InteractionResult.sidedSuccess(this.level().isClientSide());
        }

        this.entityData.set(LINKED, true);
        this.entityData.set(CONTROLLER, player.getStringUUID());
        Monitor.link(stack, this.getStringUUID());
        
        player.displayClientMessage(Component.translatable("tips.superbwarfare.monitor.linked")
            .withStyle(ChatFormatting.GREEN), true);

        if (player instanceof ServerPlayer sp) {
            sp.level().playSound(null, sp.getOnPos(), SoundEvents.ARROW_HIT_PLAYER, SoundSource.PLAYERS, 0.5F, 1);
        }
        
        return InteractionResult.sidedSuccess(this.level().isClientSide());
    }

    private InteractionResult unlinkDrone(Player player, ItemStack stack) {
        // Проверяем что дрон привязан
        if (!this.entityData.get(LINKED)) {
            player.displayClientMessage(Component.translatable("tips.superbwarfare.drone.not_linked")
                .withStyle(ChatFormatting.YELLOW), true);
            return InteractionResult.sidedSuccess(this.level().isClientSide());
        }
        
        // Проверяем что этот игрок - контроллер дрона
        String controllerUUID = this.entityData.get(CONTROLLER);
        if (!controllerUUID.equals(player.getStringUUID())) {
            player.displayClientMessage(Component.translatable("tips.superbwarfare.drone.not_your_drone")
                .withStyle(ChatFormatting.RED), true);
            return InteractionResult.sidedSuccess(this.level().isClientSide());
        }

        // Eject controller if in drone
        Player controller = getController();
        if (controller != null && this.hasPassenger(controller)) {
            ejectController(controller);
        }
        
        this.entityData.set(CONTROLLER, "");
        this.entityData.set(LINKED, false);
        Monitor.disLink(stack, player);
        
        player.displayClientMessage(Component.translatable("tips.superbwarfare.monitor.unlinked")
            .withStyle(ChatFormatting.GREEN), true);

        if (player instanceof ServerPlayer sp) {
            sp.level().playSound(null, sp.getOnPos(), SoundEvents.ARROW_HIT_PLAYER, SoundSource.PLAYERS, 0.5F, 1);
        }
        
        return InteractionResult.sidedSuccess(this.level().isClientSide());
    }


    // ==================== FLIGHT CONTROL ====================

    @Override
    public void travel() {
        if (!isLinked()) {
            // Not linked - fall
            this.setDeltaMovement(this.getDeltaMovement().add(0, -0.04, 0));
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.95, 0.98, 0.95));
            return;
        }
        handleAircraftEngine();
    }

    protected void handleAircraftEngine() {
        // Aerodynamics
        float drag = (float) Mth.clamp(
            Math.max((this.onGround() ? 0.819 : 0.82) - 0.005 * this.getDeltaMovement().length(), 0.5),
            0.01, 0.99
        );

        boolean movingForward = this.getDeltaMovement().dot(this.getViewVector(1.0f)) > 0.0;
        this.setDeltaMovement(this.getDeltaMovement().add(
            this.getViewVector(1.0f).scale((movingForward ? 0.227 : 0.1) * this.getDeltaMovement().dot(this.getViewVector(1.0f)))
        ));
        this.setDeltaMovement(this.getDeltaMovement().multiply(drag, drag, drag));

        Player controller = getController();
        boolean isControlling = false;
        if (controller != null) {
            ItemStack stack = controller.getMainHandItem();
            isControlling = stack.is(ModItems.MONITOR.get()) 
                && stack.getOrCreateTag().getBoolean("Using")
                && stack.getOrCreateTag().getBoolean("Linked");
        }

        if (this.getHealth() > 0.1f * this.getMaxHealth()) {
            if (isControlling) {
                // W - throttle up
                if (this.forwardInputDown()) {
                    this.entityData.set(POWER, (float) Mth.clamp(this.entityData.get(POWER) + 0.006f, 0, 1.0));
                }
                // S - throttle down (but not below 0.4 in air)
                if (this.backInputDown()) {
                    float minPower = this.onGround() ? 0.0f : 0.4f;
                    this.entityData.set(POWER, Math.max(this.entityData.get(POWER) - 0.008f, minPower));
                }
                // Thrust holds - NO auto decay!

                // A/D - roll
                if (!this.onGround()) {
                    if (this.rightInputDown()) {
                        this.entityData.set(DELTA_ROT, this.entityData.get(DELTA_ROT) - 0.8f);
                    } else if (this.leftInputDown()) {
                        this.entityData.set(DELTA_ROT, this.entityData.get(DELTA_ROT) + 0.8f);
                    }
                }

                // Space - takeoff / pitch up / toggle gear
                if (this.upInputDown()) {
                    if (this.onGround()) {
                        // Takeoff
                        this.setDeltaMovement(this.getDeltaMovement().add(0, 0.1, 0));
                        this.entityData.set(POWER, Math.max(this.entityData.get(POWER), 0.7f));
                    } else {
                        // Pitch up
                        this.setXRot(Mth.clamp(this.getXRot() - 0.6f, -89.0f, 89.0f));
                    }
                }

                // Shift - brake / pitch down
                if (this.downInputDown()) {
                    if (this.onGround()) {
                        // Ground brake
                        this.setDeltaMovement(this.getDeltaMovement().multiply(0.95, 1.0, 0.95));
                    } else {
                        // Air brake + pitch down
                        this.setDeltaMovement(this.getDeltaMovement().multiply(0.992, 1.0, 0.992));
                        this.setXRot(Mth.clamp(this.getXRot() + 0.4f, -89.0f, 89.0f));
                    }
                }
            } else {
                // No control - autopilot (slow descent)
                if (!this.onGround()) {
                    this.setXRot(Mth.clamp(this.getXRot() + 0.05f, -89.0f, 89.0f));
                }
            }

            // Mouse controls yaw/pitch
            float rotSpeed = 1.5f + 1.2f * Mth.abs(VectorTool.calculateY(this.getRoll()));
            float addY = Mth.clamp(Math.max((this.onGround() ? 0.6f : 0.25f) * (float) this.getDeltaMovement().length(), 0.0f) * this.getMouseMoveSpeedX(), -rotSpeed, rotSpeed);
            float addX = Mth.clamp(Math.min((float) Math.max(this.getDeltaMovement().dot(this.getViewVector(1.0f)) - 0.24, 0.15), 0.4f) * this.getMouseMoveSpeedY(), -3.5f, 3.5f);
            float addZ = this.entityData.get(DELTA_ROT) - (this.onGround() ? 0.0f : 0.004f) * this.getMouseMoveSpeedX() * (float) this.getDeltaMovement().dot(this.getViewVector(1.0f));

            this.setYRot(this.getYRot() + addY);
            if (!this.onGround()) {
                this.setXRot(this.getXRot() + addX);
                this.setZRot(this.getRoll() - addZ);
                
                // Auto-level roll
                float xSpeed = 1.0f + 20.0f * Mth.abs(this.getXRot() / 180.0f);
                float speed = Mth.clamp(Mth.abs(this.getRoll()) / (90.0f / xSpeed), 0.0f, 1.0f);
                if (this.getRoll() > 0) this.setZRot(this.getRoll() - Math.min(speed, this.getRoll()));
                else if (this.getRoll() < 0) this.setZRot(this.getRoll() + Math.min(speed, -this.getRoll()));
            }

            // Thrust
            float power = this.entityData.get(POWER);
            this.setDeltaMovement(this.getDeltaMovement().add(this.getViewVector(1.0f).scale(power * 0.13)));

            // Gravity
            if (!this.onGround()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0, -0.04, 0));
            }
        }

        this.entityData.set(DELTA_ROT, this.entityData.get(DELTA_ROT) * 0.8f);
    }

    // ==================== PASSENGER ====================

    @Override
    protected boolean canAddPassenger(@NotNull Entity passenger) {
        if (passenger instanceof Player player) {
            return player.getStringUUID().equals(this.entityData.get(CONTROLLER));
        }
        return false;
    }

    @Override
    public boolean canRiderInteract() {
        // Prevent player from dismounting with shift key
        return false;
    }

    @Override
    public void positionRider(@NotNull Entity passenger, @NotNull MoveFunction callback) {
        if (this.hasPassenger(passenger)) {
            // Position inside drone (player hidden)
            callback.accept(passenger, this.getX(), this.getY() + 0.5, this.getZ());
        }
    }

    @Override
    public int getSeatIndex(Entity entity) {
        if (entity instanceof Player player && player.getStringUUID().equals(this.entityData.get(CONTROLLER))) {
            return 0;
        }
        return super.getSeatIndex(entity);
    }

    // ==================== DESTRUCTION ====================

    @Override
    public void destroy() {
        Player controller = getController();
        if (controller != null) {
            // ВАЖНО: сначала получаем сохранённую позицию оператора
            double opX = this.entityData.get(OPERATOR_X);
            double opY = this.entityData.get(OPERATOR_Y);
            double opZ = this.entityData.get(OPERATOR_Z);
            
            // Высаживаем игрока
            if (this.hasPassenger(controller)) {
                controller.stopRiding();
            }
            
            // Телепортируем на сохранённую позицию (не на позицию дрона!)
            if (opX != 0 || opY != 0 || opZ != 0) {
                controller.teleportTo(opX, opY, opZ);
            }
            
            // Сбрасываем монитор
            ItemStack stack = controller.getMainHandItem();
            if (stack.is(ModItems.MONITOR.get())) {
                stack.getOrCreateTag().putBoolean("Using", false);
                Monitor.disLink(stack, controller);
            }
        }
        super.destroy();
    }
    
    /**
     * Метод для стрельбы с передачей UUID цели - стреляет ракетой Missile
     */
    @Override
    public void vehicleShoot(net.minecraft.world.entity.LivingEntity living, java.util.UUID uuid, Vec3 targetPos) {
        // Используем modifyGunData для оружия "Missile"
        modifyGunData("Missile", data -> {
            if (!data.canShoot(this.getAmmoSupplier())) return;
            
            if (this.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                data.shoot(new com.atsuishio.superbwarfare.data.gun.ShootParameters(
                    this.getAmmoSupplier(),
                    living,
                    serverLevel,
                    getShootPos("Missile", 1f),
                    getShootVec("Missile", 1f),
                    data,
                    data.compute().spread,
                    true,
                    uuid,
                    targetPos
                ));
            }
        });
        
        // Звук и эффекты
        var gunData = getGunData("Missile");
        if (gunData != null && living != null) {
            afterShoot(gunData, getShootVec("Missile", 1f));
            playShootSound3p(living, "Missile");
        }
    }
}
