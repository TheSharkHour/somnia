package com.shark.somnia.client.gui.screen;

import com.shark.somnia.client.gui.widget.OptionToggleWidget;
import com.shark.somnia.core.Somnia;
import com.shark.somnia.core.util.SomniaUtils;
import com.shark.somnia.mixin.LivingEntityAccessor;
import com.shark.somnia.mixin.WorldAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.option.Option;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;

import java.util.Random;

/**
 * @author TheSharkHour
 * @since 09/04/2026
 * <p>
 *     Client-side screen that shows up when you use a bed.
 * </p>
 */
@Environment(EnvType.CLIENT)
public class SomniaBedScreen extends Screen {
    private static final int TIME_SUNRISE_AFTER     = 0;
    private static final int TIME_MORNING_EARLY     = 1500;
    private static final int TIME_MORNING_MIDDLE    = 3000;
    private static final int TIME_MORNING_LATE      = 4500;
    private static final int TIME_MIDDAY            = 6000;
    private static final int TIME_AFTERNOON_EARLY   = 7500;
    private static final int TIME_AFTERNOON_MIDDLE  = 9000;
    private static final int TIME_AFTERNOON_LATE    = 10500;
    private static final int TIME_SUNSET_BEFORE     = 12000;
    private static final int TIME_DUSK              = 12600;
    private static final int TIME_SUNSET_MIDDLE     = 12900;
    private static final int TIME_SUNSET_AFTER      = 13800;
    private static final int TIME_MIDNIGHT_BEFORE   = 16000;
    private static final int TIME_MIDNIGHT          = 18000;
    private static final int TIME_MIDNIGHT_AFTER    = 20000;
    private static final int TIME_SUNRISE_BEFORE    = 22200;
    private static final int TIME_SUNRISE_MIDDLE    = 23100;
    private static final int TIME_DAWN              = 23400;
    private static final int DAY_LENGTH             = 24000;

    private final PlayerEntity player;
    private boolean keepSleeping;

    private final SomniaUtils.BedInfo bedInfo;

    private boolean playHurtSound = false;
    private boolean playDeathSound = false;
    private boolean hasClock = false;

    private float originalSoundVolume;
    private boolean originalFancyGraphics;
    private int lastPlayerHealth;
    private int originalSaveInternal;

    /**
     * Main constructor
     * @param player Player
     * @param bedInfo Bed Information, such as Coordinates
     */
    public SomniaBedScreen(PlayerEntity player, SomniaUtils.BedInfo bedInfo) {
        this.player = player;
        this.keepSleeping = false;
        this.bedInfo = bedInfo;
    }

    /**
     * Render method.<br>
     * This displays the "Sleep until" text, as well as formatted in-game time.
     * @param mouseX Mouse X position
     * @param mouseY Mouse Y Position
     * @param delta Partial ticks
     */
    @Override
    public void render(int mouseX, int mouseY, float delta) {
        renderBackground();

        String msg = hasClock ? "Sleep until...?" : "Sleep until " + getNextTransitionString() + "?";

        drawCenteredTextWithShadow(textRenderer, msg, width / 2, height / 2 - 4, 0xffffff);

        if (hasClock) {
            drawCenteredTextWithShadow(textRenderer, SomniaUtils.getCurrentFormattedTime(bedInfo), width / 2, height / 2 - 70, 0xffffff);
        }

        super.render(mouseX, mouseY, delta);
    }

