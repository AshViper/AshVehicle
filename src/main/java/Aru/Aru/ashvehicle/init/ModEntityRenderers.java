package Aru.Aru.ashvehicle.init;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import Aru.Aru.ashvehicle.client.renderer.entity.*;
import Aru.Aru.ashvehicle.client.renderer.weapon.*;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEntityRenderers {

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {

        // Register entity render for tom7
        event.registerEntityRenderer(ModEntities.TOM_7.get(), Tom7Renderer::new);
        event.registerEntityRenderer(ModEntities.UH_60.get(), UH60Renderer::new);
        event.registerEntityRenderer(ModEntities.F_16.get(), F16Renderer::new);
        event.registerEntityRenderer(ModEntities.MIG_15.get(), Mig15Renderer::new);
        event.registerEntityRenderer(ModEntities.F_4.get(), F4Renderer::new);
        event.registerEntityRenderer(ModEntities.MIG_29.get(), Mig29Renderer::new);
        event.registerEntityRenderer(ModEntities.T_90.get(), T90Renderer::new);
        event.registerEntityRenderer(ModEntities.M1A1_ABRAMS.get(), M1A1AbramsRenderer::new);
        event.registerEntityRenderer(ModEntities.KV_2.get(), KV2Renderer::new);
        event.registerEntityRenderer(ModEntities.GEPARD_1A2.get(), GepardRenderer::new);
        event.registerEntityRenderer(ModEntities.SU_33.get(), SU33Renderer::new);
        event.registerEntityRenderer(ModEntities.M3A3_BRADLEY.get(), M3A3BradleyRenderer::new);
        event.registerEntityRenderer(ModEntities.SU_25.get(), SU25Renderer::new);
        event.registerEntityRenderer(ModEntities.F_39E.get(), F39ERenderer::new);
        event.registerEntityRenderer(ModEntities.SU_34.get(), SU34Renderer::new);
        event.registerEntityRenderer(ModEntities.F_35.get(),F35Renderer::new);
        event.registerEntityRenderer(ModEntities.B_2.get(),B2Renderer::new);
        event.registerEntityRenderer(ModEntities.F_22.get(),F22Renderer::new);
        event.registerEntityRenderer(ModEntities.AAM_4.get(), Aam4Renderer::new);
        event.registerEntityRenderer(ModEntities.GBU_57.get(), GBU57Renderer::new);
        event.registerEntityRenderer(ModEntities.NUCLEAR_BOMB.get(), NuclearBomRenderer::new);
        event.registerEntityRenderer(ModEntities.MH_60M.get(), MH60MRenderer::new);
        event.registerEntityRenderer(ModEntities.SAPSAN_GRIM2.get(), SapsanRenderer::new);
        event.registerEntityRenderer(ModEntities.F_18.get(), F18Renderer::new);
        event.registerEntityRenderer(ModEntities.F_117.get(), F117Renderer::new);
        event.registerEntityRenderer(ModEntities.ZYNWALT.get(), ZumwaltRenderer::new);
        event.registerEntityRenderer(ModEntities.SU_57.get(), SU57Renderer::new);
        event.registerEntityRenderer(ModEntities.JASSM_XR.get(), JassmXRRenderer::new);
        event.registerEntityRenderer(ModEntities.BALLISTIC_MISSILE.get(), BallisticMissileRenderer::new);
        event.registerEntityRenderer(ModEntities.NAPALM_BOMB.get(), NapalmBombRenderer::new);
        event.registerEntityRenderer(ModEntities.TOMAHAWK.get(), TomahawkRenderer::new);
        event.registerEntityRenderer(ModEntities.V_22.get(), V22Renderer::new);
    }
}

