package com.shark.somnia.mixin;

import com.shark.somnia.core.Somnia;
import com.shark.somnia.core.util.SomniaUtils;
import net.minecraft.entity.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MobEntity.class)
public abstract class MobEntityMixin {

    @Unique
    private int somnia$pathTickCounter = 0;

    @Inject(method = "tickLiving", at = @At("HEAD"), cancellable = true)
    private void somnia$throttlePathingDuringSleep(CallbackInfo ci) {
        int interval = Somnia.CONFIG_OPTIMIZATIONS.pathTicks;
        if (interval < 0) return;

        somnia$pathTickCounter++;
        if (somnia$pathTickCounter % interval != 0) {
            ci.cancel();
        }
    }
}
