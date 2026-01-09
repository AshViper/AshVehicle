package Aru.Aru.ashvehicle.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;

public class AfterburnerFlameParticle extends TextureSheetParticle {
    private final SpriteSet spriteSet;
    private final float initialScale;

    public AfterburnerFlameParticle(ClientLevel level, double x, double y, double z,
                                    double dx, double dy, double dz, SpriteSet sprite) {
        super(level, x, y, z, dx, dy, dz);
        this.spriteSet = sprite;
        this.lifetime = 15 + this.random.nextInt(8); // Более короткая жизнь для динамичности
        this.gravity = -0.02F; // Легкий подъем вверх
        
        // Добавляем случайное отклонение для более реалистичного эффекта
        this.xd = dx + (this.random.nextDouble() - 0.5) * 0.05;
        this.yd = dy + (this.random.nextDouble() - 0.5) * 0.05;
        this.zd = dz + (this.random.nextDouble() - 0.5) * 0.05;
        
        this.alpha = 0.95F;
        this.initialScale = 1.8F + this.random.nextFloat() * 0.4F; // Случайный начальный размер
        this.quadSize = this.initialScale;
        
        // Цвет пламени: от ярко-оранжевого к желтому
        this.rCol = 1.0F;
        this.gCol = 0.7F + this.random.nextFloat() * 0.2F;
        this.bCol = 0.3F + this.random.nextFloat() * 0.2F;
        
        this.setSpriteFromAge(spriteSet);
    }

    @Override
    public void tick() {
        super.tick();

        float ageRatio = (float) this.age / (float) this.lifetime;

        // Плавное увеличение размера в начале, затем уменьшение
        if (ageRatio < 0.3F) {
            this.quadSize = this.initialScale * (1.0F + ageRatio * 0.5F);
        } else {
            this.quadSize = this.initialScale * (1.15F - (ageRatio - 0.3F) * 1.2F);
        }

        // Плавное затухание с более быстрым исчезновением в конце
        if (ageRatio < 0.7F) {
            this.alpha = 0.95F * (1.0F - ageRatio * 0.5F);
        } else {
            this.alpha = 0.95F * (1.0F - ageRatio) * 0.5F;
        }

        // Изменение цвета: от оранжевого к красному
        this.gCol = (0.7F + this.random.nextFloat() * 0.2F) * (1.0F - ageRatio * 0.5F);
        this.bCol = (0.3F + this.random.nextFloat() * 0.2F) * (1.0F - ageRatio * 0.7F);

        // Замедление движения
        this.xd *= 0.95;
        this.yd *= 0.95;
        this.zd *= 0.95;

        this.setSpriteFromAge(spriteSet);
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    /** ✨ Максимальная яркость для эффекта свечения */
    @Override
    public int getLightColor(float partialTick) {
        // Динамическая яркость в зависимости от возраста частицы
        float ageRatio = ((float) this.age + partialTick) / (float) this.lifetime;
        int brightness = (int) (15.0F * (1.0F - ageRatio * 0.5F));
        brightness = Math.max(brightness, 0);
        return brightness << 20 | brightness << 4;
    }
}
