package Aru.Aru.ashvehicle.init;

import Aru.Aru.ashvehicle.entity.vehicle.ZumwaltEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import Aru.Aru.ashvehicle.AshVehicle;
import Aru.Aru.ashvehicle.client.screen.CoordinateInputScreen;
import Aru.Aru.ashvehicle.entity.vehicle.SapsanEntity;

@Mod.EventBusSubscriber(modid = AshVehicle.MODID, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null || mc.player == null) return;

        // カスタムキーが押された瞬間だけ反応
        if (ClientKeyMappings.OPEN_COORDINATE_SCREEN.consumeClick()) {
            Entity vehicle = mc.player.getVehicle();
            if (vehicle instanceof CoordinateTargetVehicle targetable) {
                mc.setScreen(new CoordinateInputScreen(targetable));
            }
        }
    }
}
