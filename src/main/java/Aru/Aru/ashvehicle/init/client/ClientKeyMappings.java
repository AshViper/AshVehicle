package Aru.Aru.ashvehicle.init.client;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;

import org.lwjgl.glfw.GLFW;

public class ClientKeyMappings {
    public static KeyMapping OPEN_COORDINATE_SCREEN;
    public static KeyMapping TOGGLE_POD;
    public static KeyMapping THERMAL_VISION;
    public static KeyMapping VTOL_TOGGLE;

    public static void register(RegisterKeyMappingsEvent event) {
        OPEN_COORDINATE_SCREEN = new KeyMapping(
                "key.ashvehicle.open_coordinate",
                GLFW.GLFW_KEY_G,  // Changed to G to avoid conflict
                "key.categories.ashvehicle"
        );
        event.register(OPEN_COORDINATE_SCREEN);
        
        TOGGLE_POD = new KeyMapping(
                "key.ashvehicle.toggle_pod",
                GLFW.GLFW_KEY_LEFT_CONTROL,  // Ctrl key for pod toggle
                "key.categories.ashvehicle"
        );
        event.register(TOGGLE_POD);

        THERMAL_VISION = new KeyMapping(
                "key.ashvehicle.thermalvision",
                GLFW.GLFW_KEY_F,  // Ctrl key for pod toggle
                "key.categories.ashvehicle"
        );
        event.register(THERMAL_VISION);

        VTOL_TOGGLE = new KeyMapping(
                "key.ashvehicle.vtol_toggle",
                GLFW.GLFW_KEY_V,  // Ctrl key for pod toggle
                "key.categories.ashvehicle"
        );
        event.register(VTOL_TOGGLE);
    }
}

