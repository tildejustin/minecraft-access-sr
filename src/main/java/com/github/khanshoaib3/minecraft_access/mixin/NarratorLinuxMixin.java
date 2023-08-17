package com.github.khanshoaib3.minecraft_access.mixin;

import com.github.khanshoaib3.minecraft_access.MainClass;
import com.mojang.text2speech.NarratorLinux;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.NarratorOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NarratorLinux.class)
public class NarratorLinuxMixin {
    @Inject(at = @At("HEAD"), method = "say", remap = false, cancellable = true)
    public void say(String msg, boolean interrupt, CallbackInfo info) {
        if (MainClass.getScreenReader() == null || !MainClass.getScreenReader().isInitialized())
            return;

        if (this.shouldNarrateSystem(MinecraftClient.getInstance().options.narrator))
            MainClass.getScreenReader().say(msg, interrupt);

        info.cancel();
    }

    @Unique
    private boolean shouldNarrateSystem(NarratorOption narrator) {
        return narrator == NarratorOption.SYSTEM || narrator == NarratorOption.ALL;
    }
}
