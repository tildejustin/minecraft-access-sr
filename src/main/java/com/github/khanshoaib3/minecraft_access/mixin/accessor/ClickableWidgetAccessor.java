package com.github.khanshoaib3.minecraft_access.mixin.accessor;

import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractButtonWidget.class)
public interface ClickableWidgetAccessor {
    @Accessor
    void setMessage(Text message);

    @Accessor("x")
    int callGetX();

    @Accessor("y")
    int callGetY();
}
