package Aru.Aru.ashvehicle.entity;

import com.atsuishio.superbwarfare.entity.projectile.Mk82Entity;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.*;
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
import Aru.Aru.ashvehicle.entity.weapon.GBU57Entity;
import Aru.Aru.ashvehicle.entity.weapon.GBU57Weapon;
import Aru.Aru.ashvehicle.init.ModEntities;

public class B2Entity extends BaseAircraftEntity {
    public B2Entity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public B2Entity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this((EntityType) ModEntities.B_2.get(), level);
    }

    @Override
    public void positionRider(@NotNull Entity passenger, @NotNull Entity.@NotNull MoveFunction callback) {
        if (this.hasPassenger(passenger)) {
            Matrix4f transform = this.getVehicleTransform(1.0F);
            float x = 0.6F;
            float y = 2.2F;
            float z = 4.7F;
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

        if ((this.hasItem((Item)ModItems.MEDIUM_AERIAL_BOMB.get()) || hasCreativeAmmoBox) && this.reloadCoolDownBomb == 0 && (Integer)this.getEntityData().get(LOADED_BOMB) < 50) {
            this.entityData.set(LOADED_BOMB, (Integer)this.getEntityData().get(LOADED_BOMB) + 1);
            this.reloadCoolDownBomb = 20;
            if (!hasCreativeAmmoBox) {
                this.getItemStacks().stream().filter((stack) -> stack.is((Item)ModItems.MEDIUM_AERIAL_BOMB.get())).findFirst().ifPresent((stack) -> stack.shrink(1));
            }

            this.level().playSound((Player)null, this, (SoundEvent)ModSounds.BOMB_RELOAD.get(), this.getSoundSource(), 2.0F, 1.0F);
        }

        if ((this.hasItem((Item)ModItems.MEDIUM_AERIAL_BOMB.get()) || hasCreativeAmmoBox) && this.reloadCoolDownMissile == 0 && (Integer)this.getEntityData().get(LOADED_MISSILE) < 2) {
            this.entityData.set(LOADED_MISSILE, (Integer)this.getEntityData().get(LOADED_MISSILE) + 1);
            this.reloadCoolDownMissile = 20;
            if (!hasCreativeAmmoBox) {
                this.getItemStacks().stream().filter((stack) -> stack.is((Item)ModItems.MEDIUM_AERIAL_BOMB.get())).findFirst().ifPresent((stack) -> stack.shrink(1));
            }

            this.level().playSound((Player)null, this, (SoundEvent)ModSounds.BOMB_RELOAD.get(), this.getSoundSource(), 2.0F, 1.0F);
        }

        if (this.getWeaponIndex(0) == 0) {
            this.entityData.set(AMMO, (Integer)this.getEntityData().get(LOADED_BOMB));
        }else if (this.getWeaponIndex(0) == 1) {
            this.entityData.set(AMMO, (Integer)this.getEntityData().get(LOADED_MISSILE));
        }

    }

    @Override
    public VehicleWeapon[][] initWeapons() {
        return new VehicleWeapon[][]{{
            (new Mk82Weapon()).sound(ModSounds.INTO_MISSILE.get()),
                (new GBU57Weapon().sound(ModSounds.INTO_MISSILE.get()))
        }};
    }

    @Override
    public void vehicleShoot(Player player, int type) {
        Matrix4f transform = this.getVehicleTransform(1.0F);
        if (this.getWeaponIndex(0) == 0 && (Integer)this.getEntityData().get(LOADED_BOMB) > 0) {
            Mk82Entity Mk82Entity = ((Mk82Weapon)this.getWeapon(0)).create(player);
            Vector4f worldPosition;
            if ((Integer)this.getEntityData().get(LOADED_BOMB) == 3) {
                worldPosition = this.transformPosition(transform, 0.55625F, -1.203125F, 0.0625F);
            } else if ((Integer)this.getEntityData().get(LOADED_BOMB) == 2) {
                worldPosition = this.transformPosition(transform, 0.0F, -1.203125F, 0.0625F);
            } else {
                worldPosition = this.transformPosition(transform, -0.55625F, -1.203125F, 0.0625F);
            }

            Mk82Entity.setPos((double)worldPosition.x, (double)worldPosition.y, (double)worldPosition.z);
            Mk82Entity.shoot(this.getDeltaMovement().x, this.getDeltaMovement().y, this.getDeltaMovement().z, (float)this.getDeltaMovement().length(), 10.0F);
            player.level().addFreshEntity(Mk82Entity);
            BlockPos pos = BlockPos.containing(new Vec3((double)worldPosition.x, (double)worldPosition.y, (double)worldPosition.z));
            this.level().playSound((Player)null, pos, (SoundEvent)ModSounds.BOMB_RELEASE.get(), SoundSource.PLAYERS, 3.0F, 1.0F);
            if ((Integer)this.getEntityData().get(LOADED_BOMB) == 3) {
                this.reloadCoolDownBomb = 50;
            }

            this.entityData.set(LOADED_BOMB, (Integer)this.getEntityData().get(LOADED_BOMB) - 1);
        }else if (this.getWeaponIndex(0) == 1 && (Integer)this.getEntityData().get(LOADED_MISSILE) > 0) {
            GBU57Entity Mk82Entity = ((GBU57Weapon)this.getWeapon(0)).create(player);
            Vector4f worldPosition;
            if ((Integer)this.getEntityData().get(LOADED_MISSILE) == 3) {
                worldPosition = this.transformPosition(transform, 0.55625F, -1.203125F, 0.0625F);
            } else if ((Integer)this.getEntityData().get(LOADED_MISSILE) == 2) {
                worldPosition = this.transformPosition(transform, 0.0F, -1.203125F, 0.0625F);
            } else {
                worldPosition = this.transformPosition(transform, -0.55625F, -1.203125F, 0.0625F);
            }

            Mk82Entity.setPos((double)worldPosition.x, (double)worldPosition.y, (double)worldPosition.z);
            Mk82Entity.shoot(this.getDeltaMovement().x, this.getDeltaMovement().y, this.getDeltaMovement().z, (float)this.getDeltaMovement().length(), 10.0F);
            player.level().addFreshEntity(Mk82Entity);
            BlockPos pos = BlockPos.containing(new Vec3((double)worldPosition.x, (double)worldPosition.y, (double)worldPosition.z));
            this.level().playSound((Player)null, pos, (SoundEvent)ModSounds.BOMB_RELEASE.get(), SoundSource.PLAYERS, 3.0F, 1.0F);
            if ((Integer)this.getEntityData().get(LOADED_MISSILE) == 3) {
                this.reloadCoolDownMissile = 100;
            }
            this.entityData.set(LOADED_MISSILE, (Integer)this.getEntityData().get(LOADED_MISSILE) - 1);
        }
    }
}
