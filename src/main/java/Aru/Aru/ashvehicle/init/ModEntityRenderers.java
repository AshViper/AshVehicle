package Aru.Aru.ashvehicle.init;

import Aru.Aru.ashvehicle.client.renderer.projectile.*;
import Aru.Aru.ashvehicle.client.renderer.entity.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEntityRenderers {

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        //weapons
        event.registerEntityRenderer(ModEntities.AIM9.get(), Aim9Renderer::new);
        event.registerEntityRenderer(ModEntities.AIM120.get(), Aim120Renderer::new);
        event.registerEntityRenderer(ModEntities.R60.get(), R60Renderer::new);
        event.registerEntityRenderer(ModEntities.AGM114.get(), Agm114Renderer::new);
        event.registerEntityRenderer(ModEntities.AGM158.get(), Agm158Renderer::new);
        event.registerEntityRenderer(ModEntities.BALLISTIC_MISSILE.get(), BallisticMissileRenderer::new);
        event.registerEntityRenderer(ModEntities.TOILETBOMB.get(), ToiletBombRenderer::new);
        event.registerEntityRenderer(ModEntities.CBU87.get(), Cbu87Renderer::new);
        event.registerEntityRenderer(ModEntities.GBU57.get(), Gbu57Renderer::new);
        event.registerEntityRenderer(ModEntities.NUKE_BOMB.get(), NukeBombRenderer::new);

        // Register entity render for tom7
        event.registerEntityRenderer(ModEntities.UH_60.get(), UH60Renderer::new);
        event.registerEntityRenderer(ModEntities.F_16.get(), F16Renderer::new);
        event.registerEntityRenderer(ModEntities.F_15.get(), F15Renderer::new);
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
        event.registerEntityRenderer(ModEntities.MH_60M.get(), MH60MRenderer::new);
        event.registerEntityRenderer(ModEntities.SAPSAN_GRIM2.get(), SapsanRenderer::new);
        event.registerEntityRenderer(ModEntities.F_18.get(), F18Renderer::new);
        event.registerEntityRenderer(ModEntities.F_117.get(), F117Renderer::new);
        event.registerEntityRenderer(ModEntities.ZUMWALT.get(), ZumwaltRenderer::new);
        event.registerEntityRenderer(ModEntities.SU_57.get(), SU57Renderer::new);
        event.registerEntityRenderer(ModEntities.V_22.get(), V22Renderer::new);
        event.registerEntityRenderer(ModEntities.F_2.get(), F2Renderer::new);
        event.registerEntityRenderer(ModEntities.PANTSIR_S1.get(), pantsirS1Renderer::new);
        event.registerEntityRenderer(ModEntities.SU_27.get(), SU27Renderer::new);
        event.registerEntityRenderer(ModEntities.J_20.get(), J20Renderer::new);
        event.registerEntityRenderer(ModEntities.EuroFighter.get(), EuroFighterRenderer::new);
        event.registerEntityRenderer(ModEntities.REAPER.get(), ReaperRenderer::new);
        event.registerEntityRenderer(ModEntities.YF_23.get(), YF23Renderer::new);
        event.registerEntityRenderer(ModEntities.X_47B.get(), X47BRenderer::new);
        event.registerEntityRenderer(ModEntities.RUBBER_BOAT.get(), RubberBoatRenderer::new);
        event.registerEntityRenderer(ModEntities.M_777.get(), m777Renderer::new);
        event.registerEntityRenderer(ModEntities.RAH_66.get(), Rah66Renderer::new);
        event.registerEntityRenderer(ModEntities.AH_64.get(), AH64Renderer::new);
        event.registerEntityRenderer(ModEntities.TOS.get(), TosRenderer::new);
        event.registerEntityRenderer(ModEntities.ZELENSKY.get(), ZelenskyRenderer::new);
    }
}

