package Aru.Aru.ashvehicle.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import com.mojang.math.Axis;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import Aru.Aru.ashvehicle.ExtensionTest;
import Aru.Aru.ashvehicle.client.model.M1A1AbramsModel;
import Aru.Aru.ashvehicle.entity.M1A1AbramsEntity;

public class M1A1AbramsRenderer extends GeoEntityRenderer<M1A1AbramsEntity> {

    // Добавляем константы для настройки гусениц
    private static final float TRACK_SCALE_Y = 0.9f; // по вертикали
    private static final float TRACK_SCALE_Z = 1.15f; //  по горизонтали
    private static final float TRACK_OFFSET_Y = -1.8f; // вниз
    // Разные смещения для левой и правой гусеницы
    private static final float LEFT_TRACK_OFFSET_Z = 2.0f; // назад //12
    private static final float RIGHT_TRACK_OFFSET_Z = 2.0f; // назад //12
    private static final float LEFT_TRACK_OFFSET_X = 4.5f; // влево
    private static final float RIGHT_TRACK_OFFSET_X = -4.5f; // вправо

    public M1A1AbramsRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new M1A1AbramsModel());
        this.shadowRadius = 0.5f;
    }

    @Override
    public ResourceLocation getTextureLocation(M1A1AbramsEntity animatable) {
        return new ResourceLocation(ExtensionTest.MODID, "textures/entity/m1a1abrams.png");
    }

    @Override
    public RenderType getRenderType(M1A1AbramsEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutoutNoCull(texture);
    }

    @Override
    public void preRender(PoseStack poseStack, M1A1AbramsEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green,
                          float blue, float alpha) {
        float scale = 1f;
        this.scaleHeight = scale;
        this.scaleWidth = scale;
        super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void render(M1A1AbramsEntity entityIn, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(-Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot())));
        poseStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot())));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entityIn.prevRoll, entityIn.getRoll())));
        super.render(entityIn, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
        poseStack.popPose();
    }

    @Override
    public void renderRecursively(PoseStack poseStack, M1A1AbramsEntity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        String name = bone.getName();
        
        // Поддержка вращения всех колес независимо
        for (int i = 1; i <= 11; i++) {
            if (name.equals("wheelL" + i)) {
                bone.setRotX(1.5f * Mth.lerp(partialTick, animatable.leftWheelRotO, animatable.getLeftWheelRot()));
            }
            if (name.equals("wheelR" + i)) {
                bone.setRotX(1.5f * Mth.lerp(partialTick, animatable.rightWheelRotO, animatable.getRightWheelRot()));
            }
        }

        if (name.equals("turret")) {
            bone.setRotY(Mth.lerp(partialTick, animatable.turretYRotO, animatable.getTurretYRot()) * Mth.DEG_TO_RAD);
        }

        if (name.equals("barrel")) {
            float a = animatable.getTurretYaw(partialTick);
            float r = (Mth.abs(a) - 90f) / 90f;

            float r2;

            if (Mth.abs(a) <= 90f) {
                r2 = a / 90f;
            } else {
                if (a < 0) {
                    r2 = - (180f + a) / 90f;
                } else {
                    r2 = (180f - a) / 90f;
                }
            }

            bone.setRotX(
                    Mth.lerp(partialTick, animatable.turretXRotO, animatable.getTurretXRot()) * Mth.DEG_TO_RAD
                            - r * animatable.getPitch(partialTick) * Mth.DEG_TO_RAD
                            - r2 * animatable.getRoll(partialTick) * Mth.DEG_TO_RAD
            );
        }

        if (name.equals("HMG")) {
            bone.setRotY(Mth.lerp(partialTick, animatable.gunYRotO, animatable.getGunYRot()) * Mth.DEG_TO_RAD - Mth.lerp(partialTick, animatable.turretYRotO, animatable.getTurretYRot()) * Mth.DEG_TO_RAD);
        }
        
        if (name.equals("qiangguan")) {
            float a = animatable.getTurretYaw(partialTick);
            float r = (Mth.abs(a) - 90f) / 90f;

            float r2;

            if (Mth.abs(a) <= 90f) {
                r2 = a / 90f;
            } else {
                if (a < 0) {
                    r2 = - (180f + a) / 90f;
                } else {
                    r2 = (180f - a) / 90f;
                }
            }

            bone.setRotX(Mth.clamp(
                    -Mth.lerp(partialTick, animatable.gunXRotO, animatable.getGunXRot()) * Mth.DEG_TO_RAD
                    - r * animatable.getPitch(partialTick) * Mth.DEG_TO_RAD
                    - r2 * animatable.getRoll(partialTick) * Mth.DEG_TO_RAD,
                    -10 * Mth.DEG_TO_RAD, 60 * Mth.DEG_TO_RAD)
            );
        }

        if (name.equals("flare")) {
            bone.setRotZ((float) (0.5 * (Math.random() - 0.5)));
        }
        if (name.equals("flare2")) {
            bone.setRotZ((float) (0.5 * (Math.random() - 0.5)));
        }

        if (name.equals("base")) {
            float a = animatable.getTurretYaw(partialTick);
            float r = (Mth.abs(a) - 90f) / 90f;

            bone.setPosZ(r * Mth.lerp(partialTick, (float) animatable.recoilShakeO, (float) animatable.getRecoilShake()) * 1.75f);
            bone.setRotX(r * Mth.lerp(partialTick, (float) animatable.recoilShakeO, (float) animatable.getRecoilShake()) * Mth.DEG_TO_RAD);

            float r2;

            if (Mth.abs(a) <= 90f) {
                r2 = a / 90f;
            } else {
                if (a < 0) {
                    r2 = - (180f + a) / 90f;
                } else {
                    r2 = (180f - a) / 90f;
                }
            }

            bone.setPosX(r2 * Mth.lerp(partialTick, (float) animatable.recoilShakeO, (float) animatable.getRecoilShake()) * 1f);
            bone.setRotZ(r2 * Mth.lerp(partialTick, (float) animatable.recoilShakeO, (float) animatable.getRecoilShake()) * Mth.DEG_TO_RAD * 1.5f);
        }

        if (name.equals("root")) {
            // Отключаем скрытие корневой кости, чтобы видеть модель танка в режиме от первого лица
            // Было: bone.setHidden(isZoomActive);
            // Теперь не скрываем корень модели
            bone.setHidden(false);
        }

        // Скрываем только некоторые внутренние части, которые могли бы мешать обзору
        if (name.equals("turret_int") || name.equals("roof_int")) {
            Player player = Minecraft.getInstance().player;
            boolean isZoomActive = animatable.getPassengers().contains(player) && player != null && Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON;
            bone.setHidden(isZoomActive);
        }

        // Обработка для всех костей гусениц, содержащих 'gus'
        if (name.toLowerCase().contains("gus")) {
            // Прямое позиционирование
            bone.setPosY(TRACK_OFFSET_Y);
            
            // Разное смещение для левых и правых траков
            if (name.equals("gus") || name.equals("gus2")) {
                bone.setPosZ(LEFT_TRACK_OFFSET_Z);
                bone.setPosX(bone.getPosX() + LEFT_TRACK_OFFSET_X);
            } else if (name.equals("gus38")) {
                bone.setPosZ(RIGHT_TRACK_OFFSET_Z);
                bone.setPosX(bone.getPosX() + RIGHT_TRACK_OFFSET_X);
            } else {
                // Для других костей с 'gus' в имени используем левые смещения по умолчанию
                bone.setPosZ(LEFT_TRACK_OFFSET_Z);
            }
            
            // Явно устанавливаем масштаб
            bone.setScaleX(2.0f);
            bone.setScaleY(TRACK_SCALE_Y);
            bone.setScaleZ(TRACK_SCALE_Z);
        }
        
        // Гусеницы
        for (int i = 0; i < 51; i++) {
            float tO = animatable.leftTrackO + 2 * i;
            float t = animatable.getLeftTrack() + 2 * i;

            while (t >= 100) {
                t -= 100;
            }
            while (t <= 0) {
                t += 100;
            }
            while (tO >= 100) {
                tO -= 100;
            }
            while (tO <= 0) {
                tO += 100;
            }

            float tO2 = animatable.rightTrackO + 2 * i;
            float t2 = animatable.getRightTrack() + 2 * i;

            while (t2 >= 100) {
                t2 -= 100;
            }
            while (t2 <= 0) {
                t2 += 100;
            }
            while (tO2 >= 100) {
                tO2 -= 100;
            }
            while (tO2 <= 0) {
                tO2 += 100;
            }

            if (name.equals("trackL" + i) || (i == 0 && (name.equals("gus") || name.equals("gus2")))) {
                // Если у нас есть направляющая кость для левого трака
                float offsetY = getBoneMoveY(t) * TRACK_SCALE_Y + TRACK_OFFSET_Y;
                float offsetZ = getBoneMoveZ(t) * TRACK_SCALE_Z + LEFT_TRACK_OFFSET_Z;
                
                // Добавляем смещение относительно направляющей кости
                bone.setPosX(LEFT_TRACK_OFFSET_X);
                bone.setPosY(offsetY);
                bone.setPosZ(offsetZ);
            }

            if (name.equals("trackR" + i) || (i == 0 && name.equals("gus38"))) {
                // Если у нас есть направляющая кость для правого трака
                float offsetY = getBoneMoveY(t2) * TRACK_SCALE_Y + TRACK_OFFSET_Y;
                float offsetZ = getBoneMoveZ(t2) * TRACK_SCALE_Z + RIGHT_TRACK_OFFSET_Z;
                
                // Добавляем смещение относительно направляющей кости
                bone.setPosX(RIGHT_TRACK_OFFSET_X);
                bone.setPosY(offsetY);
                bone.setPosZ(offsetZ);
            }

            if (name.equals("trackLRot" + i) || (i == 0 && (name.equals("gusRot") || name.equals("gus2Rot")))) {
                bone.setRotX(-getBoneRotX(t) * Mth.DEG_TO_RAD);
            }

            if (name.equals("trackRRot" + i) || (i == 0 && name.equals("gus38Rot"))) {
                bone.setRotX(-getBoneRotX(t2) * Mth.DEG_TO_RAD);
            }
        }
        
        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    // Методы для анимации гусениц
    public float getBoneRotX(float t) {
        if (t <= 37.6667) return 0F;
        if (t <= 38.5833) return Mth.lerp((t - 37.6667F) / (38.5833F - 37.6667F), 0F, -45F);
        if (t <= 39.75) return -45F;
        if (t <= 40.6667) return Mth.lerp((t - 39.75F) / (40.6667F - 39.75F), -45F, -90F);
        if (t <= 41.6667) return -90F;
        if (t <= 42.5) return -90F;
        if (t <= 43.5) return Mth.lerp(t - 42.5F, -90F, -135F);
        if (t <= 44.5833) return -135F;
        if (t <= 45.0833) return Mth.lerp((t - 44.5833F) / (45.0833F - 44.5833F), -135F, -150F);
        if (t <= 52.25) return -150F;
        if (t <= 52.75) return Mth.lerp((t - 52.25F) / (52.75F - 52.25F), -150F, -180F);
        if (t <= 84.3333) return -180F;
        if (t <= 84.9167) return Mth.lerp((t - 84.3333F) / (84.9167F - 84.3333F), -180F, -210F);
        if (t <= 92.5833) return -210F;
        if (t <= 93.4167) return Mth.lerp((t - 92.5833F) / (93.4167F - 92.5833F), -210F, -220F);
        if (t <= 94.25) return -220F;
        if (t <= 94.9167) return Mth.lerp((t - 94.25F) / (94.9167F - 94.25F), -220F, -243.33F);
        if (t <= 95.75) return Mth.lerp((t - 94.9167F) / (95.75F - 94.9167F), -243.33F, -270F);
        if (t <= 96.8333) return -270F;
        if (t <= 97.5833) return Mth.lerp((t - 96.8333F) / (97.5833F - 96.8333F), -270F, -315F);
        if (t <= 98.8333) return -315F;
        if (t <= 99.5833) return Mth.lerp((t - 98.8333F) / (99.5833F - 98.8333F), -315F, -360F);

        return 0F;
    }

    public float getBoneMoveY(float t) {
        if (t <= 37.6667) return 0F;
        if (t <= 38.5833) return Mth.lerp((t - 37.6667F) / (38.5833F - 37.6667F), 0F, -1.8F);
        if (t <= 40.3333) return Mth.lerp((t - 38.5833F) / (40.3333F - 38.5833F), -1.8F, -4.1F);
        if (t <= 42.9167) return Mth.lerp((t - 40.3333F) / (42.9167F - 40.3333F), -4.1F, -10.3F);
        if (t <= 44.25) return Mth.lerp((t - 42.9167F) / (44.25F - 42.9167F), -10.3F, -12.9F);
        if (t <= 52.4167) return Mth.lerp((t - 44.25F) / (52.4167F - 44.25F), -12.9F, -23.96F);
        if (t <= 84.5833) return -23.96F;
        if (t <= 93) return Mth.lerp((t - 84.5833F) / (93F - 84.5833F), -23.96F, -12.93F);
        if (t <= 95.25) return Mth.lerp((t - 93F) / (95.25F - 93F), -12.93F, -10.085F);
        if (t <= 97.5) return Mth.lerp((t - 95.25F) / (97.5F - 95.25F), -10.085F, -4.585F);
        if (t <= 98.8333) return Mth.lerp((t - 97.5F) / (98.8333F - 97.5F), -4.585F, -1.165F);
        if (t <= 99.25) return Mth.lerp((t - 98.8333F) / (99.25F - 98.8333F), -1.165F, -0.25F);

        return Mth.lerp((t - 99.25F) / (100F - 99.25F), -0.25F, 0F);
    }

    public float getBoneMoveZ(float t) {
        if (t <= 37.6667) return Mth.lerp(t / (37.6667F - 0F), 0F, 111.6F);
        if (t <= 38.5833) return Mth.lerp((t - 37.6667F) / (38.5833F - 37.6667F), 111.6F, 113.25F);
        if (t <= 40.3333) return Mth.lerp((t - 38.5833F) / (40.3333F - 38.5833F), 113.25F, 116F);
        if (t <= 42.9167) return 116F;
        if (t <= 44.25) return Mth.lerp((t - 42.9167F) / (44.25F - 42.9167F), 116F, 113.5F);
        if (t <= 52.4167) return Mth.lerp((t - 44.25F) / (52.4167F - 44.25F), 113.5F, 96.25F);
        if (t <= 84.5833) return Mth.lerp((t - 52.4167F) / (84.5833F - 52.4167F), 96.25F, 14.095F);
        if (t <= 93) return Mth.lerp((t - 84.5833F) / (93F - 84.5833F), 14.095F, -3.565F);
        if (t <= 95.25) return Mth.lerp((t - 93F) / (95.25F - 93F), -3.565F, -6.35F);
        if (t <= 97.5) return Mth.lerp((t - 95.25F) / (97.5F - 95.25F), -6.35F, -6.39F);
        if (t <= 98.8333) return Mth.lerp((t - 97.5F) / (98.8333F - 97.5F), -6.39F, -3.03F);
        if (t <= 99.25) return Mth.lerp((t - 98.8333F) / (99.25F - 98.8333F), -3.03F, -1.95F);

        return Mth.lerp((t - 99.25F) / (100F - 99.25F), -1.95F, 0F);
    }
} 