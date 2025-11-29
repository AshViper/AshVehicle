package Aru.Aru.ashvehicle.init.client;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;

import org.lwjgl.glfw.GLFW;

public class ClientKeyMappings {
    // キー割り当てを保持
    public static KeyMapping OPEN_COORDINATE_SCREEN;

    public static void register(RegisterKeyMappingsEvent event) {
        // 第1引数: 名前（langファイルで翻訳可）
        // 第2引数: デフォルトキー
        // 第3引数: カテゴリ（設定画面でまとめる）
        OPEN_COORDINATE_SCREEN = new KeyMapping(
                "key.ashvehicle.open_coordinate",  // 言語キー
                GLFW.GLFW_KEY_Q,                  // デフォルトはQ
                "key.categories.ashvehicle"       // カテゴリ名
        );
        event.register(OPEN_COORDINATE_SCREEN);
    }
}

