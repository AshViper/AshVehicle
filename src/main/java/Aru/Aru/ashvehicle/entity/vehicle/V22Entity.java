package Aru.Aru.ashvehicle.entity.vehicle;

import com.atsuishio.superbwarfare.data.DataLoader;
import com.atsuishio.superbwarfare.data.vehicle.DefaultVehicleData;
import com.atsuishio.superbwarfare.data.vehicle.subdata.EngineInfo;
import com.atsuishio.superbwarfare.entity.vehicle.base.GeoVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.utils.VehicleVecUtils;
import com.atsuishio.superbwarfare.init.ModDamageTypes;
import com.atsuishio.superbwarfare.tools.VectorTool;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Math;

public class V22Entity extends GeoVehicleEntity {
    public V22Entity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public static boolean vtolMode = true;
    private static final EntityDataAccessor<Float> VTOL_ROT = SynchedEntityData.defineId(V22Entity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> WEAPON_BAY_ROT = SynchedEntityData.defineId(V22Entity.class, EntityDataSerializers.FLOAT);
    public float vtolRotO = 0f;
    public float weaponBayRotO = 0f;

    @OnlyIn(Dist.CLIENT)
    private V22Entity.V22EngineSound engineSound;

    DefaultVehicleData computed = this.computed();
    JsonObject engineInfo = computed.engineInfo;
    EngineInfo.Aircraft aircraft = DataLoader.GSON.fromJson(engineInfo, EngineInfo.Aircraft.class);

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(VTOL_ROT, 0.0F);
        this.entityData.define(WEAPON_BAY_ROT, 0.0F);
    }

    public void setPodRot(float value) {
        this.entityData.set(VTOL_ROT, value);
    }

    public float getPodRot() {
        return this.entityData.get(VTOL_ROT);
    }

    public void setWeaponBayRot(float value) {
        this.entityData.set(WEAPON_BAY_ROT, value);
    }

    public float getWeaponBayRot() {
        return this.entityData.get(WEAPON_BAY_ROT);
    }

    @Override
    public void baseTick() {
        super.baseTick();
        aircraftEngine(this, aircraft);

        // クライアント側でエンジン音を管理
        if (this.level().isClientSide) {
            handleEngineSound();
        }

        vtolRotO = getPodRot();
        float target = this.vtolMode ? 0.0F : 85.0F;
        float current = getPodRot();
        float diff = target - current;
        float newRot = current + diff * 0.05f;
        setPodRot(newRot);

        int driverWeapon = this.getWeaponIndex(0);
        boolean driverNeedsBay = driverWeapon >= 1;
        weaponBayRotO = getWeaponBayRot();
        float target1 = driverNeedsBay ? 90.0F : 0.0F;
        float current1 = getWeaponBayRot();
        float diff1 = target1 - current1;
        float newRot1 = current1 + diff1 * 0.1f;
        setWeaponBayRot(newRot1);
    }

