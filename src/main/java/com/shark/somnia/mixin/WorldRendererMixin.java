package com.shark.somnia.mixin;

import com.shark.somnia.core.util.SomniaUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void somnia$skipWorldRenderer(CallbackInfo ci) {
        if (SomniaUtils.isSimulating)
            ci.cancel();
    }
}
