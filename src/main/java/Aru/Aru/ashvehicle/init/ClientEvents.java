package Aru.Aru.ashvehicle.init;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import Aru.Aru.ashvehicle.ExtensionTest;
import Aru.Aru.ashvehicle.client.screen.CoordinateInputScreen;
import Aru.Aru.ashvehicle.entity.SapsanEntity;

@Mod.EventBusSubscriber(modid = ExtensionTest.MODID, value = Dist.CLIENT)
public class ClientEvents {
    private static boolean wasQDown = false;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null || mc.player == null) return;

        boolean isQDown = mc.options.keyDrop.isDown();

        // 🚩 特定のエンティティに乗っているか確認（例：SapsanEntity）
        Entity vehicle = mc.player.getVehicle();
        if (isQDown && !wasQDown && vehicle instanceof SapsanEntity) {
            mc.setScreen(new CoordinateInputScreen((SapsanEntity) vehicle));
        }

        wasQDown = isQDown;
    }
}
