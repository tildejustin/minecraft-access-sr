package com.github.khanshoaib3.minecraft_access.features;

import com.github.khanshoaib3.minecraft_access.MainClass;
import com.github.khanshoaib3.minecraft_access.utils.KeyBindingsHandler;
import com.github.khanshoaib3.minecraft_access.utils.KeyUtils;
import com.github.khanshoaib3.minecraft_access.utils.PlayerPositionUtils;
import com.github.khanshoaib3.minecraft_access.utils.PositionUtils;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

/**
 * Adds key bindings to speak the player's position.<br><br>
 * Keybindings and combinations:<br>
 * 1. Speak Player Position Key (default: G) = Speaks the player's x y and z position.<br>
 * 2. Left Alt + X = Speaks only the x position.<br>
 * 3. Left Alt + C = Speaks only the y position.<br>
 * 4. Left Alt + Z = Speaks only the z position.<br>
 */
public class PositionNarrator {
    public static String defaultFormat = "{x}x, {y}y, {z}z";

    public void update() {
        try {
            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            if (minecraftClient == null) return;
            if (minecraftClient.player == null) return;
            if (minecraftClient.currentScreen != null) return;

            boolean isLeftAltPressed = KeyUtils.isLeftAltPressed();
            boolean isXPressed = KeyUtils.isAnyPressed(GLFW.GLFW_KEY_X);
            boolean isCPressed = KeyUtils.isAnyPressed(GLFW.GLFW_KEY_C);
            boolean isZPressed = KeyUtils.isAnyPressed(GLFW.GLFW_KEY_Z);

            if (isLeftAltPressed) {
                if (isXPressed) {
                    MainClass.speakWithNarrator(new PlayerPositionUtils(minecraftClient).getNarratableXPos(), true);
                    return;
                }

                if (isCPressed) {
                    MainClass.speakWithNarrator(new PlayerPositionUtils(minecraftClient).getNarratableYPos(), true);
                    return;
                }

                if (isZPressed) {
                    MainClass.speakWithNarrator(new PlayerPositionUtils(minecraftClient).getNarratableZPos(), true);
                    return;
                }
            }

            boolean isPositionNarrationKeyPressed = KeyUtils.isAnyPressed(KeyBindingsHandler.getInstance().positionNarrationKey);

            if (isPositionNarrationKeyPressed) {
                String posX = PositionUtils.getNarratableNumber(new PlayerPositionUtils(minecraftClient).getX());
                String posY = PositionUtils.getNarratableNumber(new PlayerPositionUtils(minecraftClient).getY());
                String posZ = PositionUtils.getNarratableNumber(new PlayerPositionUtils(minecraftClient).getZ());

                MainClass.speakWithNarrator(getNarrationText(posX, posY, posZ), true);
            }
        } catch (Exception e) {
            MainClass.errorLog("An error occurred in PositionNarrator.");
            e.printStackTrace();
        }
    }

    private String getNarrationText(String posX, String posY, String posZ) {
        String format = MainClass.config.getConfigMap().getOtherConfigsMap().getPositionNarratorFormat();

        if (!format.contains("{x}") || !format.contains("{y}") || !format.contains("{z}")) format = defaultFormat;

        format = format.replace("{x}", posX);
        format = format.replace("{y}", posY);
        format = format.replace("{z}", posZ);

        return format;
    }
}
