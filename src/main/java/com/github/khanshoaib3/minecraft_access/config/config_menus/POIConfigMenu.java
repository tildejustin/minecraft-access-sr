package com.github.khanshoaib3.minecraft_access.config.config_menus;

import com.github.khanshoaib3.minecraft_access.MainClass;
import com.github.khanshoaib3.minecraft_access.config.Config;
import com.github.khanshoaib3.minecraft_access.config.ConfigMap;
import com.github.khanshoaib3.minecraft_access.config.config_maps.POIMarkingConfigMap;
import com.github.khanshoaib3.minecraft_access.utils.BaseScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.function.Function;

@SuppressWarnings("DataFlowIssue")
public class POIConfigMenu extends BaseScreen {
    public POIConfigMenu(String title, BaseScreen previousScreen) {
        super(title, previousScreen);
    }

    @Override
    protected void init() {
        super.init();

        ButtonWidget poiBlocksButton = this.buildButtonWidget("minecraft_access.gui.poi_config_menu.button.poi_blocks_button",
                (button) -> this.client.openScreen(new POIBlocksConfigMenu("poi_blocks_config_menu", this)));
        this.addButton(poiBlocksButton);

        ButtonWidget poiEntitiesButton = this.buildButtonWidget("minecraft_access.gui.poi_config_menu.button.poi_entities_button",
                (button) -> this.client.openScreen(new POIEntitiesConfigMenu("poi_entities_config_menu", this)));
        this.addButton(poiEntitiesButton);

        ButtonWidget poiLockingButton = this.buildButtonWidget("minecraft_access.gui.poi_config_menu.button.poi_locking_button",
                (button) -> this.client.openScreen(new POILockingConfigMenu("poi_locking_config_menu", this)));
        this.addButton(poiLockingButton);

        ButtonWidget poiMarkingButton = this.buildButtonWidget("minecraft_access.gui.poi_config_menu.button.poi_marking_button",
                (button) -> this.client.openScreen(new POIMarkingConfigMenu("poi_marking_config_menu", this)));
        this.addButton(poiMarkingButton);
    }
}

@SuppressWarnings("DataFlowIssue")
class POIBlocksConfigMenu extends BaseScreen {
    public POIBlocksConfigMenu(String title, BaseScreen previousScreen) {
        super(title, previousScreen);
    }

