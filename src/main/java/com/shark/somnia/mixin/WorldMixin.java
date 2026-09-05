package com.shark.somnia.mixin;

import com.shark.somnia.core.util.SomniaUtils;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = World.class)
public abstract class WorldMixin {

    @ModifyConstant(method = "manageChunkUpdatesAndEvents", constant = @Constant(intValue = 9))
    private int somnia$reduceRadius(int original) {
        return SomniaUtils.simulatedRadius > 0
                ? SomniaUtils.simulatedRadius
                : original;
    }

    @ModifyConstant(method = "manageChunkUpdatesAndEvents", constant = @Constant(intValue = 80))
    private int somnia$reduceRandomTicks(int original) {
        return SomniaUtils.simulatedBlockTicks > 0
                ? SomniaUtils.simulatedBlockTicks
                : original;
    }
}
