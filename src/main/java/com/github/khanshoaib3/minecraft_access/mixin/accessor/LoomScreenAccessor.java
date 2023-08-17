package com.github.khanshoaib3.minecraft_access.mixin.accessor;

import net.minecraft.client.gui.screen.ingame.LoomScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LoomScreen.class)
public interface LoomScreenAccessor {
    @Accessor
    boolean isCanApplyDyePattern();

    @Accessor("firstPatternButtonId")
    int getVisibleTopRow();
}
