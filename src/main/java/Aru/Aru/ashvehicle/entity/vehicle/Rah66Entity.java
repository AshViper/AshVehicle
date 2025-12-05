package Aru.Aru.ashvehicle.entity.vehicle;

import com.atsuishio.superbwarfare.entity.vehicle.base.GeoVehicleEntity;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class Rah66Entity extends GeoVehicleEntity {
    
    private static final EntityDataAccessor<Float> WEAPON_BAY_ROT = SynchedEntityData.defineId(Rah66Entity.class, EntityDataSerializers.FLOAT);
    
    public float weaponBayRotO = 0f;

    public Rah66Entity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(WEAPON_BAY_ROT, 0.0F);
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
        
        weaponBayRotO = getWeaponBayRot();
        
        // Check if driver (seat 0) or gunner (seat 1) has weapons that need open bay
        // Seat 0 weapons: 0=CloseBay, 1=Rocket, 2=DriverMissile, 3=DriverAAMissile
        // Seat 1 weapons: 0=CloseBay, 1=Cannon, 2=PassengerMissile, 3=SeekMissile
        // Bay opens when weapon index > 0 (anything except CloseBay)
        int driverWeapon = this.getWeaponIndex(0);
        int gunnerWeapon = this.getWeaponIndex(1);
        boolean driverNeedsBay = driverWeapon >= 1; // All weapons except CloseBay
        boolean gunnerNeedsBay = gunnerWeapon >= 2; // PassengerMissile and SeekMissile (not Cannon)
        boolean shouldOpen = driverNeedsBay || gunnerNeedsBay;
        
        float target = shouldOpen ? 90.0F : 0.0F;
        float current = getWeaponBayRot();
        float diff = target - current;
        
        // Smooth animation
        float newRot = current + diff * 0.1f;
        
        if (Math.abs(diff) < 0.5f) {
            newRot = target;
        }
        
        setWeaponBayRot(newRot);
    }
}
