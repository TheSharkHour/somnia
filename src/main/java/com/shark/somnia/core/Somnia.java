package com.shark.somnia.core;

import com.shark.somnia.core.util.SomniaConfig;
import net.fabricmc.api.ModInitializer;
import net.glasslauncher.mods.gcapi3.api.ConfigRoot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Somnia implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("somnia");

    @ConfigRoot(value = "main", visibleName = "Main Options", index = 0)
    public static final SomniaConfig.MainConfig CONFIG = new SomniaConfig.MainConfig();

    @Override
    public void onInitialize() {
        LOGGER.info("SomniaMod has been loaded.");
    }
}
