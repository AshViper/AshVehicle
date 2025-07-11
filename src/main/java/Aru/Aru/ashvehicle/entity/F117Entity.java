package Aru.Aru.ashvehicle.entity;

import com.atsuishio.superbwarfare.entity.projectile.Mk82Entity;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.Mk82Weapon;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.VehicleWeapon;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.tools.InventoryTool;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PlayMessages;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import Aru.Aru.ashvehicle.entity.Class.BaseAircraftEntity;
import Aru.Aru.ashvehicle.init.ModEntities;

public class F117Entity extends BaseAircraftEntity{
    private static final int MAX_BOMB_SLOTS = 3;
    public F117Entity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public F117Entity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this((EntityType) ModEntities.F_117.get(), level);
    }

    @Override
    public void positionRider(@NotNull Entity passenger, @NotNull Entity.@NotNull MoveFunction callback) {
        if (this.hasPassenger(passenger)) {
            Matrix4f transform = this.getVehicleTransform(1.0F);
            float x = 0.0F;
            float y = 1.6F;
            float z = 3.7F;
            y += (float)passenger.getMyRidingOffset();
            Vector4f worldPosition = this.transformPosition(transform, x, y, z );
            passenger.setPos((double)worldPosition.x, (double)worldPosition.y, (double)worldPosition.z);
            callback.accept(passenger, (double)worldPosition.x, (double)worldPosition.y, (double)worldPosition.z);
            this.copyEntityData(passenger);
        }
    }

    @Override
    public void handleAmmo() {
        boolean var10000;
        label73: {
            Entity var3 = this.getFirstPassenger();
            if (var3 instanceof Player player) {
                if (InventoryTool.hasCreativeAmmoBox(player)) {
                    var10000 = true;
                    break label73;
                }
            }

            var10000 = false;
        }

        boolean hasCreativeAmmoBox = var10000;

        if ((this.hasItem((Item)ModItems.MEDIUM_AERIAL_BOMB.get()) || hasCreativeAmmoBox) && this.reloadCoolDownBomb == 0 && (Integer)this.getEntityData().get(LOADED_BOMB) < 2) {
            this.entityData.set(LOADED_BOMB, (Integer)this.getEntityData().get(LOADED_BOMB) + 1);
            this.reloadCoolDownBomb = 300;
            if (!hasCreativeAmmoBox) {
                this.getItemStacks().stream().filter((stack) -> stack.is((Item)ModItems.MEDIUM_AERIAL_BOMB.get())).findFirst().ifPresent((stack) -> stack.shrink(1));
            }

            this.level().playSound((Player)null, this, (SoundEvent)ModSounds.BOMB_RELOAD.get(), this.getSoundSource(), 2.0F, 1.0F);
        }

        if (this.getWeaponIndex(0) == 0) {
            this.entityData.set(AMMO, (Integer)this.getEntityData().get(LOADED_BOMB));
        }

    }

    @Override
    public VehicleWeapon[][] initWeapons() {
        return new VehicleWeapon[][]{{
            (new Mk82Weapon()).sound(ModSounds.INTO_MISSILE.get())
        }};
    }

    public void vehicleShoot(Player player, int type) {
        if (this.getWeaponIndex(0) == 0 && (Integer)this.getEntityData().get(LOADED_BOMB) > 0) {
            Matrix4f transform = this.getVehicleTransform(1.0F);
            Mk82Entity mk82 = ((Mk82Weapon)this.getWeapon(0)).create(player);

            Vector4f worldPosition;
            worldPosition = this.transformPosition(transform, -0.55625F, -1.203125F, 0.0625F);

            mk82.setPos(worldPosition.x(), worldPosition.y(), worldPosition.z());
            mk82.shoot(this.getDeltaMovement().x, this.getDeltaMovement().y, this.getDeltaMovement().z,
                    (float)this.getDeltaMovement().length(), 10.0F);
            player.level().addFreshEntity(mk82);

            BlockPos pos = BlockPos.containing(new Vec3(worldPosition.x(), worldPosition.y(), worldPosition.z()));
            this.level().playSound(null, pos, ModSounds.BOMB_RELEASE.get(), SoundSource.PLAYERS, 3.0F, 1.0F);
            // 残弾数を1減らす
            this.getEntityData().set(LOADED_BOMB, this.getEntityData().get(LOADED_BOMB) - 1);
        }
    }
}
