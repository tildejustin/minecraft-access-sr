package com.github.khanshoaib3.minecraft_access.mixin.accessor;

import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.item.ItemGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(CreativeInventoryScreen.class)
public interface CreativeInventoryScreenAccessor {
    @Accessor
    static int getSelectedTab() {
        throw new UnsupportedOperationException();
    }

    @Invoker
    void invokeSetSelectedTab(ItemGroup group);

    @Accessor
    TextFieldWidget getSearchBox();
}