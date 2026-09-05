package com.shark.somnia.core.util;

import com.shark.somnia.client.gui.screen.SomniaBedScreen;
import com.shark.somnia.client.gui.widget.OptionToggleWidget;
import com.shark.somnia.core.Somnia;
import com.shark.somnia.mixin.EntityAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.glasslauncher.mods.gcapi3.mixin.client.MinecraftAccessor;
import net.minecraft.block.BedBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public class SomniaUtils {
    public static boolean SET_SPAWN = true;
    public static volatile int simulatedBlockTicks = -1;
    public static volatile int simulatedRadius = -1;

    public static boolean isClockEquipped(PlayerEntity player) {
        if (!Somnia.CONFIG.clockNeeded) return true;

        ItemStack stack = player.inventory.getSelectedItem();
        return stack != null && stack.getItem() == Item.CLOCK;
    }

    public static void toggleSpawnOption(OptionToggleWidget option) {
        setSpawnOption(option, !SET_SPAWN);
    }

    public static void setSpawnOption(OptionToggleWidget option, boolean shouldSetSpawn) {
        SET_SPAWN = shouldSetSpawn;
        option.setChecked(shouldSetSpawn);
    }

    public static void setBedOccupied(BedInfo info, boolean value) {
        BedBlock.updateState(info.getWorld(), info.getX(), info.getY(), info.getZ(), value);
    }

    public static boolean isPlayerReclining(BedInfo info, PlayerEntity player) {
        double bx = info.getX() + 0.5;
        double by = info.getY() + player.standingEyeHeight + 0.001;
        double bz = info.getZ() + 0.5;

        return (double)0.1F > Math.abs(player.x - bx) &&
                (double)0.1F > Math.abs(player.y - by) &&
                (double)0.1F > Math.abs(player.z - bz);
    }

    public static void reclinePlayer(BedInfo info, PlayerEntity player) {
        double camOff = player.standingEyeHeight + 0.001;
        int meta = info.getWorld().getBlockMeta(info.getX(), info.getY(), info.getZ());
        int dir = BedBlock.getDirection(meta);

        float yaw = 0;
        float pitch = 45;

        yaw = switch (dir) {
            case 0 -> 180;
            case 1 -> 270;
            case 2 -> 0;
            case 3 -> 90;
            default -> yaw;
        };

        double bx = info.getX() + 0.5;
        double by = info.getY() + camOff;
        double bz = info.getZ() + 0.5;

        player.setPosition(bx, by, bz);
        ((EntityAccessor) player).somnia$setRotation(yaw, pitch);
        ((EntityAccessor) player).somnia$setVelocityX(0);
        ((EntityAccessor) player).somnia$setVelocityY(0);
        ((EntityAccessor) player).somnia$setVelocityZ(0);
    }

    public static long getWorldTime(BedInfo bedInfo) {
        return bedInfo.getWorld().getTime();
    }

    public static boolean setSpawn(PlayerEntity player, BedInfo bedInfo) {
        Vec3i newSpawn = new Vec3i(bedInfo.getX(), bedInfo.getY(), bedInfo.getZ());
        Vec3i actualSpawnLocation = PlayerEntity.findRespawnPosition(bedInfo.getWorld(), newSpawn);

        if(actualSpawnLocation != null) {
            player.setSpawnPos(newSpawn);
            return true;
        }

        return false;
    }

    public static String getCurrentFormattedTime(BedInfo bedInfo) {
        return formatTime(getWorldTime(bedInfo));
    }

    public static String formatTime(long timeOfDay) {
        int hours = Math.toIntExact((timeOfDay + 6000L) % 24000L / 1000L);
        int minutes = Math.toIntExact(timeOfDay % 1000L * 60L / 1000L);

        String ampm = "AM";
        if (hours > 12) {
            hours -= 12;
            ampm = "PM";
        }

        if (hours == 0) {
            hours = 12;
        }

        return hours + ":" + padToTwoDigits(minutes) + " " + ampm;
    }

    private static String padToTwoDigits(int i) {
        return (i < 10 ? "0" : "") + i;
    }

    public static float getHealRate(World world) {
        SomniaConfig.MainConfig config = Somnia.CONFIG;
        return switch (world.difficulty) {
            case 0 -> config.healPeaceful;
            case 1 -> config.healEasy;
            case 2 -> config.healNormal;
            case 3 -> config.healHard;
            default -> 0F;
        };
    }

    public static boolean isValid(PlayerEntity player) {
        World world = player.world;
        return !world.isRemote && !world.dimension.isNether && player.isAlive() && !player.isSleeping();
    }

    @Environment(EnvType.CLIENT)
    public static void tryOpenGui(PlayerEntity player, int x, int y, int z) {
        Material m = player.world.getMaterial(x, y + 1, z);
        if (!m.isSolid() && !m.isFluid()) openGui(player, x, y, z);
        else player.sendMessage("Bed is blocked");
    }

    @Environment(EnvType.CLIENT)
    private static void openGui(PlayerEntity player, int x, int y, int z) {
        BedInfo bedInfo = new BedInfo();
        bedInfo.setWorld(MinecraftAccessor.getInstance().world);
        bedInfo.setX(x);
        bedInfo.setY(y);
        bedInfo.setZ(z);

        MinecraftAccessor.getInstance().setScreen(new SomniaBedScreen(player, bedInfo));
    }

    public static class BedInfo {
        private World world;
        private int x, y, z;

        public World getWorld() {
            return world;
        }

        public void setWorld(World world) {
            this.world = world;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getZ() {
            return z;
        }

        public void setZ(int z) {
            this.z = z;
        }
    }
}