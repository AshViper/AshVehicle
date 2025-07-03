package tech.lq0.ashvehicle.entity;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.config.server.VehicleConfig;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.CannonShellWeapon;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.VehicleWeapon;
import com.atsuishio.superbwarfare.init.ModItems;
import com.mojang.math.Axis;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PlayMessages;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import tech.lq0.ashvehicle.entity.Class.BaseTankEntity;
import tech.lq0.ashvehicle.init.ModEntities;
import tech.lq0.ashvehicle.init.ModSounds;

public class KV2Entity extends BaseTankEntity {
    public KV2Entity(EntityType<?> type, Level world) {
        super(type, world);
    }
    public KV2Entity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this(ModEntities.KV_2.get(), level);
    }
    @Override
    public Matrix4f getTurretTransform(float ticks) {
        Matrix4f transformV = this.getVehicleTransform(ticks);
        Matrix4f transform = new Matrix4f();
        Vector4f worldPosition = this.transformPosition(transform, 0.0F, 2.1059375F, 1.0F);
        transformV.translate(worldPosition.x, worldPosition.y, worldPosition.z);
        transformV.rotate(Axis.YP.rotationDegrees(Mth.lerp(ticks, this.turretYRotO, this.getTurretYRot())));
        return transformV;
    }
    @Override
    public VehicleWeapon[][] initWeapons() {
        return new VehicleWeapon[][]{{
                (new CannonShellWeapon()).hitDamage((float)(Integer) VehicleConfig.YX_100_AP_CANNON_DAMAGE.get())
                        .explosionRadius(((Double)VehicleConfig.YX_100_AP_CANNON_EXPLOSION_RADIUS.get()).floatValue())
                        .explosionDamage((float)(Integer)VehicleConfig.YX_100_AP_CANNON_EXPLOSION_DAMAGE.get())
                        .fireProbability(0.0F)
                        .fireTime(0)
                        .durability(100)
                        .velocity(40.0F)
                        .gravity(0.1F)
                        .sound((SoundEvent) com.atsuishio.superbwarfare.init.ModSounds.INTO_MISSILE.get()).ammo((Item) ModItems.AP_5_INCHES.get())
                        .icon(Mod.loc("textures/screens/vehicle_weapon/ap_shell.png"))
                        .sound1p(ModSounds.M1A1_ABRAMS_FIRE.get())
                        .sound3p(ModSounds.M1A1_ABRAMS_FIRE.get())
                        .sound3pFar(ModSounds.YX_100_VERY_FAR.get())
                        .sound3pVeryFar(ModSounds.YX_100_VERY_FAR.get())

        }};
    }
    @Override
    public VehicleWeapon getWeapon(int index) {
        VehicleWeapon[][] weapons = this.initWeapons(); // 1行複数武装（1Dとみなす）
        return weapons[0][0];
    }
    @Override
    public void positionRider(@NotNull Entity passenger, @NotNull Entity.@NotNull MoveFunction callback) {
        if (this.hasPassenger(passenger)) {
            Matrix4f transform = this.getTurretTransform(1.0F);
            int i = this.getOrderedPassengers().indexOf(passenger);
            Vector4f var10000;
            switch (i) {
                case 0 -> var10000 = this.transformPosition(transform, 0.6F, 0.07F, 1.6F);
                case 1 -> var10000 = this.transformPosition(transform, -0.7580562F, 0.3F, -0.57275623F);
                case 2 -> var10000 = this.transformPosition(transform, 0.86219376F, 0.07F, -0.5696875F);
                default -> throw new IllegalStateException("Unexpected value: " + i);
            }

            Vector4f worldPosition = var10000;
            passenger.setPos((double)worldPosition.x, (double)worldPosition.y, (double)worldPosition.z);
            callback.accept(passenger, (double)worldPosition.x, (double)worldPosition.y, (double)worldPosition.z);
            this.copyEntityData(passenger);
        }
    }
}
