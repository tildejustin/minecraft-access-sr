package com.github.khanshoaib3.minecraft_access.features;

import com.github.khanshoaib3.minecraft_access.MainClass;
import com.github.khanshoaib3.minecraft_access.config.Config;
import com.github.khanshoaib3.minecraft_access.config.config_maps.RCPartialSpeakingConfigMap;
import com.github.khanshoaib3.minecraft_access.config.config_maps.ReadCrosshairConfigMap;
import com.github.khanshoaib3.minecraft_access.mixin.accessor.MobSpawnerLogicAccessor;
import com.github.khanshoaib3.minecraft_access.utils.PlayerPositionUtils;
import com.github.khanshoaib3.minecraft_access.utils.TimeUtils;
import net.minecraft.block.*;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.enums.ComparatorMode;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Predicate;

/**
 * This feature reads the name of the targeted block or entity.<br>
 * It also gives feedback when a block is powered by a redstone signal or when a door is open similar cases.
 */
public class ReadCrosshair {
    private String previousQuery;
    private boolean speakSide;
    private boolean speakingConsecutiveBlocks;
    private TimeUtils.Interval repeatSpeakingInterval;
    private boolean enablePartialSpeaking;
    private boolean partialSpeakingWhitelistMode;
    private boolean partialSpeakingFuzzyMode;
    private List<String> partialSpeakingTargets;
    private boolean partialSpeakingBlock;
    private boolean partialSpeakingEntity;

    public ReadCrosshair() {
        previousQuery = "";
        loadConfigurations();
    }

    public String getPreviousQuery() {
        if (repeatSpeakingInterval.isReady()) {
            this.previousQuery = "";
        }
        return this.previousQuery;
    }

    public void update() {
        try {
            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            if (minecraftClient == null) return;
            if (minecraftClient.world == null) return;
            if (minecraftClient.player == null) return;
            if (minecraftClient.currentScreen != null) return;

            loadConfigurations();

            Entity entity = minecraftClient.getCameraEntity();
            if (entity == null) return;

            HitResult blockHit = minecraftClient.crosshairTarget;
            HitResult fluidHit = entity.rayTrace(6.0, 0.0F, true);

            if (blockHit == null) return;

            if (!minecraftClient.player.isSwimming() && !minecraftClient.player.isSubmergedInWater() && !minecraftClient.player.isInsideWaterOrBubbleColumn() && !minecraftClient.player.isInLava() && checkForFluidHit(minecraftClient, fluidHit))
                return;

            checkForBlockAndEntityHit(minecraftClient, blockHit);
        } catch (Exception e) {
            MainClass.errorLog("Error occurred in read block feature.\n%s".formatted(e.getMessage()));
        }
    }

    private void loadConfigurations() {
        // It is best to get the config map from instance of Config class rather than directly from
        // the ReadCrosshairConfigMap class because in the case of an error in the config.json,
        // while it does get reset to default but the mod crashes as well. So to avoid the mod from crashing,
        // use the instance of Config class to get instances of other config maps.
        ReadCrosshairConfigMap rcMap = Config.getInstance().getConfigMap().getReadCrosshairConfigMap();
        RCPartialSpeakingConfigMap rcpMap = rcMap.getPartialSpeakingConfigMap();

        this.speakSide = rcMap.isSpeakSide();
        // affirmation for easier use
        this.speakingConsecutiveBlocks = !rcMap.isDisableSpeakingConsecutiveBlocks();
        long interval = rcMap.getRepeatSpeakingInterval();
        this.repeatSpeakingInterval = TimeUtils.Interval.inMilliseconds(interval, this.repeatSpeakingInterval);

        this.enablePartialSpeaking = rcpMap.isEnabled();
        this.partialSpeakingFuzzyMode = rcpMap.isPartialSpeakingFuzzyMode();
        this.partialSpeakingWhitelistMode = rcpMap.isPartialSpeakingWhitelistMode();
        this.partialSpeakingTargets = rcpMap.getPartialSpeakingTargets();
        switch (rcpMap.getPartialSpeakingTargetMode()) {
            case ALL -> {
                partialSpeakingBlock = true;
                partialSpeakingEntity = true;
            }
            case BLOCK -> {
                partialSpeakingBlock = true;
                partialSpeakingEntity = false;
            }
            case ENTITY -> {
                partialSpeakingBlock = false;
                partialSpeakingEntity = true;
            }
        }
    }

