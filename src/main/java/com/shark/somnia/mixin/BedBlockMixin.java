package com.shark.somnia.mixin;

import com.shark.somnia.core.util.SomniaUtils;
import net.minecraft.block.BedBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.SleepAttemptResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.block.BedBlock.updateState;

@Mixin(BedBlock.class)
public abstract class BedBlockMixin {

    @Inject(method = "onUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;trySleep(III)Lnet/minecraft/entity/player/SleepAttemptResult;"), cancellable = true)
    private void somnia$redirectBedOnUse(World world, int x, int y, int z, PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if (SomniaUtils.isValid(player)) {
            SomniaUtils.tryOpenGui(player, x, y, z);
            cir.setReturnValue(true);
        } else {
            SleepAttemptResult result = player.trySleep(x, y, z);
            if (result == SleepAttemptResult.OK) {
                updateState(world, x, y, z, true);
                cir.setReturnValue(true);
            }

            if (result == SleepAttemptResult.NOT_POSSIBLE_NOW) {
                player.sendMessage("tile.bed.noSleep");
                cir.setReturnValue(true);
            }
        }

        cir.setReturnValue(true);
    }
}
