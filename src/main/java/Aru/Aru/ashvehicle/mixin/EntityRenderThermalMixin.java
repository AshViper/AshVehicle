package Aru.Aru.ashvehicle.mixin;

import Aru.Aru.ashvehicle.client.renderer.ThermalBufferSource;
import Aru.Aru.ashvehicle.client.renderer.ThermalShaderManager;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * サーマルビジョン用 Mixin:
 * エンティティ描画時に、サーマルモードが有効なら
 * MultiBufferSource を ThermalBufferSource（白色強制ラッパー）に差し替えます。
 */
@Mixin(EntityRenderDispatcher.class)
public class EntityRenderThermalMixin {

    /**
     * render メソッドの MultiBufferSource パラメータを差し替える。
     * サーマルが有効なら全てのエンティティを白色化する。
     */
    @ModifyArg(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;render(Lnet/minecraft/world/entity/Entity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"
            ),
            index = 4 // MultiBufferSource の位置
    )
    private MultiBufferSource ashvehicle$wrapBufferSource(MultiBufferSource original) {
        if (ThermalShaderManager.isEnabled()) {
            return new ThermalBufferSource(original);
        }
        return original;
    }
}
