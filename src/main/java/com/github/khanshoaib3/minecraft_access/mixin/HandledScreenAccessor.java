package com.github.khanshoaib3.minecraft_access.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Environment(EnvType.CLIENT)
@Mixin(HandledScreen.class)
public interface HandledScreenAccessor {
    @Accessor("playerInventoryTitleX")
    int getPlayerInventoryTitleX();

    @Accessor("playerInventoryTitleY")
    int getPlayerInventoryTitleY();

    @Accessor("x")
    int getX();

    @Accessor("y")
    int getY();

    @Accessor("handler")
    ScreenHandler getHandler();

    @Accessor("focusedSlot")
    Slot getFocusedSlot();

    @Accessor("focusedSlot")
    public void setFocusedSlot(Slot slot);
}