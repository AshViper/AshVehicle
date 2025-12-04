package Aru.Aru.ashvehicle.entity.vehicle;

import Aru.Aru.ashvehicle.entity.projectile.BallisticMissileEntity;
import Aru.Aru.ashvehicle.init.CoordinateTargetVehicle;
import com.atsuishio.superbwarfare.entity.vehicle.base.GeoVehicleEntity;
import com.atsuishio.superbwarfare.tools.ParticleTool;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4d;
import org.joml.Vector4d;

public class SapsanEntity extends GeoVehicleEntity implements CoordinateTargetVehicle {
    
    private boolean shotToggled = false;
    
    private static final EntityDataAccessor<Float> POD_ROT = SynchedEntityData.defineId(SapsanEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> POD_TOGGLED = SynchedEntityData.defineId(SapsanEntity.class, EntityDataSerializers.BOOLEAN);

    public SapsanEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public int getId() {
        return super.getId();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(POD_ROT, 0.0F);
        this.entityData.define(POD_TOGGLED, false);
    }

    public void setPodRot(float value) {
        this.entityData.set(POD_ROT, value);
    }

    public float getPodRot() {
        return this.entityData.get(POD_ROT);
    }

    public void setPodToggled(boolean value) {
        this.entityData.set(POD_TOGGLED, value);
    }

    public boolean getPodToggled() {
        return this.entityData.get(POD_TOGGLED);
    }
    
    // Toggle pod from external call (packet from client)
    public void togglePod() {
        setPodToggled(!getPodToggled());
    }

    @Override
    public void baseTick() {
        super.baseTick();
        
        // Pod rotation animation
        float target = this.getPodToggled() ? 90.0F : 0.0F;
        float current = getPodRot();
        float speed = 2.5F;

        if (Math.abs(current - target) <= speed) {
            setPodRot(target);
        } else if (this.getPodToggled() && current < 90.0F) {
            setPodRot(current + speed);
        } else if (!this.getPodToggled() && current > 0.0F) {
            setPodRot(current - speed);
        }
    }

    public void shootMissileTo(Player player, Vec3 targetPos) {
        if (!getPodToggled()) return;
        if (this.level().isClientSide()) return;

        Matrix4d transform = this.getVehicleTransform(1.0f);

        float x = shotToggled ? -1F : 1F;
        float y = 1.0F;
        float z = -10F;
        this.shotToggled = !this.shotToggled;

        Vector4d worldPosition = transformPos(transform, x, y, z);

        BallisticMissileEntity missile = new BallisticMissileEntity(player, this.level());
        missile.setPos(worldPosition.x, worldPosition.y, worldPosition.z);
        missile.setXRot(90);
        missile.setYRot(180);
        missile.setTargetPosition(targetPos);

        this.level().addFreshEntity(missile);

        if (this.level() instanceof ServerLevel serverLevel) {
            ParticleTool.sendParticle(serverLevel, ParticleTypes.LARGE_SMOKE,
                    worldPosition.x, worldPosition.y, worldPosition.z,
                    10, 0.1, 0.1, 0.1, 0.0, false);
        }
    }

    private Vector4d transformPos(Matrix4d transform, double x, double y, double z) {
        Vector4d vec = new Vector4d(x, y, z, 1.0);
        transform.transform(vec);
        return vec;
    }
}
