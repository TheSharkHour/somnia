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
}