    @Override
    protected void init() {
        super.init();

        ButtonWidget featureToggleButton = this.buildButtonWidget("minecraft_access.gui.common.button.feature_toggle_button." + (MainClass.config.getConfigMap().getPoiConfigMap().getBlocksConfigMap().isEnabled() ? "enabled" : "disabled"),
                (button) -> {
                    ConfigMap configMap = MainClass.config.getConfigMap();
                    configMap.getPoiConfigMap().getBlocksConfigMap().setEnabled(!configMap.getPoiConfigMap().getBlocksConfigMap().isEnabled());
                    MainClass.config.setConfigMap(configMap);
                    button.setMessage(new LiteralText(I18n.translate("minecraft_access.gui.common.button.feature_toggle_button." + (MainClass.config.getConfigMap().getPoiConfigMap().getBlocksConfigMap().isEnabled() ? "enabled" : "disabled"))));
                });
        this.addButton(featureToggleButton);

        ButtonWidget detectFluidBlocksButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.toggle_button." + (MainClass.config.getConfigMap().getPoiConfigMap().getBlocksConfigMap().isDetectFluidBlocks() ? "enabled" : "disabled"),
                        I18n.translate("minecraft_access.gui.poi_blocks_config_menu.button.detect_fluid_blocks_button")
                ),
                (button) -> {
                    ConfigMap configMap = MainClass.config.getConfigMap();
                    configMap.getPoiConfigMap().getBlocksConfigMap().setDetectFluidBlocks(!configMap.getPoiConfigMap().getBlocksConfigMap().isDetectFluidBlocks());
                    MainClass.config.setConfigMap(configMap);
                    button.setMessage(new LiteralText(I18n.translate("minecraft_access.gui.common.button.toggle_button." + (MainClass.config.getConfigMap().getPoiConfigMap().getBlocksConfigMap().isDetectFluidBlocks() ? "enabled" : "disabled"),
                            I18n.translate("minecraft_access.gui.poi_blocks_config_menu.button.detect_fluid_blocks_button")
                    )));
                });
        this.addButton(detectFluidBlocksButton);

        ButtonWidget rangeButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.range",
                        MainClass.config.getConfigMap().getPoiConfigMap().getBlocksConfigMap().getRange()),
                (button) -> this.client.openScreen(new ValueEntryMenu("value_entry_menu", ValueEntryMenu.CONFIG_TYPE.POI_BLOCKS_RANGE, this)));
        this.addButton(rangeButton);

        ButtonWidget playSoundButton = this.buildButtonWidget("minecraft_access.gui.common.button.play_sound_toggle_button." + (MainClass.config.getConfigMap().getPoiConfigMap().getBlocksConfigMap().isPlaySound() ? "enabled" : "disabled"),
                (button) -> {
                    ConfigMap configMap = MainClass.config.getConfigMap();
                    configMap.getPoiConfigMap().getBlocksConfigMap().setPlaySound(!configMap.getPoiConfigMap().getBlocksConfigMap().isPlaySound());
                    MainClass.config.setConfigMap(configMap);
                    button.setMessage(new LiteralText(I18n.translate("minecraft_access.gui.common.button.play_sound_toggle_button." + (MainClass.config.getConfigMap().getPoiConfigMap().getBlocksConfigMap().isPlaySound() ? "enabled" : "disabled"))));
                });
        this.addButton(playSoundButton);

        ButtonWidget volumeButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.volume",
                        MainClass.config.getConfigMap().getPoiConfigMap().getBlocksConfigMap().getVolume()),
                (button) -> this.client.openScreen(new ValueEntryMenu("value_entry_menu", ValueEntryMenu.CONFIG_TYPE.POI_BLOCKS_VOLUME, this)));
        this.addButton(volumeButton);

        ButtonWidget playSoundForOtherBlocksButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.toggle_button." + (MainClass.config.getConfigMap().getPoiConfigMap().getBlocksConfigMap().isPlaySoundForOtherBlocks() ? "enabled" : "disabled"),
                        I18n.translate("minecraft_access.gui.poi_blocks_config_menu.button.play_sound_for_other_blocks_button")
                ),
                (button) -> {
                    ConfigMap configMap = MainClass.config.getConfigMap();
                    configMap.getPoiConfigMap().getBlocksConfigMap().setPlaySoundForOtherBlocks(!configMap.getPoiConfigMap().getBlocksConfigMap().isPlaySoundForOtherBlocks());
                    MainClass.config.setConfigMap(configMap);
                    button.setMessage(new LiteralText(I18n.translate("minecraft_access.gui.common.button.toggle_button." + (MainClass.config.getConfigMap().getPoiConfigMap().getBlocksConfigMap().isPlaySoundForOtherBlocks() ? "enabled" : "disabled"),
                            I18n.translate("minecraft_access.gui.poi_blocks_config_menu.button.play_sound_for_other_blocks_button")
                    )));
                },
                true);
        this.addButton(playSoundForOtherBlocksButton);

        ButtonWidget delayButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.delay",
                        MainClass.config.getConfigMap().getPoiConfigMap().getBlocksConfigMap().getDelay()),
                (button) -> this.client.openScreen(new ValueEntryMenu("value_entry_menu", ValueEntryMenu.CONFIG_TYPE.POI_BLOCKS_DELAY, this)));
        this.addButton(delayButton);
    }
}

@SuppressWarnings("DataFlowIssue")
class POIEntitiesConfigMenu extends BaseScreen {
    public POIEntitiesConfigMenu(String title, BaseScreen previousScreen) {
        super(title, previousScreen);
    }

