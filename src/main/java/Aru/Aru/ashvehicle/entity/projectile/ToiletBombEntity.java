package Aru.Aru.ashvehicle.entity.projectile;

import com.atsuishio.superbwarfare.entity.projectile.DestroyableProjectile;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.tools.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ToiletBombEntity extends DestroyableProjectile implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public ToiletBombEntity(EntityType<? extends ToiletBombEntity> type, Level level) {
        super(type, level);
        this.noCulling = true;
        this.explosionRadius = 22.0F;
        this.explosionDamage = 650.0F;
    }

    public boolean hurt(@NotNull DamageSource source, float amount) {
        Entity entity = source.getDirectEntity();
        if (entity instanceof ToiletBombEntity mk82Entity) {
            if (mk82Entity.getOwner() == this.getOwner()) {
                return false;
            }
        }

        return super.hurt(source, amount);
    }

    protected @NotNull Item getDefaultItem() {
        return (Item)ModItems.MEDIUM_AERIAL_BOMB.get();
    }

    public void onHitBlock(@NotNull BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        if (this.level() instanceof ServerLevel) {
            ProjectileTool.causeCustomExplode(this, this.explosionDamage, this.explosionRadius, 1.2F);
        }

        this.discard();
    }

    public void tick() {
        super.tick();
        if (this.tickCount > 600 || (Float)this.entityData.get(HEALTH) <= 0.0F) {
            if (!this.level().isClientSide) {
                ProjectileTool.causeCustomExplode(this, this.explosionDamage, this.explosionRadius, 1.2F);
            }

            this.discard();
        }

    }

    public void registerControllers(AnimatableManager.ControllerRegistrar data) {}

    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    public @NotNull SoundEvent getSound() {
        return (SoundEvent)ModSounds.SHELL_FLY.get();
    }

    public float getVolume() {
        return 0.7F;
    }

    public boolean shouldSyncMotion() {
        return true;
    }
}