    private void checkForBlockAndEntityHit(MinecraftClient minecraftClient, HitResult blockHit) {
        switch (blockHit.getType()) {
            case MISS -> {
            }
            case BLOCK -> checkForBlocks(minecraftClient, (BlockHitResult) blockHit);
            case ENTITY -> checkForEntities((EntityHitResult) blockHit);
        }
    }

    private void checkForEntities(EntityHitResult hit) {
        try {
            Entity entity = hit.getEntity();

            if (enablePartialSpeaking && partialSpeakingEntity) {
                if (checkIfPartialSpeakingFeatureDoesNotAllowsSpeakingThis(EntityType.getId(entity.getType()))) return;
            }

            String currentQuery = entity.getName().getString();
            if (entity instanceof AnimalEntity animalEntity) {
                if (animalEntity instanceof SheepEntity sheepEntity) {
                    currentQuery = getSheepInfo(sheepEntity, currentQuery);
                }
                if (animalEntity.isBaby())
                    currentQuery = I18n.translate("minecraft_access.read_crosshair.animal_entity_baby", currentQuery);
                if (animalEntity.isLeashed())
                    currentQuery = I18n.translate("minecraft_access.read_crosshair.animal_entity_leashed", currentQuery);
            }

            speakIfFocusChanged(currentQuery, currentQuery);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getSheepInfo(SheepEntity sheepEntity, String currentQuery) {
        String dyedColor = sheepEntity.getColor().getName();
        String translatedColor = I18n.translate("minecraft_access.color." + dyedColor);
        String shearable = sheepEntity.isShearable() ?
                I18n.translate("minecraft_access.other.shearable", currentQuery) :
                I18n.translate("minecraft_access.other.not_shearable", currentQuery);
        return translatedColor + " " + shearable;
    }

    /**
     * @param currentQuery for checking if focus is changed
     * @param toSpeak      text will be narrated (if focus has changed)
     */
    private void speakIfFocusChanged(String currentQuery, String toSpeak) {
        boolean focusChanged = !getPreviousQuery().equalsIgnoreCase(currentQuery);
        if (focusChanged) {
            this.previousQuery = currentQuery;
            MainClass.speakWithNarrator(toSpeak, true);
        }
    }

    private void checkForBlocks(MinecraftClient minecraftClient, BlockHitResult hit) {
        ClientWorld clientWorld = minecraftClient.world;
        if (clientWorld == null) return;

        // Since Minecraft uses flyweight pattern for blocks and entities,
        // All same type of blocks share one singleton Block instance,
        // While every block keep their states with a BlockState instance.
        BlockPos blockPos = hit.getBlockPos();
        BlockState blockState = clientWorld.getBlockState(blockPos);
        Block block = blockState.getBlock();

        if (enablePartialSpeaking && partialSpeakingBlock) {
            if (checkIfPartialSpeakingFeatureDoesNotAllowsSpeakingThis(Registry.BLOCK.getId(block))) return;
        }

        String name = block.getName().getString();
        String toSpeak = name;

        String side = "";
        if (this.speakSide) {
            Direction d = hit.getSide();
            side = I18n.translate("minecraft_access.direction." + d.getName());
        }
        toSpeak += " " + side;

        // Difference between toSpeak and currentQuery:
        // currentQuery is used for checking condition, toSpeak is actually the one to be spoken.
        // currentQuery is checked to not speak the same block repeatedly, two blocks can have same name.
        String currentQuery = name + side;

        // If this config is enabled, add position info to currentQuery,
        // so same blocks at different positions will be regard as different one then trigger the narrator.
        // Class name in production environment can be different
        String blockPosInString = blockPos.toString();
        if (this.speakingConsecutiveBlocks) currentQuery += " " + blockPosInString;

        // Different special narration (toSpeak) about different type of blocks
        try {
            BlockEntity blockEntity = clientWorld.getBlockEntity(blockPos);
            if (blockEntity != null) {
                // The all signs tag include all types of signs, so it should also work with the hanging signs in 1.20.x
                if (blockState.isIn(BlockTags.SIGNS)) {
                    toSpeak = getSignInfo((SignBlockEntity) blockEntity, toSpeak);
                } else if (blockEntity instanceof BeehiveBlockEntity beehiveBlockEntity) {
                    Pair<String, String> beehiveInfo = getBeehiveInfo(beehiveBlockEntity, blockState, toSpeak, currentQuery);
                    toSpeak = beehiveInfo.getLeft();
                    currentQuery = beehiveInfo.getRight();
                }
            }

            if (block instanceof CropBlock || block instanceof CocoaBlock || block instanceof NetherWartBlock) {
                Pair<String, String> cropsInfo = getCropsInfo(block, blockState, toSpeak, currentQuery);
                toSpeak = cropsInfo.getLeft();
                currentQuery = cropsInfo.getRight();
            }

            // Check if farmland is wet
            // TODO: fix hardcode max
            if (block instanceof FarmlandBlock && blockState.get(FarmlandBlock.MOISTURE) == 7) {
                toSpeak = I18n.translate("minecraft_access.crop.wet_farmland", toSpeak);
                currentQuery = I18n.translate("minecraft_access.crop.wet_farmland", currentQuery);
            }

            // Speak monster spawner mob type
            if (blockEntity instanceof MobSpawnerBlockEntity spawner) {
                // Will not support non-vanilla custom configured multiple-mob spawner (like generated with command)
                Entity entity = ((MobSpawnerLogicAccessor) spawner.getLogic()).getRenderedEntity();
                // Monster spawners that gotten from creative creating screen is empty.
                String entityName = "Empty";
                if (entity != null) {
                    entityName = entity.getDisplayName().getString();
                }
                toSpeak = entityName + " " + toSpeak;
                currentQuery = entityName + currentQuery;
            }

            // Redstone related
            Pair<String, String> redstoneRelatedInfo = getRedstoneRelatedInfo(clientWorld, blockPos, block, blockState, toSpeak, currentQuery);
            toSpeak = redstoneRelatedInfo.getLeft();
            currentQuery = redstoneRelatedInfo.getRight();

        } catch (Exception e) {
            MainClass.errorLog("An error occurred while adding narration text for special blocks");
            e.printStackTrace();
        }

        speakIfFocusChanged(currentQuery, toSpeak);
    }

    private static String getSignInfo(SignBlockEntity signEntity, String toSpeak) {
        String[] lines = new String[4];
        for (int i = 0; i < 4; i++) {
            lines[i] = signEntity.text[i].getString();
        }
        String content = String.join(", ", lines);
        return I18n.translate("minecraft_access.read_crosshair.sign_content", toSpeak, content);
    }

    private static @NotNull Pair<String, String> getRedstoneRelatedInfo(ClientWorld world, BlockPos blockPos, Block block, BlockState blockState, String toSpeak, String currentQuery) {
        boolean isEmittingPower = world.isEmittingRedstonePower(blockPos, Direction.DOWN);
        boolean isReceivingPower = world.isReceivingRedstonePower(blockPos);

        if (block instanceof PistonBlock) {
            String facing = blockState.get(PistonBlock.FACING).getName();
            toSpeak = I18n.translate("minecraft_access.read_crosshair.facing", toSpeak, I18n.translate("minecraft_access.direction." + facing));
            currentQuery += "facing " + facing;
            if (isReceivingPower) {
                toSpeak = I18n.translate("minecraft_access.read_crosshair.powered", toSpeak);
                currentQuery += "powered";
            }
        } else if ((block instanceof RedstoneWireBlock || block instanceof RedstoneLampBlock) && (isReceivingPower || isEmittingPower)) {
            toSpeak = I18n.translate("minecraft_access.read_crosshair.powered", toSpeak);
            currentQuery += "powered";
//        } else if ((block instanceof RedstoneTorchBlock || block instanceof LeverBlock || block instanceof AbstractButtonBlock) && isEmittingPower) { // pre 1.19.3
        } else if ((block instanceof RedstoneTorchBlock || block instanceof LeverBlock || block instanceof AbstractButtonBlock) && isEmittingPower) { // From 1.19.3
            toSpeak = I18n.translate("minecraft_access.read_crosshair.powered", toSpeak);
            currentQuery += "powered";
        } else if (block instanceof DoorBlock doorBlock && blockState.get(DoorBlock.OPEN)) {
            toSpeak = I18n.translate("minecraft_access.read_crosshair.opened", toSpeak);
            currentQuery += "open";
        } else if (block instanceof HopperBlock) {
            toSpeak = I18n.translate("minecraft_access.read_crosshair.facing", toSpeak, I18n.translate("minecraft_access.direction." + blockState.get(HopperBlock.FACING).getName()));
            currentQuery += "facing " + blockState.get(HopperBlock.FACING).getName();
            if (isReceivingPower) {
                toSpeak = I18n.translate("minecraft_access.read_crosshair.locked", toSpeak);
                currentQuery += "locked";
            }
        } else if (block instanceof ObserverBlock) {
            toSpeak = I18n.translate("minecraft_access.read_crosshair.facing", toSpeak, I18n.translate("minecraft_access.direction." + blockState.get(ObserverBlock.FACING).getName()));
            currentQuery += "facing " + blockState.get(ObserverBlock.FACING).getName();
            if (isEmittingPower) {
                toSpeak = I18n.translate("minecraft_access.read_crosshair.powered", toSpeak);
                currentQuery += "powered";
            }
        } else if (block instanceof DispenserBlock) {
            toSpeak = I18n.translate("minecraft_access.read_crosshair.facing", toSpeak, I18n.translate("minecraft_access.direction." + blockState.get(DispenserBlock.FACING).getName()));
            currentQuery += "facing " + blockState.get(DispenserBlock.FACING).getName();
            if (isReceivingPower) {
                toSpeak = I18n.translate("minecraft_access.read_crosshair.powered", toSpeak);
                currentQuery += "powered";
            }
        } else if (block instanceof ComparatorBlock) {
            ComparatorMode mode = blockState.get(ComparatorBlock.MODE);
            Direction facing = blockState.get(ComparatorBlock.FACING);
            String correctFacing = I18n.translate("minecraft_access.direction." + PlayerPositionUtils.getOppositeDirectionKey(facing.getName()).toLowerCase());
            toSpeak = I18n.translate("minecraft_access.read_crosshair.comparator_info", toSpeak, correctFacing, mode);
            if (isReceivingPower) {
                toSpeak = I18n.translate("minecraft_access.read_crosshair.powered", toSpeak);
                currentQuery += "powered";
            }
            currentQuery += "mode:" + mode + " facing:" + correctFacing;
        } else if (block instanceof RepeaterBlock) {
            boolean locked = blockState.get(RepeaterBlock.LOCKED);
            int delay = blockState.get(RepeaterBlock.DELAY);
            Direction facing = blockState.get(ComparatorBlock.FACING);
            String correctFacing = I18n.translate("minecraft_access.direction." + PlayerPositionUtils.getOppositeDirectionKey(facing.getName()).toLowerCase());

            toSpeak = I18n.translate("minecraft_access.read_crosshair.repeater_info", toSpeak, correctFacing, delay);
            currentQuery += "delay:" + delay + " facing:" + correctFacing;
            if (locked) {
                toSpeak = I18n.translate("minecraft_access.read_crosshair.locked", toSpeak);
                currentQuery += "locked";
            }
        } else if (isReceivingPower) { // For all the other blocks
            toSpeak = I18n.translate("minecraft_access.read_crosshair.powered", toSpeak);
            currentQuery += "powered";
        }

        return new Pair<>(toSpeak, currentQuery);
    }

    private static @NotNull Pair<String, String> getBeehiveInfo(BeehiveBlockEntity blockEntity, BlockState blockState, String toSpeak, String currentQuery) {
        boolean isSmoked = blockEntity.isSmoked();
        int honeyLevel = blockState.get(BeehiveBlock.HONEY_LEVEL);
        Direction facingDirection = blockState.get(BeehiveBlock.FACING);

        if (isSmoked) {
            toSpeak = I18n.translate("minecraft_access.read_crosshair.bee_hive_smoked", toSpeak);
            currentQuery += "smoked";
        }

        if (honeyLevel > 0) {
            toSpeak = I18n.translate("minecraft_access.read_crosshair.bee_hive_honey_level", toSpeak, honeyLevel);
            currentQuery += ("honey-level:" + honeyLevel);
        }

        toSpeak = I18n.translate("minecraft_access.read_crosshair.bee_hive_facing", toSpeak, facingDirection.getName());
        currentQuery += ("facing:" + facingDirection.getName());

        return new Pair<>(toSpeak, currentQuery);
    }

    /**
     * Blocks that can be planted and have growing stages (age) and harvestable.<br>
     * Including wheat, carrot, potato, beetroot, nether wart, cocoa bean.<br>
     * Watermelon vein and pumpkin vein are not harvestable so not be included here.
     */
    private static @NotNull Pair<String, String> getCropsInfo(Block block, BlockState blockState, String toSpeak, String currentQuery) {
        int currentAge = 0, maxAge = 64;

        if (block instanceof CropBlock) {
            if (block instanceof BeetrootsBlock) {
                // Beetroots has a different max_age as 3
                currentAge = blockState.get(BeetrootsBlock.AGE);
                maxAge = 5;
            } else {
                // While wheat, carrots, potatoes has max_age as 7
                currentAge = blockState.get(CropBlock.AGE);
                maxAge = 7;
            }
        } else if (block instanceof CocoaBlock) {
            currentAge = blockState.get(CocoaBlock.AGE);
            maxAge = 3;
        } else if (block instanceof NetherWartBlock) {
            currentAge = blockState.get(NetherWartBlock.AGE);
            // The max_age of NetherWartBlock hasn't been translated, for future compatibility, hard code it.
            maxAge = 3;
        }

        String configKey = checkCropRipeLevel(currentAge, maxAge);
        return new Pair<>(I18n.translate(configKey, toSpeak), I18n.translate(configKey, currentQuery));
    }

    /**
     * @return corresponding ripe level text config key
     */
    private static String checkCropRipeLevel(Integer current, int max) {
        if (current == max) {
            return "minecraft_access.crop.ripe";
        } else if (current < max / 2) {
            return "minecraft_access.crop.seedling";
        } else {
            return "minecraft_access.crop.half_ripe";
        }
    }

    private boolean checkForFluidHit(MinecraftClient minecraftClient, HitResult fluidHit) {
        if (minecraftClient == null) return false;
        if (minecraftClient.world == null) return false;
        if (minecraftClient.currentScreen != null) return false;

        if (fluidHit.getType() == HitResult.Type.BLOCK) {
            BlockPos blockPos = ((BlockHitResult) fluidHit).getBlockPos();
            FluidState fluidState = minecraftClient.world.getFluidState(blockPos);

            String name = String.valueOf(fluidState.getBlockState().getBlock().getTranslationKey());
            if (name.equals("block.minecraft.empty")) return false;
            if (name.contains("block.minecraft."))
                name = name.replace("block.minecraft.", ""); // Remove `block.minecraft.` for unsupported languages

            String currentQuery = name + blockPos;

            int level = fluidState.getLevel();
            String levelString = "";
            if (level < 8) levelString = I18n.translate("minecraft_access.read_crosshair.fluid_level", level);

            String toSpeak = name + levelString;

            speakIfFocusChanged(currentQuery, toSpeak);
            return true;
        }
        return false;
    }

    /**
     * Gets the fluid name from registry entry
     *
     * @param fluid the fluid's registry entry
     * @return the fluid's name
     */
//    public static String getFluidName(RegistryEntry<Fluid> fluid) {
//        return I18n.translate(getFluidTranslationKey(fluid));
//    }

    /**
     * Gets the fluid translation key from registry entry
     *
//     * @param fluid the fluid's registry entry
     * @return the fluid's translation key
     */
//    private static String getFluidTranslationKey(RegistryEntry<Fluid> fluid) {
//        return fluid.getKeyOrValue().map(
//                (fluidKey) -> "block." + fluidKey.getValue().getNamespace() + "." + fluidKey.getValue().getPath(),
//                (fluidValue) -> "[unregistered " + fluidValue + "]" // For unregistered fluid
//        );
//    }

    private boolean checkIfPartialSpeakingFeatureDoesNotAllowsSpeakingThis(Identifier id) {
        if (id == null) return false;
        String name = id.getPath();
        Predicate<String> p = partialSpeakingFuzzyMode ? name::contains : name::equals;
        return partialSpeakingWhitelistMode ?
                partialSpeakingTargets.stream().noneMatch(p) :
                partialSpeakingTargets.stream().anyMatch(p);
    }
}