    @Override
    protected void init() {
        super.init();

        ButtonWidget featureToggleButton = this.buildButtonWidget("minecraft_access.gui.common.button.feature_toggle_button." + (MainClass.config.getConfigMap().getPoiConfigMap().getEntitiesConfigMap().isEnabled() ? "enabled" : "disabled"),
                (button) -> {
                    ConfigMap configMap = MainClass.config.getConfigMap();
                    configMap.getPoiConfigMap().getEntitiesConfigMap().setEnabled(!configMap.getPoiConfigMap().getEntitiesConfigMap().isEnabled());
                    MainClass.config.setConfigMap(configMap);
                    button.setMessage(new LiteralText(I18n.translate("minecraft_access.gui.common.button.feature_toggle_button." + (MainClass.config.getConfigMap().getPoiConfigMap().getEntitiesConfigMap().isEnabled() ? "enabled" : "disabled"))));
                });
        this.addButton(featureToggleButton);

        ButtonWidget rangeButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.range",
                        MainClass.config.getConfigMap().getPoiConfigMap().getEntitiesConfigMap().getRange()),
                (button) -> this.client.openScreen(new ValueEntryMenu("value_entry_menu", ValueEntryMenu.CONFIG_TYPE.POI_ENTITIES_RANGE, this)));
        this.addButton(rangeButton);

        ButtonWidget playSoundButton = this.buildButtonWidget("minecraft_access.gui.common.button.play_sound_toggle_button." + (MainClass.config.getConfigMap().getPoiConfigMap().getEntitiesConfigMap().isPlaySound() ? "enabled" : "disabled"),
                (button) -> {
                    ConfigMap configMap = MainClass.config.getConfigMap();
                    configMap.getPoiConfigMap().getEntitiesConfigMap().setPlaySound(!configMap.getPoiConfigMap().getEntitiesConfigMap().isPlaySound());
                    MainClass.config.setConfigMap(configMap);
                    button.setMessage(new LiteralText(I18n.translate("minecraft_access.gui.common.button.play_sound_toggle_button." + (MainClass.config.getConfigMap().getPoiConfigMap().getEntitiesConfigMap().isPlaySound() ? "enabled" : "disabled"))));
                });
        this.addButton(playSoundButton);

        ButtonWidget volumeButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.volume",
                        MainClass.config.getConfigMap().getPoiConfigMap().getEntitiesConfigMap().getVolume()),
                (button) -> this.client.openScreen(new ValueEntryMenu("value_entry_menu", ValueEntryMenu.CONFIG_TYPE.POI_ENTITIES_VOLUME, this)));
        this.addButton(volumeButton);

        ButtonWidget delayButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.delay",
                        MainClass.config.getConfigMap().getPoiConfigMap().getEntitiesConfigMap().getDelay()),
                (button) -> this.client.openScreen(new ValueEntryMenu("value_entry_menu", ValueEntryMenu.CONFIG_TYPE.POI_ENTITIES_DELAY, this)));
        this.addButton(delayButton);
    }
}

@SuppressWarnings("DataFlowIssue")
class POILockingConfigMenu extends BaseScreen {
    public POILockingConfigMenu(String title, BaseScreen previousScreen) {
        super(title, previousScreen);
    }