    /**
     * A helper method to get the transition time.
     * @return Returns the transition strings.
     */
    private String getNextTransitionString() {
        int nextTransition = getNextTransitionTime();
        return getString(nextTransition);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    /**
     * Screen initialization.<br>
     * This is used to create all of the buttons, and to "recline" the player.
     */
    @Override
    public void init() {
        this.hasClock = SomniaUtils.isClockEquipped(player);
        
        int relCenterX = width / 2;
        int relCenterY = height / 2;

        if (hasClock) {
            buttons.add(this.createOption(1, relCenterX + 0, relCenterY + 22, "Cancel"));
            buttons.add(this.createOption(3, relCenterX + 0, relCenterY + 88, "Midnight"));
            buttons.add(this.createOption(4, relCenterX - 80, relCenterY + 66, "After Midnight"));
            buttons.add(this.createOption(5, relCenterX - 110, relCenterY + 44, "Before Sunrise"));
            buttons.add(this.createOption(6, relCenterX - 130, relCenterY + 22, "Mid Sunrise"));
            buttons.add(this.createOption(7, relCenterX - 140, relCenterY + 0, "After Sunrise"));
            buttons.add(this.createOption(8, relCenterX - 130, relCenterY - 22, "Early Morning"));
            buttons.add(this.createOption(9, relCenterX - 110, relCenterY - 44, "Mid Morning"));
            buttons.add(this.createOption(10, relCenterX - 80, relCenterY - 66, "Late Morning"));
            buttons.add(this.createOption(11, relCenterX + 0, relCenterY - 88, "Noon"));
            buttons.add(this.createOption(12, relCenterX + 80, relCenterY - 66, "Early Afternoon"));
            buttons.add(this.createOption(13, relCenterX + 110, relCenterY - 44, "Mid Afternoon"));
            buttons.add(this.createOption(14, relCenterX + 130, relCenterY - 22, "Late Afternoon"));
            buttons.add(this.createOption(15, relCenterX + 140, relCenterY + 0, "Before Sunset"));
            buttons.add(this.createOption(16, relCenterX + 130, relCenterY + 22, "Mid Sunset"));
            buttons.add(this.createOption(17, relCenterX + 110, relCenterY + 44, "After Sunset"));
            buttons.add(this.createOption(18, relCenterX + 80, relCenterY + 66, "Before Midnight"));
        } else {
            buttons.add(this.createOption(0, relCenterX - 54, relCenterY + 22, "Yes"));
            buttons.add(this.createOption(1, relCenterX + 54, relCenterY + 22, "No"));
        }

        buttons.add(this.createSpawnCheckbox(19, relCenterX, relCenterY - 22));
        playHurtSound = false;
        playDeathSound = false;
        SomniaUtils.setBedOccupied(bedInfo, true);
        if(!SomniaUtils.isPlayerReclining(bedInfo, player)) {
            SomniaUtils.reclinePlayer(bedInfo, player);
        }
    }

    /**
     * A helper method to create an "option" button.
     * @param id Button ID
     * @param x Button X position
     * @param y Button Y position
     * @param text Displayed text
     * @return a new button widget
     */
    private OptionButtonWidget createOption(int id, int x, int y, String text) {
        return new OptionButtonWidget(id, x - 50, y - 10, 100, 20, text);
    }

    /**
     * A helper method to make a new toggle button.
     * @param id Button ID
     * @param x Button X position
     * @param y Button Y position
     * @return a new toggle widget
     */
    private OptionToggleWidget createSpawnCheckbox(int id, int x, int y) {
        return new OptionToggleWidget(id, x - 50, y - 10, 100, 20, "Reset spawn", SomniaUtils.SET_SPAWN);
    }

    /**
     * Button handling.<br>
     * This handles each button's wake time, and other special functions.
     * @param button
     */
    @Override
    protected void buttonClicked(ButtonWidget button) {
        int wakeTime = -1;
        boolean closeGui = true;

        if (SomniaUtils.isPlayerReclining(bedInfo, player)) {
            switch (button.id) {
                case 0 -> wakeTime = getNextTransitionTime();
                case 1 -> closeGui = !keepSleeping;
                case 2 -> closeGui = keepSleeping;
                case 3 -> wakeTime = TIME_MIDNIGHT;
                case 4 -> wakeTime = TIME_MIDNIGHT_AFTER;
                case 5 -> wakeTime = TIME_SUNRISE_BEFORE;
                case 6 -> wakeTime = TIME_SUNRISE_MIDDLE;
                case 7 -> wakeTime = TIME_SUNRISE_AFTER;
                case 8 -> wakeTime = TIME_MORNING_EARLY;
                case 9 -> wakeTime = TIME_MORNING_MIDDLE;
                case 10 -> wakeTime = TIME_MORNING_LATE;
                case 11 -> wakeTime = TIME_MIDDAY;
                case 12 -> wakeTime = TIME_AFTERNOON_EARLY;
                case 13 -> wakeTime = TIME_AFTERNOON_MIDDLE;
                case 14 -> wakeTime = TIME_AFTERNOON_LATE;
                case 15 -> wakeTime = TIME_SUNSET_BEFORE;
                case 16 -> wakeTime = TIME_SUNSET_MIDDLE;
                case 17 -> wakeTime = TIME_SUNSET_AFTER;
                case 18 -> wakeTime = TIME_MIDNIGHT_BEFORE;
                case 19 -> SomniaUtils.toggleSpawnOption((OptionToggleWidget) button);
            }
        }

        if (!keepSleeping && wakeTime >= 0) {
            if (SomniaUtils.SET_SPAWN && !SomniaUtils.setSpawn(player, bedInfo)) {
                minecraft.inGameHud.addChatMessage("Spawn point not set. Your bed is missing or obstructed.");
            }

            sleepUntil(wakeTime);
        } else {
            if (closeGui)
                wake();
        }
    }

    /**
     * A helper method to wake the player.<br>
     * Also plays a hurt or death sound if it's needed.
     */
    private void wake() {
        SomniaUtils.setBedOccupied(bedInfo,false);
        minecraft.setScreen(null);
        keepSleeping = false;

        LivingEntityAccessor accesses = ((LivingEntityAccessor) player);
        Random random = new Random();

        if (playHurtSound) {
            minecraft.world.playSound(player,
                    accesses.somnia$getHurtSound(),
                    accesses.somnia$getSoundVolume(),
                    (random.nextFloat() - random.nextFloat()) * 0.2F + 1F);
            playHurtSound = false;
        }

        if (playDeathSound) {
            minecraft.world.playSound(player,
                    accesses.somnia$getDeathSound(),
                    accesses.somnia$getSoundVolume(),
                    (random.nextFloat() - random.nextFloat()) * 0.2F + 1F);
            playDeathSound = false;
        }
    }

    /**
     * A helper method to sleep until a certain point.<br>
     * Also handles healing, the sleep loading, and more.
     * @param wakeTime
     */
    private void sleepUntil(int wakeTime) {
        int sleepDuration = getTimeUntil(wakeTime);
        beforeSleep(sleepDuration);

        World world = minecraft.world;

        OptionButtonWidget wakeButton = createOption(2, width / 2, height / 2 + 22, "Wake up");
        buttons.add(wakeButton);

        long timeStart = System.currentTimeMillis();

        SomniaSleepingScreen sleepingScreen = new SomniaSleepingScreen(minecraft, wakeButton, sleepDuration, timeStart, hasClock, bedInfo);
        sleepingScreen.progressStart("Sleeping until " + getString(wakeTime));
        sleepingScreen.progressStage("Simulating the world");

        float healRate = SomniaUtils.getHealRate(world);
        int healTimer = 0;
        if (healRate != 0F) {
            healTimer = (int) Math.floor(500F / healRate);
        }

        lastPlayerHealth = player.health;
        minecraft.particleManager.setWorld(world);
        world.allowSpawning(world.difficulty > 0, true);
        keepSleeping = true;

        while (sleepingScreen.getTicksElapsed() < sleepDuration
                && !player.dead && player.health >= lastPlayerHealth
                && keepSleeping) {
            if (healTimer != 0 && sleepingScreen.getTicksElapsed() % healTimer == 0) {
                player.heal(1);
            }

            lastPlayerHealth = player.health;
            world.tickEntities();
            world.tick();
            sleepingScreen.tick();
            tickInput();

            if (sleepingScreen.getTicksElapsed() % 10 == 0) {
                Box.resetCacheCount();
                Vec3d.resetCacheCount();
            }
        }

        afterSleep();
    }

    /**
     * A helper method to handle after-sleep.<br>
     * Also resets the simulated content to -1.
     */
    private void afterSleep() {
        minecraft.options.setFloat(Option.SOUND, originalSoundVolume);
        minecraft.options.fancyGraphics = originalFancyGraphics;

        World world = bedInfo.getWorld();
        ((WorldAccessor) world).somnia$setSaveInterval(originalSaveInternal);

        if (!player.dead) playHurtSound = player.health < lastPlayerHealth;
        else playDeathSound = true;

        SomniaUtils.simulatedBlockTicks = -1;
        SomniaUtils.simulatedRadius = -1;

        wake();
    }

    @Override
    public void tickInput() {
        super.tickInput();
        if (!SomniaUtils.isPlayerReclining(bedInfo, player)) {
            wake();
            player.sendMessage("Bed out of range");
        }
    }

    @Override
    protected void keyPressed(char character, int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE)
            this.wake();

        super.keyPressed(character, keyCode);
    }

