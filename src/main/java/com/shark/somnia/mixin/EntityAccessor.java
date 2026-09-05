package com.shark.somnia.mixin;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Random;

@Mixin(value = Entity.class)
public interface EntityAccessor {
    @Invoker("setRotation")
    void somnia$setRotation(float yaw, float pitch);

    @Accessor("velocityX")
    void somnia$setVelocityX(double velocity);

    @Accessor("velocityY")
    void somnia$setVelocityY(double velocity);

    @Accessor("velocityZ")
    void somnia$setVelocityZ(double velocity);
}
