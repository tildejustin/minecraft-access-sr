package com.github.khanshoaib3.minecraft_access.mixin;


import com.github.khanshoaib3.minecraft_access.MainClass;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Narrates/Speaks the currently selected hotbar item's name and the action bar.
 */
@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Shadow
    private int heldItemTooltipFade;

    @Shadow
    private ItemStack currentStack;

    @Unique
    private String minecraft_access$previousContent = "";

    @Inject(at = @At("TAIL"), method = "renderHeldItemTooltip")
    public void renderHeldItemTooltipMixin(MatrixStack matrixStack, CallbackInfo callbackInfo) {
        if (this.heldItemTooltipFade == 38 && !this.currentStack.isEmpty()/*FIXME && Config.get(Config.getHelditemnarratorkey())*/) {
            MutableText mutableText = new LiteralText("")
                    .append(String.valueOf(this.currentStack.getCount()))
                    .append(" ")
                    .append(this.currentStack.getName())
                    .formatted(this.currentStack.getRarity().formatting);

            String toSpeak = mutableText.getString();
            if (!this.minecraft_access$previousContent.equals(toSpeak)) {
                MainClass.speakWithNarrator(I18n.translate("minecraft_access.other.hotbar", toSpeak), true);
                this.minecraft_access$previousContent = toSpeak;
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "setOverlayMessage(Lnet/minecraft/text/Text;Z)V")
    public void speakActionbar(Text message, boolean tinted, CallbackInfo ci) {
        if (MainClass.config.getConfigMap().getOtherConfigsMap().isActionBarEnabled())
            MainClass.speakWithNarrator(message.getString(), true);
    }
}