    /**
     * A helper method to set a string based on the time of day.
     * @param timeOfDay The time of day
     * @return the relevant time string.
     */
    private String getString(int timeOfDay) {
        return switch (timeOfDay) {
            case TIME_SUNRISE_AFTER -> "after sunrise";
            case TIME_MORNING_EARLY -> "early morning";
            case TIME_MORNING_MIDDLE -> "mid morning";
            case TIME_MORNING_LATE -> "late morning";
            case TIME_MIDDAY -> "noon";
            case TIME_AFTERNOON_EARLY -> "early afternoon";
            case TIME_AFTERNOON_MIDDLE -> "mid afternoon";
            case TIME_AFTERNOON_LATE -> "late afternoon";
            case TIME_SUNSET_BEFORE -> "before sunset";
            case TIME_DUSK -> "dusk";
            case TIME_SUNSET_MIDDLE -> "mid sunset";
            case TIME_SUNSET_AFTER -> "after sunset";
            case TIME_MIDNIGHT_BEFORE -> "before midnight";
            case TIME_MIDNIGHT -> "midnight";
            case TIME_MIDNIGHT_AFTER -> "after midnight";
            case TIME_SUNRISE_BEFORE -> "before sunrise";
            case TIME_SUNRISE_MIDDLE -> "mid sunrise";
            case TIME_DAWN -> "dawn";
            default -> SomniaUtils.formatTime(timeOfDay);
        };
    }