    @OnlyIn(Dist.CLIENT)
    private void handleEngineSound() {
        boolean shouldPlay = this.engineRunning() && !this.isRemoved();

        if (shouldPlay) {
            if (this.engineSound == null || !Minecraft.getInstance().getSoundManager().isActive(this.engineSound)) {
                this.engineSound = new V22Entity.V22EngineSound(this);
                Minecraft.getInstance().getSoundManager().play(this.engineSound);
            }
        } else {
            if (this.engineSound != null) {
                this.engineSound = null;
            }
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        if (this.level().isClientSide && this.engineSound != null) {
            this.engineSound = null;
        }
    }

    // Vキー用（Packet から呼ばれる）
    public void toggleVtolMode() {
        vtolMode = !vtolMode;
    }

    // エンジン音クラス
    @OnlyIn(Dist.CLIENT)
    public static class V22EngineSound extends AbstractTickableSoundInstance {
        private final V22Entity vehicle;

        public V22EngineSound(V22Entity vehicle) {
            super(vehicle.getEngineSound(), SoundSource.NEUTRAL, SoundInstance.createUnseededRandom());
            this.vehicle = vehicle;
            this.looping = true;
            this.delay = 0;
            this.volume = 0.0F;
            this.pitch = 1.0F;  // 固定ピッチ
            this.x = vehicle.getX();
            this.y = vehicle.getY();
            this.z = vehicle.getZ();
        }

        @Override
        public void tick() {
            if (this.vehicle.isRemoved() || !this.vehicle.engineRunning()) {
                this.stop();
                return;
            }

            // 位置を更新
            this.x = this.vehicle.getX();
            this.y = this.vehicle.getY();
            this.z = this.vehicle.getZ();

            // 音量を計算（POWERに基づく）
            float power = org.joml.Math.abs(this.vehicle.getPower());
            float targetVolume = Mth.clamp(power * 2.0F, 0.0F, 3.0F);

            // スムーズに音量を変化
            this.volume = Mth.lerp(0.1F, this.volume, targetVolume);

            // アフターバーナー（スプリント）時は音量を少し上げる
            if (this.vehicle.sprintInputDown()) {
                this.volume = org.joml.Math.min(this.volume * 1.2F, 4.0F);
            }
        }

        @Override
        public boolean canStartSilent() {
            return true;
        }
    }

    public static void aircraftEngine(VehicleEntity vehicle, EngineInfo.Aircraft engineInfo) {
        float powerAdd = engineInfo.increment;
        float powerReduce = engineInfo.decrement;
        float pitchSpeed = engineInfo.pitchSpeed;
        float yawSpeed = engineInfo.yawSpeed;
        float rollSpeed = engineInfo.rollSpeed;
        float lift = engineInfo.liftSpeed;
        float speedRate = engineInfo.speedRate;
        float gearRotateAngle = engineInfo.gearRotateAngle;
        int energyCost = (int)(engineInfo.energyCostRate * (double)Mth.abs((Float)vehicle.getEntityData().get(VehicleEntity.POWER)));
        float f = (float)Mth.clamp(org.joml.Math.max((double)(vehicle.onGround() ? 0.819F : 0.82F) - 0.005 * vehicle.getDeltaMovement().length(), (double)0.5F) + (double)(0.001F * Mth.abs(90.0F - (float) VehicleVecUtils.calculateAngle(vehicle.getDeltaMovement(), vehicle.getViewVector(1.0F))) / 90.0F), 0.01, 0.99);
        boolean forward = vehicle.getDeltaMovement().dot(vehicle.getViewVector(1.0F)) > (double)0.0F;
        vehicle.setDeltaMovement(vehicle.getDeltaMovement().add(vehicle.getViewVector(1.0F).scale((forward ? 0.227 : 0.1) * vehicle.getDeltaMovement().dot(vehicle.getViewVector(1.0F)))));
        vehicle.setDeltaMovement(vehicle.getDeltaMovement().multiply((double)f, (double)f, (double)f));
        if (vehicle.isInFluidType() && vehicle.tickCount % 4 == 0) {
            vehicle.setDeltaMovement(vehicle.getDeltaMovement().multiply(0.6, 0.6, 0.6));
            if (vehicle.lastTickSpeed > 0.4) {
                vehicle.hurt(ModDamageTypes.causeVehicleStrikeDamage(vehicle.level().registryAccess(), vehicle, (Entity)(vehicle.getFirstPassenger() == null ? vehicle : vehicle.getFirstPassenger())), (float)((double)20.0F * (vehicle.lastTickSpeed - 0.4) * (vehicle.lastTickSpeed - 0.4)));
            }
        }

        Entity passenger = vehicle.getFirstPassenger();
        if (vehicle.getEnergy() >= energyCost && (vehicle.getMaxEnergy() <= 0 || vehicle.getEnergy() > 0)) {
            vehicle.consumeEnergy(energyCost);
        } else {
            vehicle.setForwardInputDown(false);
            vehicle.setBackInputDown(false);
            vehicle.engineStart = false;
            vehicle.engineStartOver = false;
            vehicle.getEntityData().set(VehicleEntity.POWER, (Float)vehicle.getEntityData().get(VehicleEntity.POWER) * 0.95F);
        }

        if (vehicle.getHealth() > 0.1F * vehicle.getMaxHealth()) {
            if (passenger != null && !vehicle.isInFluidType()) {
                if (passenger instanceof Player) {
                    if (!vehicle.engineStart && vehicle.forwardInputDown() && (double)(Float)vehicle.getEntityData().get(VehicleEntity.POWER) > 0.01) {
                        vehicle.engineStart = true;
                        vehicle.level().playSound((Player)null, vehicle, engineInfo.engineStartSound, vehicle.getSoundSource(), 3.0F, 1.0F);
                    }

                    if (vehicle.getEnergy() >= energyCost) {
                        if (vehicle.forwardInputDown()) {
                            vehicle.getEntityData().set(VehicleEntity.POWER, (float)Mth.clamp((double)((Float)vehicle.getEntityData().get(VehicleEntity.POWER) + 0.0045F * powerAdd), -0.1, (double)1.0F));
                        }

                        if (vehicle.backInputDown()) {
                            vehicle.getEntityData().set(VehicleEntity.POWER, org.joml.Math.max((Float)vehicle.getEntityData().get(VehicleEntity.POWER) - 0.006F * powerReduce, vehicle.onGround() ? -0.2F : 0.4F));
                        }
                    }

                    if (!vehicle.forwardInputDown() && !vehicle.backInputDown()) {
                        vehicle.getEntityData().set(VehicleEntity.POWER, (Float)vehicle.getEntityData().get(VehicleEntity.POWER) * 0.995F);
                    }

                    if (!vehicle.onGround()) {
                        if (vehicle.rightInputDown()) {
                            vehicle.getEntityData().set(VehicleEntity.DELTA_ROT, (Float)vehicle.getEntityData().get(VehicleEntity.DELTA_ROT) - 0.6F);
                        } else if (vehicle.leftInputDown()) {
                            vehicle.getEntityData().set(VehicleEntity.DELTA_ROT, (Float)vehicle.getEntityData().get(VehicleEntity.DELTA_ROT) + 0.6F);
                        }
                    }

                    if (vehicle.downInputDown()) {
                        if (vehicle.onGround()) {
                            vehicle.getEntityData().set(VehicleEntity.POWER, (Float)vehicle.getEntityData().get(VehicleEntity.POWER) * 0.92F);
                            vehicle.setDeltaMovement(vehicle.getDeltaMovement().multiply(0.97, (double)1.0F, 0.97));
                        } else {
                            vehicle.getEntityData().set(VehicleEntity.POWER, (Float)vehicle.getEntityData().get(VehicleEntity.POWER) * 0.97F);
                            vehicle.setDeltaMovement(vehicle.getDeltaMovement().multiply(0.994, (double)1.0F, 0.994));
                        }

                        vehicle.getEntityData().set(VehicleEntity.PLANE_BREAK, org.joml.Math.min((Float)vehicle.getEntityData().get(VehicleEntity.PLANE_BREAK) + 10.0F, 60.0F));
                    }
                }
            } else {
                vehicle.setLeftInputDown(false);
                vehicle.setRightInputDown(false);
                vehicle.setForwardInputDown(false);
                vehicle.setBackInputDown(false);
                vehicle.getEntityData().set(VehicleEntity.POWER, (Float)vehicle.getEntityData().get(VehicleEntity.POWER) * 0.95F);
                if (vehicle.onGround()) {
                    vehicle.setDeltaMovement(vehicle.getDeltaMovement().multiply(0.94, (double)1.0F, 0.94));
                } else {
                    vehicle.setXRot(Mth.clamp(vehicle.getXRot() + 0.1F, -89.0F, 89.0F));
                }
            }

            float rotSpeed = 1.5F + 1.2F * Mth.abs(VectorTool.calculateY(vehicle.getRoll()));
            float addY = Mth.clamp(org.joml.Math.max((vehicle.onGround() ? 0.6F : 0.2F) * (float)vehicle.getDeltaMovement().length(), 0.0F) * vehicle.getMouseMoveSpeedX(), -rotSpeed, rotSpeed);
            float addX = Mth.clamp(org.joml.Math.min((float) org.joml.Math.max(vehicle.getDeltaMovement().dot(vehicle.getViewVector(1.0F)) - 0.24, 0.15), 0.4F) * vehicle.getMouseMoveSpeedY(), -3.5F, 3.5F);
            float addZ = (Float)vehicle.getEntityData().get(VehicleEntity.DELTA_ROT) - (vehicle.onGround() ? 0.0F : 0.004F) * vehicle.getMouseMoveSpeedX() * (float)vehicle.getDeltaMovement().dot(vehicle.getViewVector(1.0F));
            vehicle.setYRot(vehicle.getYRot() + yawSpeed * addY);
            if (!vehicle.onGround()) {
                vehicle.setXRot(vehicle.getXRot() + pitchSpeed * addX);
                vehicle.setZRot(vehicle.getRoll() - rollSpeed * addZ);
            }

            if (!vehicle.onGround()) {
                float xSpeed = 1.0F + 20.0F * Mth.abs(vehicle.getXRot() / 180.0F);
                float speed = Mth.clamp(Mth.abs(vehicle.getRoll()) / (90.0F / xSpeed), 0.0F, 1.0F);
                if (vehicle.getRoll() > 0.0F) {
                    vehicle.setZRot(vehicle.getRoll() - org.joml.Math.min(speed, vehicle.getRoll()));
                } else if (vehicle.getRoll() < 0.0F) {
                    vehicle.setZRot(vehicle.getRoll() + org.joml.Math.min(speed, -vehicle.getRoll()));
                }
            }

            vehicle.setPropellerRot(vehicle.getPropellerRot() + 30.0F * (Float)vehicle.getEntityData().get(VehicleEntity.POWER));
            if (engineInfo.hasGear) {
                if (vehicle.upInputDown()) {
                    vehicle.setUpInputDown(false);
                    if ((Float)vehicle.getEntityData().get(VehicleEntity.GEAR_ROT) == 0.0F && !vehicle.onGround()) {
                        vehicle.getEntityData().set(VehicleEntity.GEAR_UP, true);
                    } else if ((Float)vehicle.getEntityData().get(VehicleEntity.GEAR_ROT) == 1.0F) {
                        vehicle.getEntityData().set(VehicleEntity.GEAR_UP, false);
                    }
                }

                if (vehicle.onGround()) {
                    vehicle.getEntityData().set(VehicleEntity.GEAR_UP, false);
                }

                if ((Boolean)vehicle.getEntityData().get(VehicleEntity.GEAR_UP)) {
                    vehicle.getEntityData().set(VehicleEntity.GEAR_ROT, org.joml.Math.min((Float)vehicle.getEntityData().get(VehicleEntity.GEAR_ROT) + 0.05F, 1.0F));
                } else {
                    vehicle.getEntityData().set(VehicleEntity.GEAR_ROT, org.joml.Math.max((Float)vehicle.getEntityData().get(VehicleEntity.GEAR_ROT) - 0.05F, 0.0F));
                }

                vehicle.setGearRot((Float)vehicle.getEntityData().get(VehicleEntity.GEAR_ROT) * gearRotateAngle);
            }

            float flapX = (1.0F - Mth.abs(vehicle.getRoll()) / 90.0F) * Mth.clamp(vehicle.getMouseMoveSpeedY(), -22.5F, 22.5F) - VectorTool.calculateY(vehicle.getRoll()) * Mth.clamp(vehicle.getMouseMoveSpeedX(), -22.5F, 22.5F);
            vehicle.setFlap1LRot(Mth.clamp(-flapX - 4.0F * addZ - (Float)vehicle.getEntityData().get(VehicleEntity.PLANE_BREAK), -22.5F, 22.5F));
            vehicle.setFlap1RRot(Mth.clamp(-flapX + 4.0F * addZ - (Float)vehicle.getEntityData().get(VehicleEntity.PLANE_BREAK), -22.5F, 22.5F));
            vehicle.setFlap1L2Rot(Mth.clamp(-flapX - 4.0F * addZ + (Float)vehicle.getEntityData().get(VehicleEntity.PLANE_BREAK), -22.5F, 22.5F));
            vehicle.setFlap1R2Rot(Mth.clamp(-flapX + 4.0F * addZ + (Float)vehicle.getEntityData().get(VehicleEntity.PLANE_BREAK), -22.5F, 22.5F));
            vehicle.setFlap2LRot(Mth.clamp(flapX - 4.0F * addZ, -22.5F, 22.5F));
            vehicle.setFlap2RRot(Mth.clamp(flapX + 4.0F * addZ, -22.5F, 22.5F));
            float flapY = (1.0F - Mth.abs(vehicle.getRoll()) / 90.0F) * Mth.clamp(vehicle.getMouseMoveSpeedX(), -22.5F, 22.5F) + VectorTool.calculateY(vehicle.getRoll()) * Mth.clamp(vehicle.getMouseMoveSpeedY(), -22.5F, 22.5F);
            vehicle.setFlap3Rot(flapY * 5.0F);
        } else if (!vehicle.onGround()) {
            vehicle.getEntityData().set(VehicleEntity.POWER, org.joml.Math.max((Float)vehicle.getEntityData().get(VehicleEntity.POWER) - 3.0E-4F, 0.02F));
            vehicle.destroyRot += 0.1F;
            float diffX = 90.0F - vehicle.getXRot();
            vehicle.setXRot(vehicle.getXRot() + diffX * 0.001F * vehicle.destroyRot);
            vehicle.setZRot(vehicle.getRoll() - vehicle.destroyRot);
            vehicle.setDeltaMovement(vehicle.getDeltaMovement().add((double)0.0F, -0.03, (double)0.0F));
            vehicle.setDeltaMovement(vehicle.getDeltaMovement().add((double)0.0F, (double)(-vehicle.destroyRot) * 0.005, (double)0.0F));
        }

        vehicle.getEntityData().set(VehicleEntity.DELTA_ROT, (Float)vehicle.getEntityData().get(VehicleEntity.DELTA_ROT) * 0.85F);
        vehicle.getEntityData().set(VehicleEntity.PLANE_BREAK, (Float)vehicle.getEntityData().get(VehicleEntity.PLANE_BREAK) * 0.8F);
        if (vehicle.onGround()) {
            vehicle.getEntityData().set(VehicleEntity.POWER, (Float)vehicle.getEntityData().get(VehicleEntity.POWER) * 0.995F);
        }

        if ((Boolean)vehicle.getEntityData().get(VehicleEntity.MAIN_ENGINE_DAMAGED)) {
            vehicle.getEntityData().set(VehicleEntity.POWER, (Float)vehicle.getEntityData().get(VehicleEntity.POWER) * 0.96F);
        }

        if ((Boolean)vehicle.getEntityData().get(VehicleEntity.SUB_ENGINE_DAMAGED)) {
            vehicle.getEntityData().set(VehicleEntity.POWER, (Float)vehicle.getEntityData().get(VehicleEntity.POWER) * 0.96F);
        }

        double flapAngle = (double)((vehicle.getFlap1LRot() + vehicle.getFlap1RRot() + vehicle.getFlap1L2Rot() + vehicle.getFlap1R2Rot()) / 4.0F);
        vehicle.setDeltaMovement(vehicle.getDeltaMovement().add(vehicle.getUpVec(1.0F).scale(vehicle.getDeltaMovement().dot(vehicle.getViewVector(1.0F)) * 0.022 * (double)lift * ((double)1.0F + Math.sin((vehicle.onGround() ? (double)25.0F : flapAngle + (double)25.0F) * (double)((float)java.lang.Math.PI / 180F))))));
        if (vtolMode){
            vehicle.setDeltaMovement(vehicle.getDeltaMovement().add(vehicle.getViewVector(1.0F).lerp(vehicle.getUpVec(1.0F), 1.0F).normalize().scale(0.25 * speedRate * (Float)vehicle.getEntityData().get(VehicleEntity.POWER) * (vehicle.sprintInputDown() ? 2.2 : 1.0))));
        }else{
            vehicle.setDeltaMovement(vehicle.getDeltaMovement().add(vehicle.getViewVector(1.0F).scale(0.03 * (double)speedRate * (double)(Float)vehicle.getEntityData().get(VehicleEntity.POWER) * (vehicle.sprintInputDown() ? 2.2 : (double)1.0F))));
        }
        if ((Float)vehicle.getEntityData().get(VehicleEntity.POWER) > 0.2F) {
            vehicle.engineStartOver = true;
        }

        if ((Float)vehicle.getEntityData().get(VehicleEntity.POWER) < 4.0E-4F) {
            vehicle.engineStart = false;
            vehicle.engineStartOver = false;
        }
    }
}