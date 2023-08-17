package com.github.khanshoaib3.minecraft_access.utils;

import com.github.khanshoaib3.minecraft_access.MainClass;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.function.Function;

public class BaseScreen extends Screen {
    int centerX;
    int buttonHeight;
    int marginY;
    int calculatedYPosition;
    int calculatedXPosition;
    int leftColumnX;
    int rightColumnX;
    boolean shouldRenderInLeftColumn;
    BaseScreen previousScreen;

    public BaseScreen(String title) {
        super(new LiteralText(I18n.translate("minecraft_access.gui.screen." + title)));
        previousScreen = null;
    }

    public BaseScreen(String title, BaseScreen previousScreen) {
        super(new LiteralText(I18n.translate("minecraft_access.gui.screen." + title)));
        this.previousScreen = previousScreen;
    }

    @Override
    protected void init() {
        this.centerX = this.width / 2;
        this.buttonHeight = 20;
        this.marginY = buttonHeight + buttonHeight / 4;
        this.calculatedYPosition = this.height / 6 - marginY; // Starting Y position (marginY will again be added in buildButtonWidget() so it is subtracted here to equate)
        this.leftColumnX = 10;
        this.rightColumnX = centerX + 10;
        shouldRenderInLeftColumn = true;
    }

    protected ButtonWidget buildButtonWidget(String translationKeyOrText, ButtonWidget.PressAction pressAction) {
        return buildButtonWidget(translationKeyOrText, pressAction, false);
    }

    protected ButtonWidget buildButtonWidget(String translationKeyOrText, ButtonWidget.PressAction pressAction, boolean shouldRenderInSeparateRow) {
        String buttonText = I18n.hasTranslation(translationKeyOrText) ? I18n.translate((translationKeyOrText)) : translationKeyOrText;
        int calculatedButtonWidth = this.textRenderer.getWidth(buttonText) + 35;
        if (shouldRenderInSeparateRow) {
            calculatedXPosition = centerX - calculatedButtonWidth / 2;
            calculatedYPosition += marginY;
            shouldRenderInLeftColumn = true;
        } else {
            calculatedXPosition = (shouldRenderInLeftColumn) ? leftColumnX : rightColumnX;
            if (shouldRenderInLeftColumn) calculatedYPosition += marginY;
            shouldRenderInLeftColumn = !shouldRenderInLeftColumn;
        }

        return new ButtonWidget(calculatedXPosition, calculatedYPosition, calculatedButtonWidth, buttonHeight, new LiteralText(buttonText), pressAction);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        this.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 15, 16777215);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        if (this.client != null) this.client.openScreen(previousScreen);
    }

    protected static String featureToggleButtonMessage(boolean enabled) {
        return I18n.translate("minecraft_access.gui.common.button.feature_toggle_button." + (enabled ? "enabled" : "disabled"));
    }

    /**
     * A reusable function for calculating feature toggle button message.
     */
    protected static Function<Boolean, String> featureToggleButtonMessageWith(String buttonTranslationKey) {
        return (Boolean b) -> I18n.translate("minecraft_access.gui.common.button.toggle_button." + (b ? "enabled" : "disabled"),
                I18n.translate(buttonTranslationKey));
    }

    protected static String floatValueButtonMessageWith(String buttonTranslationKey, double value) {
        return I18n.translate("minecraft_access.gui.common.button.button_with_float_value",
                I18n.translate(buttonTranslationKey), value);
    }
}
