package com.github.khanshoaib3.minecraft_access.features.point_of_interest;

import com.github.khanshoaib3.minecraft_access.MainClass;
import com.github.khanshoaib3.minecraft_access.config.Config;
import com.github.khanshoaib3.minecraft_access.utils.KeyBindingsHandler;
import com.github.khanshoaib3.minecraft_access.utils.KeyUtils;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public class POIMarking {
    private static final POIMarking instance;
    private static final POIBlocks poiBlocks;
    private static final POIEntities poiEntities;
    private static final LockingHandler lockingHandler;
    private static boolean marking = false;

    static {
        instance = new POIMarking();
        poiBlocks = POIBlocks.getInstance();
        poiEntities = POIEntities.getInstance();
        lockingHandler = LockingHandler.getInstance();
    }

    public static POIMarking getInstance() {
        return instance;
    }

    /**
     * Perform this feature before the normal POI scan,
     * and suppress the normal POI scan (by switching their targets to marked target)
     * if this feature is enabled.
     */
    public void update() {
        if (Config.getInstance().getConfigMap().getPoiConfigMap().getPoiMarkingConfigMap().isEnabled()) {
            boolean controlPressed = Screen.hasControlDown();
            boolean AltPressed = Screen.hasAltDown();
            boolean lockingKeyPressed = KeyUtils.isAnyPressed(KeyBindingsHandler.getInstance().lockingHandlerKey);

            if (lockingKeyPressed && AltPressed && controlPressed) {
                unmark();
            } else if (controlPressed && lockingKeyPressed) {
                mark();
            }

        } else {
            unmark();
        }

        // trigger POI updates
        poiBlocks.update();
        poiEntities.update();
    }

    private void mark() {
        if (marking) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return;
        HitResult hit = client.crosshairTarget;
        if (hit == null) return;

        switch (hit.getType()) {
            case MISS -> {
                return;
            }
            case BLOCK -> {
                ClientWorld world = client.world;
                if (world == null) return;
                Block b = world.getBlockState(((BlockHitResult) hit).getBlockPos()).getBlock();
                poiBlocks.setMarkedBlock(b);
                poiEntities.setMarking(true);

                String name = b.getName().getString();
                MainClass.speakWithNarrator(I18n.translate("minecraft_access.point_of_interest.marking.marked", name), true);
            }
            case ENTITY -> {
                Entity e = ((EntityHitResult) hit).getEntity();
                poiEntities.setMarkedEntity(e);
                poiBlocks.setMarking(true);

                String name = e.getName().getString();
                MainClass.speakWithNarrator(I18n.translate("minecraft_access.point_of_interest.marking.marked", name), true);
            }
        }

        marking = true;
        lockingHandler.setMarking(true);
    }

    private void unmark() {
        if (!marking) return;
        marking = false;
        lockingHandler.setMarking(false);
        poiEntities.setMarkedEntity(null);
        poiBlocks.setMarkedBlock(null);
        MainClass.speakWithNarrator(I18n.translate("minecraft_access.point_of_interest.marking.unmarked"), true);
    }
}