    @Override
    protected void init() {
        super.init();

        ButtonWidget featureToggleButton = this.buildButtonWidget("minecraft_access.gui.common.button.feature_toggle_button." + (MainClass.config.getConfigMap().getPoiConfigMap().getLockingConfigMap().isEnabled() ? "enabled" : "disabled"),
                (button) -> {
                    ConfigMap configMap = MainClass.config.getConfigMap();
                    configMap.getPoiConfigMap().getLockingConfigMap().setEnabled(!configMap.getPoiConfigMap().getLockingConfigMap().isEnabled());
                    MainClass.config.setConfigMap(configMap);
                    button.setMessage(new LiteralText(I18n.translate("minecraft_access.gui.common.button.feature_toggle_button." + (MainClass.config.getConfigMap().getPoiConfigMap().getLockingConfigMap().isEnabled() ? "enabled" : "disabled"))));
                });
        this.addButton(featureToggleButton);

        ButtonWidget lockOnBlocksButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.toggle_button." + (MainClass.config.getConfigMap().getPoiConfigMap().getLockingConfigMap().isLockOnBlocks() ? "enabled" : "disabled"),
                        I18n.translate("minecraft_access.gui.poi_locking_config_menu.button.lock_on_blocks_button")
                ),
                (button) -> {
                    ConfigMap configMap = MainClass.config.getConfigMap();
                    configMap.getPoiConfigMap().getLockingConfigMap().setLockOnBlocks(!configMap.getPoiConfigMap().getLockingConfigMap().isLockOnBlocks());
                    MainClass.config.setConfigMap(configMap);
                    button.setMessage(new LiteralText(I18n.translate("minecraft_access.gui.common.button.toggle_button." + (MainClass.config.getConfigMap().getPoiConfigMap().getLockingConfigMap().isLockOnBlocks() ? "enabled" : "disabled"),
                            I18n.translate("minecraft_access.gui.poi_locking_config_menu.button.lock_on_blocks_button")
                    )));
                });
        this.addButton(lockOnBlocksButton);

        ButtonWidget speakDistanceButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.toggle_button." + (MainClass.config.getConfigMap().getPoiConfigMap().getLockingConfigMap().isSpeakDistance() ? "enabled" : "disabled"),
                        I18n.translate("minecraft_access.gui.poi_locking_config_menu.button.speak_distance_button")
                ),
                (button) -> {
                    ConfigMap configMap = MainClass.config.getConfigMap();
                    configMap.getPoiConfigMap().getLockingConfigMap().setSpeakDistance(!configMap.getPoiConfigMap().getLockingConfigMap().isSpeakDistance());
                    MainClass.config.setConfigMap(configMap);
                    button.setMessage(new LiteralText(I18n.translate("minecraft_access.gui.common.button.toggle_button." + (MainClass.config.getConfigMap().getPoiConfigMap().getLockingConfigMap().isSpeakDistance() ? "enabled" : "disabled"),
                            I18n.translate("minecraft_access.gui.poi_locking_config_menu.button.speak_distance_button")
                    )));
                },
                true);
        this.addButton(speakDistanceButton);

        ButtonWidget autoLockEyeOfEnderButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.toggle_button." + (MainClass.config.getConfigMap().getPoiConfigMap().getLockingConfigMap().isAutoLockEyeOfEnderEntity() ? "enabled" : "disabled"),
                        I18n.translate("minecraft_access.gui.poi_locking_config_menu.button.auto_lock_eye_of_ender_button")
                ),
                (button) -> {
                    ConfigMap configMap = MainClass.config.getConfigMap();
                    configMap.getPoiConfigMap().getLockingConfigMap().setAutoLockEyeOfEnderEntity(!configMap.getPoiConfigMap().getLockingConfigMap().isAutoLockEyeOfEnderEntity());
                    MainClass.config.setConfigMap(configMap);
                    button.setMessage(new LiteralText(I18n.translate("minecraft_access.gui.common.button.toggle_button." + (MainClass.config.getConfigMap().getPoiConfigMap().getLockingConfigMap().isAutoLockEyeOfEnderEntity() ? "enabled" : "disabled"),
                            I18n.translate("minecraft_access.gui.poi_locking_config_menu.button.auto_lock_eye_of_ender_button")
                    )));
                },
                true);
        this.addButton(autoLockEyeOfEnderButton);

        ButtonWidget unlockingSoundButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.toggle_button." + (MainClass.config.getConfigMap().getPoiConfigMap().getLockingConfigMap().isUnlockingSound() ? "enabled" : "disabled"),
                        I18n.translate("minecraft_access.gui.poi_locking_config_menu.button.unlocking_sound_button")
                ),
                (button) -> {
                    ConfigMap configMap = MainClass.config.getConfigMap();
                    configMap.getPoiConfigMap().getLockingConfigMap().setUnlockingSound(!configMap.getPoiConfigMap().getLockingConfigMap().isUnlockingSound());
                    MainClass.config.setConfigMap(configMap);
                    button.setMessage(new LiteralText(I18n.translate("minecraft_access.gui.common.button.toggle_button." + (MainClass.config.getConfigMap().getPoiConfigMap().getLockingConfigMap().isUnlockingSound() ? "enabled" : "disabled"),
                            I18n.translate("minecraft_access.gui.poi_locking_config_menu.button.unlocking_sound_button")
                    )));
                });
        this.addButton(unlockingSoundButton);

        ButtonWidget delayButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.delay",
                        MainClass.config.getConfigMap().getPoiConfigMap().getLockingConfigMap().getDelay()),
                (button) -> this.client.openScreen(new ValueEntryMenu("value_entry_menu", ValueEntryMenu.CONFIG_TYPE.POI_LOCKING_DELAY, this)));
        this.addButton(delayButton);
    }
}

class POIMarkingConfigMenu extends BaseScreen {
    public POIMarkingConfigMenu(String title, BaseScreen previousScreen) {
        super(title, previousScreen);
    }

    @Override
    protected void init() {
        super.init();

        POIMarkingConfigMap map = POIMarkingConfigMap.getInstance();

        ButtonWidget b1 = this.buildButtonWidget(featureToggleButtonMessage(map.isEnabled()),
                (button) -> {
                    map.setEnabled(!map.isEnabled());
                    button.setMessage(new LiteralText(featureToggleButtonMessage(map.isEnabled())));
                    Config.getInstance().writeJSON();
                });
        this.addButton(b1);

        Function<Boolean, String> t2 = featureToggleButtonMessageWith("minecraft_access.gui.poi_marking_config_menu.button.suppress_other_when_enabled_button");
        ButtonWidget b2 = this.buildButtonWidget(
                t2.apply(map.isSuppressOtherWhenEnabled()),
                (button) -> {
                    map.setSuppressOtherWhenEnabled(!map.isSuppressOtherWhenEnabled());
                    button.setMessage(new LiteralText(t2.apply(map.isSuppressOtherWhenEnabled())));
                    Config.getInstance().writeJSON();
                });
        this.addButton(b2);
    }
}