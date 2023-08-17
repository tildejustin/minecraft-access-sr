package com.github.khanshoaib3.minecraft_access.config.config_menus;

import com.github.khanshoaib3.minecraft_access.config.Config;
import com.github.khanshoaib3.minecraft_access.config.config_maps.RCPartialSpeakingConfigMap;
import com.github.khanshoaib3.minecraft_access.config.config_maps.ReadCrosshairConfigMap;
import com.github.khanshoaib3.minecraft_access.utils.BaseScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.function.Function;

@SuppressWarnings("DataFlowIssue")
public class ReadCrosshairConfigMenu extends BaseScreen {
    public ReadCrosshairConfigMenu(String title, BaseScreen previousScreen) {
        super(title, previousScreen);
    }

    @Override
    protected void init() {
        super.init();

        ReadCrosshairConfigMap map = ReadCrosshairConfigMap.getInstance();

        ButtonWidget featureToggleButton = this.buildButtonWidget(featureToggleButtonMessage(map.isEnabled()),
                (button) -> {
                    map.setEnabled(!map.isEnabled());
                    button.setMessage(new LiteralText(featureToggleButtonMessage(map.isEnabled())));
                    Config.getInstance().writeJSON();
                });
        this.addButton(featureToggleButton);

        Function<Boolean, String> speakBlockSidesText = featureToggleButtonMessageWith("minecraft_access.gui.read_crosshair_config_menu.button.speak_block_sides_button");
        ButtonWidget speakBlockSidesButton = this.buildButtonWidget(
                speakBlockSidesText.apply(map.isSpeakSide()),
                (button) -> {
                    map.setSpeakSide(!map.isSpeakSide());
                    button.setMessage(new LiteralText(speakBlockSidesText.apply(map.isSpeakSide())));
                    Config.getInstance().writeJSON();
                });
        this.addButton(speakBlockSidesButton);

        Function<Boolean, String> disableConsecutiveBlocksText = featureToggleButtonMessageWith("minecraft_access.gui.read_crosshair_config_menu.button.disable_speaking_consecutive_blocks_button");
        ButtonWidget disableConsecutiveBlocksButton = this.buildButtonWidget(
                disableConsecutiveBlocksText.apply(map.isDisableSpeakingConsecutiveBlocks()),
                (button) -> {
                    map.setDisableSpeakingConsecutiveBlocks(!map.isDisableSpeakingConsecutiveBlocks());
                    button.setMessage(new LiteralText(disableConsecutiveBlocksText.apply(map.isDisableSpeakingConsecutiveBlocks())));
                    Config.getInstance().writeJSON();
                },
                true);
        this.addButton(disableConsecutiveBlocksButton);

        ButtonWidget repeatSpeakingIntervalButton = this.buildButtonWidget(
                floatValueButtonMessageWith("minecraft_access.gui.read_crosshair_config_menu.button.repeat_speaking_interval_button",
                        map.getRepeatSpeakingInterval()),
                (button) -> this.client.openScreen(new ValueEntryMenu("value_entry_menu", ValueEntryMenu.CONFIG_TYPE.READ_CROSSHAIR_REPEAT_SPEAKING_INTERVAL, this)),
                true);
        this.addButton(repeatSpeakingIntervalButton);

        ButtonWidget enablePartialSpeakingButton = this.buildButtonWidget("minecraft_access.gui.read_crosshair_config_menu.button.partial_speaking_menu_button",
                (button) -> this.client.openScreen(new RCPartialSpeakingConfigMenu("rc_partial_speaking_menu", this)));
        this.addButton(enablePartialSpeakingButton);
    }
}

class RCPartialSpeakingConfigMenu extends BaseScreen {

    public RCPartialSpeakingConfigMenu(String title, BaseScreen previousScreen) {
        super(title, previousScreen);
    }

    @Override
    protected void init() {
        super.init();

        RCPartialSpeakingConfigMap map = RCPartialSpeakingConfigMap.getInstance();

        ButtonWidget featureToggleButton = this.buildButtonWidget(featureToggleButtonMessage(map.isEnabled()),
                (button) -> {
                    map.setEnabled(!map.isEnabled());
                    button.setMessage(new LiteralText(featureToggleButtonMessage(map.isEnabled())));
                    Config.getInstance().writeJSON();
                });
        this.addButton(featureToggleButton);

        Function<Boolean, String> partialSpeakingWhitelistModeText = featureToggleButtonMessageWith("minecraft_access.gui.rc_partial_speaking_menu.button.partial_speaking_whitelist_mode_button");
        ButtonWidget partialSpeakingWhitelistModeButton = this.buildButtonWidget(
                partialSpeakingWhitelistModeText.apply(map.isPartialSpeakingWhitelistMode()),
                (button) -> {
                    map.setPartialSpeakingWhitelistMode(!map.isPartialSpeakingWhitelistMode());
                    button.setMessage(new LiteralText(partialSpeakingWhitelistModeText.apply(map.isPartialSpeakingWhitelistMode())));
                    Config.getInstance().writeJSON();
                });
        this.addButton(partialSpeakingWhitelistModeButton);

        Function<Boolean, String> partialSpeakingFuzzyModeText = featureToggleButtonMessageWith("minecraft_access.gui.rc_partial_speaking_menu.button.partial_speaking_fuzzy_mode_button");
        ButtonWidget partialSpeakingFuzzyModeButton = this.buildButtonWidget(
                partialSpeakingFuzzyModeText.apply(map.isPartialSpeakingFuzzyMode()),
                (button) -> {
                    map.setPartialSpeakingFuzzyMode(!map.isPartialSpeakingFuzzyMode());
                    button.setMessage(new LiteralText(partialSpeakingFuzzyModeText.apply(map.isPartialSpeakingFuzzyMode())));
                    Config.getInstance().writeJSON();
                });
        this.addButton(partialSpeakingFuzzyModeButton);
    }
}