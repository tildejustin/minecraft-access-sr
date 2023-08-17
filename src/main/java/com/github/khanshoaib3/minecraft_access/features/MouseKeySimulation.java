package com.github.khanshoaib3.minecraft_access.features;

import com.github.khanshoaib3.minecraft_access.MainClass;
import com.github.khanshoaib3.minecraft_access.config.config_maps.MouseSimulationConfigMap;
import com.github.khanshoaib3.minecraft_access.utils.KeyBindingsHandler;
import com.github.khanshoaib3.minecraft_access.utils.KeyUtils;
import com.github.khanshoaib3.minecraft_access.utils.MouseUtils;
import com.github.khanshoaib3.minecraft_access.utils.TimeUtils;
import net.minecraft.client.MinecraftClient;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Set;

/**
 * Bind four mouse operations with customizable keys:<br><br>
 * 1) left mouse key pressing<br>
 * 2) right mouse key pressing<br>
 * 3) middle mouse key pressing<br>
 * 4) mouse wheel scroll up<br>
 * 5) mouse wheel scroll down
 */
public class MouseKeySimulation {
    private static final MouseKeySimulation instance;

    private boolean enabled;
    /**
     * index 0,1,2 for left, middle, right mouse key
     */
    private final boolean[] isMouseKeyPressedPreviousTick = new boolean[]{false, false, false};
    private TimeUtils.Interval scrollUpDelay;
    private TimeUtils.Interval scrollDownDelay;

    static {
        try {
            instance = new MouseKeySimulation();
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred in creating AttackAndUseSimulation instance");
        }
    }

    public static synchronized MouseKeySimulation getInstance() {
        return instance;
    }

    public void update() {
        try {
            loadConfigurations();

            if (!enabled) return;
            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            if (minecraftClient == null) return;
            if (minecraftClient.player == null) return;
            // this feature only works when there is no screen open.
            if (minecraftClient.currentScreen != null) return;

            execute();

        } catch (Exception e) {
            MainClass.errorLog("An error occurred while executing MouseKeySimulation", e);
        }
    }

    private void loadConfigurations() {
        MouseSimulationConfigMap map = MainClass.config.getConfigMap().getMouseSimulationConfigMap();
        this.enabled = map.isEnabled();
        this.scrollUpDelay = TimeUtils.Interval.inMilliseconds(map.getScrollDelayInMilliseconds(), this.scrollUpDelay);
        this.scrollDownDelay = TimeUtils.Interval.inMilliseconds(map.getScrollDelayInMilliseconds(), this.scrollDownDelay);
    }

    private void execute() {
        KeyBindingsHandler kbh = KeyBindingsHandler.getInstance();

        Set.of(
                Triple.<Boolean, TimeUtils.Interval, Runnable>of(KeyUtils.isAnyPressed(kbh.mouseSimulationScrollUpKey), scrollUpDelay, MouseUtils::scrollUp),
                Triple.<Boolean, TimeUtils.Interval, Runnable>of(KeyUtils.isAnyPressed(kbh.mouseSimulationScrollDownKey), scrollDownDelay, MouseUtils::scrollDown)
        ).forEach(t -> {
            if (t.getLeft() && t.getMiddle().isReady()) {
                t.getRight().run();
            }
        });

        Set.of(
                new MouseKeyDTO(KeyUtils.isAnyPressed(kbh.mouseSimulationLeftMouseKey), 0, MouseUtils::leftDown, MouseUtils::leftUp),
                new MouseKeyDTO(KeyUtils.isAnyPressed(kbh.mouseSimulationMiddleMouseKey), 1, MouseUtils::middleDown, MouseUtils::middleUp),
                new MouseKeyDTO(KeyUtils.isAnyPressed(kbh.mouseSimulationRightMouseKey), 2, MouseUtils::rightDown, MouseUtils::rightUp)
        ).forEach(dto -> {
            if (dto.keyPressed && !isMouseKeyPressedPreviousTick[dto.keyPressedPreviouslyIndex]) {
                // key pressed
                dto.keyDown.run();
            } else if (!dto.keyPressed && isMouseKeyPressedPreviousTick[dto.keyPressedPreviouslyIndex]) {
                // key released
                dto.keyUp.run();
            }

            // update state
            isMouseKeyPressedPreviousTick[dto.keyPressedPreviouslyIndex] = dto.keyPressed;
        });
    }

    private record MouseKeyDTO(Boolean keyPressed, int keyPressedPreviouslyIndex, Runnable keyDown, Runnable keyUp) {
    }
}
