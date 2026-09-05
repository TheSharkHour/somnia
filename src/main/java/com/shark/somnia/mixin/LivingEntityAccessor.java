package com.shark.somnia.mixin;

import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
    @Invoker("getSoundVolume")
    float somnia$getSoundVolume();

    @Invoker("getHurtSound")
    String somnia$getHurtSound();

    @Invoker("getDeathSound")
    String somnia$getDeathSound();
}
