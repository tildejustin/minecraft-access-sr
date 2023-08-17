package com.github.khanshoaib3.minecraft_access.config.config_menus;

import com.github.khanshoaib3.minecraft_access.MainClass;
import com.github.khanshoaib3.minecraft_access.config.ConfigMap;
import com.github.khanshoaib3.minecraft_access.utils.BaseScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

@SuppressWarnings("DataFlowIssue")
public class PlayerWarningsConfigMenu extends BaseScreen {
    public PlayerWarningsConfigMenu(String title, BaseScreen previousScreen) {
        super(title, previousScreen);
    }

    @Override
    protected void init() {
        super.init();

        ButtonWidget featureToggleButton = this.buildButtonWidget("minecraft_access.gui.common.button.feature_toggle_button." + (MainClass.config.getConfigMap().getPlayerWarningConfigMap().isEnabled() ? "enabled" : "disabled"),
                (button) -> {
                    ConfigMap configMap = MainClass.config.getConfigMap();
                    configMap.getPlayerWarningConfigMap().setEnabled(!configMap.getPlayerWarningConfigMap().isEnabled());
                    MainClass.config.setConfigMap(configMap);
                    button.setMessage(new LiteralText(I18n.translate("minecraft_access.gui.common.button.feature_toggle_button." + (MainClass.config.getConfigMap().getPlayerWarningConfigMap().isEnabled() ? "enabled" : "disabled"))));
                });
        this.addButton(featureToggleButton);

        ButtonWidget playSoundButton = this.buildButtonWidget("minecraft_access.gui.common.button.play_sound_toggle_button." + (MainClass.config.getConfigMap().getPlayerWarningConfigMap().isPlaySound() ? "enabled" : "disabled"),
                (button) -> {
                    ConfigMap configMap = MainClass.config.getConfigMap();
                    configMap.getPlayerWarningConfigMap().setPlaySound(!configMap.getPlayerWarningConfigMap().isPlaySound());
                    MainClass.config.setConfigMap(configMap);
                    button.setMessage(new LiteralText(I18n.translate("minecraft_access.gui.common.button.play_sound_toggle_button." + (MainClass.config.getConfigMap().getPlayerWarningConfigMap().isPlaySound() ? "enabled" : "disabled"))));
                });
        this.addButton(playSoundButton);

        ButtonWidget firstHealthThresholdButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.button_with_float_value",
                        I18n.translate("minecraft_access.gui.player_warnings_config_menu.button.first_health_threshold_button"),
                        MainClass.config.getConfigMap().getPlayerWarningConfigMap().getFirstHealthThreshold()
                ),
                (button) -> this.client.openScreen(new ValueEntryMenu("value_entry_menu", ValueEntryMenu.CONFIG_TYPE.PLAYER_WARNINGS_FIRST_HEALTH_THRESHOLD, this)));
        this.addButton(firstHealthThresholdButton);

        ButtonWidget secondHealthThresholdButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.button_with_float_value",
                        I18n.translate("minecraft_access.gui.player_warnings_config_menu.button.second_health_threshold_button"),
                        MainClass.config.getConfigMap().getPlayerWarningConfigMap().getSecondHealthThreshold()
                ),
                (button) -> this.client.openScreen(new ValueEntryMenu("value_entry_menu", ValueEntryMenu.CONFIG_TYPE.PLAYER_WARNINGS_SECOND_HEALTH_THRESHOLD, this)));
        this.addButton(secondHealthThresholdButton);

        ButtonWidget hungerThresholdButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.button_with_float_value",
                        I18n.translate("minecraft_access.gui.player_warnings_config_menu.button.hunger_threshold_button"),
                        MainClass.config.getConfigMap().getPlayerWarningConfigMap().getHungerThreshold()
                ),
                (button) -> this.client.openScreen(new ValueEntryMenu("value_entry_menu", ValueEntryMenu.CONFIG_TYPE.PLAYER_WARNINGS_HUNGER_THRESHOLD, this)));
        this.addButton(hungerThresholdButton);

        ButtonWidget airThresholdButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.button_with_float_value",
                        I18n.translate("minecraft_access.gui.player_warnings_config_menu.button.air_threshold_button"),
                        MainClass.config.getConfigMap().getPlayerWarningConfigMap().getAirThreshold()
                ),
                (button) -> this.client.openScreen(new ValueEntryMenu("value_entry_menu", ValueEntryMenu.CONFIG_TYPE.PLAYER_WARNINGS_AIR_THRESHOLD, this)));
        this.addButton(airThresholdButton);
    }
}