    /**
     * A helper method to handle BEFORE sleeping.<br>
     * Also (temporarily) disables audio, fancy graphics, and save interval.
     * @param sleepDuration How long to sleep for.
     */
    private void beforeSleep(int sleepDuration) {
        originalSoundVolume = minecraft.options.getFloat(Option.SOUND);
        originalFancyGraphics = minecraft.options.fancyGraphics;

        minecraft.options.setFloat(Option.SOUND, 0F);
        minecraft.options.fancyGraphics = false;

        World world = bedInfo.getWorld();
        originalSaveInternal = ((WorldAccessor) world).somnia$saveInterval();

        int tempSaveInterval = calculateDisabledSaveInterval(world.getTime(), sleepDuration);
        ((WorldAccessor) world).somnia$setSaveInterval(tempSaveInterval);

        SomniaUtils.simulatedBlockTicks = Somnia.CONFIG_OPTIMIZATIONS.randomBlockTicks;
        SomniaUtils.simulatedRadius = Somnia.CONFIG_OPTIMIZATIONS.radius;
    }

    private int calculateDisabledSaveInterval(long worldTime, int sleepDuration) {
        int result = Integer.MAX_VALUE;
        while (worldTime / result != (worldTime + sleepDuration) / result) {
            result--;
        }

        return result;
    }

    private int getTimeUntil(int timeOfDay) {
        int result = timeOfDay - getTimeOfDay();
        return result < 0 ? result + DAY_LENGTH : result;
    }

    private int getNextTransitionTime() {
        int start = getTimeOfDay();
        boolean duskNext = start < TIME_DUSK || start >= TIME_DAWN;
        return duskNext ? TIME_DUSK : TIME_DAWN;
    }

    private int getTimeOfDay() {
        return Math.toIntExact(SomniaUtils.getWorldTime(bedInfo) % DAY_LENGTH);
    }
}
