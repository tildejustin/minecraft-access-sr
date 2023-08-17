package com.github.khanshoaib3.minecraft_access.features.narrator_menu;

import com.github.khanshoaib3.minecraft_access.MainClass;
import com.github.khanshoaib3.minecraft_access.config.ConfigMenu;
import com.github.khanshoaib3.minecraft_access.screen_reader.ScreenReaderController;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

/**
 * GUI screen for the narrator menu.
 */
public class NarratorMenuGUI extends Screen {
    int centerX;
    int buttonHeight;
    int marginY;
    int calculatedYPosition;
    int calculatedXPosition;
    int leftColumnX;
    int rightColumnX;
    boolean shouldRenderInLeftColumn;

    public NarratorMenuGUI(String title) {
        super(new LiteralText(I18n.translate("minecraft_access.gui.screen." + title)));
    }

    /**
     * The order of buttons initialization should be same as NarratorMenu.menuFunctions
     */
    @Override
    protected void init() {
        this.centerX = this.width / 2;
        this.buttonHeight = 20;
        this.marginY = buttonHeight + buttonHeight / 4;
        this.calculatedYPosition = this.height / 6 - marginY; // Starting Y position (marginY will again be added in buildButtonWidget() so it is subtracted here to equate)
        this.leftColumnX = 10;
        this.rightColumnX = centerX + 10;
        shouldRenderInLeftColumn = true;


        ButtonWidget blockAndFluidTargetInformationButton = this.buildButtonWidget("minecraft_access.narrator_menu.gui.button.block_and_fluid_target_info",
                (button) -> NarratorMenu.getBlockAndFluidTargetInformation());
        this.addButton(blockAndFluidTargetInformationButton);

        ButtonWidget blockAndFluidTargetPositionButton = this.buildButtonWidget("minecraft_access.narrator_menu.gui.button.block_and_fluid_target_position",
                (button) -> NarratorMenu.getBlockAndFluidTargetPosition());
        this.addButton(blockAndFluidTargetPositionButton);

        ButtonWidget lightLevelButton = this.buildButtonWidget("minecraft_access.narrator_menu.gui.button.light_level",
                (button) -> NarratorMenu.getLightLevel());
        this.addButton(lightLevelButton);

        ButtonWidget findWaterButton = this.buildButtonWidget("minecraft_access.narrator_menu.gui.button.find_water",
                (button) -> MainClass.fluidDetector.findClosestWaterSource(true));
        this.addButton(findWaterButton);

        ButtonWidget findLavaButton = this.buildButtonWidget("minecraft_access.narrator_menu.gui.button.find_lava",
                (button) -> MainClass.fluidDetector.findClosestLavaSource(true));
        this.addButton(findLavaButton);

        ButtonWidget biomeButton = this.buildButtonWidget("minecraft_access.narrator_menu.gui.button.biome",
                (button) -> NarratorMenu.getBiome());
        this.addButton(biomeButton);

        ButtonWidget timeOfDayButton = this.buildButtonWidget("minecraft_access.narrator_menu.gui.button.time_of_day",
                (button) -> NarratorMenu.getTimeOfDay());
        this.addButton(timeOfDayButton);

        ButtonWidget xpButton = this.buildButtonWidget("minecraft_access.narrator_menu.gui.button.xp",
                (button) -> NarratorMenu.getXP());
        this.addButton(xpButton);

        ButtonWidget refreshScreenReaderButton = this.buildButtonWidget("minecraft_access.narrator_menu.gui.button.refresh_screen_reader",
                (button) -> ScreenReaderController.refreshScreenReader(true));
        this.addButton(refreshScreenReaderButton);

        ButtonWidget openConfigMenuButton = this.buildButtonWidget("minecraft_access.narrator_menu.gui.button.open_config_menu",
                (button) -> MinecraftClient.getInstance().openScreen(new ConfigMenu("config_menu")));
        this.addButton(openConfigMenuButton);
    }

    private ButtonWidget buildButtonWidget(String translationKey, ButtonWidget.PressAction pressAction) {
        int calculatedButtonWidth = this.textRenderer.getWidth(I18n.translate((translationKey))) + 35;
        calculatedXPosition = shouldRenderInLeftColumn ? leftColumnX : rightColumnX;
        if (shouldRenderInLeftColumn) calculatedYPosition += marginY;
        shouldRenderInLeftColumn = !shouldRenderInLeftColumn;

        return new ButtonWidget(calculatedXPosition, calculatedYPosition, calculatedButtonWidth, buttonHeight,new TranslatableText(translationKey), pressAction);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        this.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 15, 16777215);
        super.render(matrices, mouseX, mouseY, delta);
    }
}