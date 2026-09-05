package com.shark.somnia.mixin;

import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = World.class)
public interface WorldAccessor {
    @Accessor("saveInterval")
    int somnia$saveInterval();

    @Accessor("saveInterval")
    void somnia$setSaveInterval(int newInterval);
}
