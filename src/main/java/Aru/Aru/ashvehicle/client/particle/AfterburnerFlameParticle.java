package Aru.Aru.ashvehicle.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;

public class AfterburnerFlameParticle extends TextureSheetParticle {
    private final SpriteSet spriteSet;
    public AfterburnerFlameParticle(ClientLevel level, double x, double y, double z,
                                    double dx, double dy, double dz,SpriteSet sprite) {
        super(level, x, y, z, dx, dy, dz);
        this.spriteSet = sprite;
        this.lifetime = 20 + this.random.nextInt(10); // 寿命
        this.gravity = 0.0F;
        this.xd = dx;
        this.yd = dy;
        this.zd = dz;
        this.alpha = 0.9F;
        this.quadSize = 1.5F;
        this.setSpriteFromAge(spriteSet);
    }

    @Override
    public void tick() {
        super.tick();

        // 年齢割合（0.0F ～ 1.0F）
        float ageRatio = (float) this.age / (float) this.lifetime;

        // サイズと透明度を徐々に減らす
        this.quadSize = this.quadSize * (1.0F - ageRatio * 0.7F); // サイズ 30%まで縮小
        this.alpha = 0.9F * (1.0F - ageRatio); // 徐々に透明へ

        this.setSpriteFromAge(spriteSet);
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }
}

