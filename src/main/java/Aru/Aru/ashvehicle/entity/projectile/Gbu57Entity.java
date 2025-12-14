package Aru.Aru.ashvehicle.entity.projectile;

import com.atsuishio.superbwarfare.entity.projectile.DestroyableProjectile;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.tools.ProjectileTool;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class Gbu57Entity extends DestroyableProjectile implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean penetrating = false;
    private int penetrateDistance = 0;
    private static final int MAX_PENETRATE = 60;

    public Gbu57Entity(EntityType<? extends Gbu57Entity> type, Level level) {
        super(type, level);
        this.noCulling = true;
        this.explosionRadius = 22.0F;
        this.explosionDamage = 650.0F;
    }

    public boolean hurt(@NotNull DamageSource source, float amount) {
        Entity entity = source.getDirectEntity();
        if (entity instanceof Gbu57Entity mk82Entity) {
            if (mk82Entity.getOwner() == this.getOwner()) {
                return false;
            }
        }

        return super.hurt(source, amount);
    }

    protected @NotNull Item getDefaultItem() {
        return (Item)ModItems.MEDIUM_AERIAL_BOMB.get();
    }

    protected void onHitBlock(@NotNull BlockHitResult hit) {
        super.onHitBlock(hit);

        if (this.level().isClientSide) return;

        // 着弾後も消えない
        this.penetrating = true;

        // 初期減速（装甲貫通感）
        this.setDeltaMovement(this.getDeltaMovement().scale(0.3));
    }


    public void tick() {
        super.tick();

        if (!(this.level() instanceof ServerLevel serverLevel)) return;

        /* ===== 貫通していない通常状態 ===== */
        if (!penetrating) {
            // 空中で600tick超えたら安全装置爆発
            if (this.tickCount > 600) {
                ProjectileTool.causeCustomExplode(
                        this,
                        this.explosionDamage,
                        this.explosionRadius,
                        1.2F
                );
                this.discard();
            }
            return;
        }

        /* ===== GBU-57 地中貫通フェーズ ===== */
        Vec3 direction = this.getDeltaMovement().normalize();

        // 前進（1ブロックずつ）
        Vec3 nextPos = this.position().add(direction);
        this.setPos(nextPos.x, nextPos.y, nextPos.z);

        BlockPos pos = this.blockPosition();

        if (!serverLevel.isEmptyBlock(pos)) {
            float hardness = serverLevel.getBlockState(pos)
                    .getDestroySpeed(serverLevel, pos);

            // 破壊不可（岩盤など）で即爆発
            if (hardness < 0) {
                explodeAndDiscard();
                return;
            }

            // ブロック破壊
            serverLevel.destroyBlock(pos, false, this);

            // 硬いほど減速（GBU感）
            double slow = Math.max(0.5, 1.0 - hardness * 0.08);
            this.setDeltaMovement(this.getDeltaMovement().scale(slow));
        }

        penetrateDistance++;

        // 貫通距離 or 速度低下で地下爆発
        if (penetrateDistance >= MAX_PENETRATE
                || this.getDeltaMovement().length() < 0.1
                || (Float)this.entityData.get(HEALTH) <= 0.0F) {

            ProjectileTool.causeCustomExplode(
                    this,
                    this.explosionDamage * 2.0F,   // 地下爆発強化
                    this.explosionRadius * 1.5F,
                    1.2F
            );

            this.discard();
        }
    }

    private void explodeAndDiscard() {
        ProjectileTool.causeCustomExplode(
                this,
                this.explosionDamage * 2.0F,
                this.explosionRadius * 1.5F,
                1.2F
        );
        this.discard();
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