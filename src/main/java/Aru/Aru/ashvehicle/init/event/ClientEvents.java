package Aru.Aru.ashvehicle.init.event;

import Aru.Aru.ashvehicle.AshVehicle;
import Aru.Aru.ashvehicle.Packet.TogglePodPacket;
import Aru.Aru.ashvehicle.client.renderer.ThermalShaderManager;
import Aru.Aru.ashvehicle.client.screen.CoordinateInputScreen;
import Aru.Aru.ashvehicle.entity.vehicle.*;
import Aru.Aru.ashvehicle.init.CoordinateTargetVehicle;
import Aru.Aru.ashvehicle.init.ModNetwork;
import Aru.Aru.ashvehicle.init.client.ClientKeyMappings;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = AshVehicle.MODID, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null || mc.player == null) return;

        Entity vehicle = mc.player.getVehicle();
        
        // Open coordinate screen
        if (ClientKeyMappings.OPEN_COORDINATE_SCREEN != null && ClientKeyMappings.OPEN_COORDINATE_SCREEN.consumeClick()) {
            if (vehicle instanceof CoordinateTargetVehicle targetable) {
                mc.setScreen(new CoordinateInputScreen(targetable));
            }
        }
        
        // Toggle pod (Ctrl key)
        if (ClientKeyMappings.TOGGLE_POD != null && ClientKeyMappings.TOGGLE_POD.consumeClick()) {
            if (vehicle instanceof SapsanEntity sapsan) {
                ModNetwork.INSTANCE.send(PacketDistributor.SERVER.noArg(),
                        new TogglePodPacket(sapsan.getId()));
            }
        }

        // thermal vition (Ctrl key)
        if (ClientKeyMappings.THERMAL_VISION != null
                && ClientKeyMappings.THERMAL_VISION.consumeClick()) {

            if (vehicle instanceof F18Entity) {
                if (ThermalShaderManager.isEnabled()) {
                    ThermalShaderManager.disable();
                } else {
                    ThermalShaderManager.enable();
                }
            }
        }

        if (ThermalShaderManager.isEnabled()) {
            if (!(vehicle instanceof F18Entity)) {
                ThermalShaderManager.disable();
            }
        }

        // Toggle VTOL mode (V key)
        if (ClientKeyMappings.VTOL_TOGGLE != null
                && ClientKeyMappings.VTOL_TOGGLE.consumeClick()) {

            if (vehicle instanceof F35Entity f35) {
                f35.toggleVtolMode();
            }
            if (vehicle instanceof V22Entity v22) {
                v22.toggleVtolMode();
            }
            if (vehicle instanceof F14Entity f14) {
                f14.toggleVtolMode();
            }
        }
    }
}
