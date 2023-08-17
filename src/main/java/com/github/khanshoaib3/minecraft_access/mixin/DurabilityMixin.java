package com.github.khanshoaib3.minecraft_access.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = ItemStack.class, priority = 0)
public class DurabilityMixin {
    @Inject(at = @At("RETURN"), method = "getTooltip")
    private void getTooltipMixin(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> info) {
        if (MinecraftClient.getInstance().world == null) return;
        List<Text> list = info.getReturnValue();
        ItemStack itemStack = (ItemStack) ((Object) this);
        if (!itemStack.getItem().isDamageable()) return;


        int totalDurability = itemStack.getItem().getMaxDamage();
        int remainingDurability = totalDurability - itemStack.getDamage();
        list.add(1, new LiteralText((I18n.translate("minecraft_access.other.durability", remainingDurability, totalDurability).formatted(Formatting.GREEN))));
    }
}
