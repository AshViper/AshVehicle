package Aru.Aru.ashvehicle.client.overlay;

import Aru.Aru.ashvehicle.AshVehicle;
import Aru.Aru.ashvehicle.client.screen.TargetingCameraScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

/**
 * Overlay for targeting camera HUD.
 */
@OnlyIn(Dist.CLIENT)
public class TargetingCameraOverlay implements IGuiOverlay {
    
    public static final String ID = AshVehicle.MODID + "_targeting_camera";

    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        if (!TargetingCameraScreen.isActive()) return;
        
        TargetingCameraScreen.renderHUD(guiGraphics, screenWidth, screenHeight);
    }
}
