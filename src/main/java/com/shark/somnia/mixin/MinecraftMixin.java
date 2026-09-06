package com.shark.somnia.mixin;

import com.shark.somnia.core.util.SomniaUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Environment(EnvType.CLIENT)
@Mixin(value = Minecraft.class)
public abstract class MinecraftMixin {
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;updateCamera()V"))
    private void somnia$skipGameRendererCamera(GameRenderer instance) {
        if (!SomniaUtils.isSimulating) {
            instance.updateCamera();
        }
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;tick()V"))
    private void somnia$skipWorldRendererTick(WorldRenderer instance) {
        if (!SomniaUtils.isSimulating) {
            instance.tick();
        }
    }
}
