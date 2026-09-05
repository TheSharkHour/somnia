package com.shark.somnia.core.util;

import net.glasslauncher.mods.gcapi3.api.ConfigEntry;

public class SomniaConfig {
    public static class MainConfig {
        @ConfigEntry(name = "Peaceful Healing", description = "How much the player heals on Peaceful.")
        public Float healPeaceful = 1.0F;

        @ConfigEntry(name = "Easy Healing", description = "How much the player heals on Easy.")
        public Float healEasy = 0.5F;

        @ConfigEntry(name = "Normal Healing", description = "How much the player heals on Normal.")
        public Float healNormal = 0.25F;

        @ConfigEntry(name = "Hard Healing", description = "How much the player heals on Hard.")
        public Float healHard = 0.0F;

        @ConfigEntry(name = "Clock Needed", description = "Whether the player needs a clock for advanced time.")
        public Boolean clockNeeded = true;
    }

    public static class OptimizationConfig {
        @ConfigEntry(name = "Path Ticks", description = "How many times entity paths will tick. Set to -1 for the vanilla value.", minValue = -1, maxValue = 10)
        public Integer pathTicks = -1;

        @ConfigEntry(name = "Radius", description = "How far things will be updated. Set to -1 for the vanilla value.", minValue = -1, maxValue = 9)
        public Integer radius = -1;

        @ConfigEntry(name = "Random Ticks", description = "How many times blocks randomly tick. Set to -1 for the vanilla value.", minValue = -1, maxValue = 80)
        public Integer randomBlockTicks = -1;
    }
}
