package com.shark.somnia.client.gui.screen;

import com.shark.somnia.core.util.SomniaUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.LoadingDisplay;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.render.ProgressRenderError;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.util.ScreenScaler;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

/**
 * @author TheSharkHour
 * @since 09/04/2026
 * <p>
 *     Client-side loading screen for sleeping.
 * </p>
 */
@Environment(EnvType.CLIENT)
public class SomniaSleepingScreen implements LoadingDisplay {
    Minecraft minecraft;
    private final OptionButtonWidget wakeButton;
    public String stage;
    public String title;
    private long lastTime;
    private boolean noAbort;
    private final long timeStart;
    private final int ticksTotal;
    private int ticksElapsed;
    private final boolean showTime;
    private SomniaUtils.BedInfo bedInfo;

    public SomniaSleepingScreen(Minecraft minecraft, OptionButtonWidget wakeButton, int ticksTotal, long timeStart, boolean showTime, SomniaUtils.BedInfo bedInfo) {
        this.minecraft = minecraft;
        this.wakeButton = wakeButton;
        this.ticksTotal = ticksTotal;
        this.timeStart = timeStart;
        this.showTime = showTime;
        this.bedInfo = bedInfo;
        this.stage = "";
        this.title = "";
        this.lastTime = System.currentTimeMillis();
        this.ticksElapsed = 0;
    }

    public void progressStart(String title) {
        noAbort = false;
        start(title);
    }

    private void start(String title) {
        if (!this.minecraft.running) {
            if (!this.noAbort) throw new ProgressRenderError();
        } else {
            this.title = title;
            ScreenScaler scaler = new ScreenScaler(minecraft.options, minecraft.displayWidth, minecraft.displayHeight);
            GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glLoadIdentity();
            GL11.glOrtho(0, scaler.rawScaledWidth, scaler.rawScaledHeight, 0, 100, 300);
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glLoadIdentity();
            GL11.glTranslatef(0F, 0F, -200F);
        }
    }

    @Override
    public void progressStartNoAbort(String title) {
        this.noAbort = true;
        this.start(this.title);
    }

    @Override
    public void progressStage(String stage) {
        if (!this.minecraft.running) {
            if (!this.noAbort) {
                throw new ProgressRenderError();
            }
        } else {
            this.lastTime = 0L;
            this.stage = stage;
            this.progressStagePercentage(-1);
            this.lastTime = 0L;
        }
    }

    @Override
    public void progressStagePercentage(int percentage) {
        if (!this.minecraft.running) {
            if (!this.noAbort) throw new ProgressRenderError();
        } else {
            long now = System.currentTimeMillis();
            if (now - lastTime >= 20L) {
                lastTime = now;

                ScreenScaler scaler = new ScreenScaler(this.minecraft.options, this.minecraft.displayWidth, this.minecraft.displayHeight);
                int screenWidth = scaler.getScaledWidth();
                int screenHeight = scaler.getScaledHeight();

                GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
                GL11.glMatrixMode(GL11.GL_PROJECTION);
                GL11.glLoadIdentity();
                GL11.glOrtho(0F, scaler.rawScaledWidth, scaler.rawScaledHeight, 0F, 100F, 300F);
                GL11.glMatrixMode(GL11.GL_MODELVIEW);
                GL11.glLoadIdentity();
                GL11.glTranslatef(0F, 0F, -200F);

                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

                Tessellator t = Tessellator.INSTANCE;
                int id = this.minecraft.textureManager.getTextureId("/gui/background.png");
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
                float s = 32.0F;
                t.startQuads();
                t.color(0x404040);
                t.vertex(0.0F, screenHeight, 0.0F, 0.0F, (float) screenHeight / s);
                t.vertex(screenWidth, screenHeight, 0.0F, (float) screenWidth / s, (float) screenHeight / s);
                t.vertex(screenWidth, 0.0F, 0.0F, (float) screenWidth / s, 0.0F);
                t.vertex(0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
                t.draw();

                if (percentage >= 0) {
                    int w = 100;
                    int h = 2;
                    int x = screenWidth / 2 - w / 2;
                    int y = screenHeight / 2 + 16;

                    GL11.glDisable(GL11.GL_TEXTURE_2D);
                    t.startQuads();
                    t.color(0x808080);
                    t.vertex(x, y, 0.0F);
                    t.vertex(x, y + h, 0.0F);
                    t.vertex(x + w, y + h, 0.0F);
                    t.vertex(x + w, y, 0.0F);

                    t.color(0x80ff80);
                    t.vertex(x, y, 0.0F);
                    t.vertex(x, y + h, 0.0F);
                    t.vertex(x + percentage, y + h, 0.0F);
                    t.vertex(x + percentage, y, 0.0F);
                    t.draw();

                    GL11.glEnable(GL11.GL_TEXTURE_2D);
                }

                if (showTime) {
                    String x1 = SomniaUtils.getCurrentFormattedTime(bedInfo);
                    minecraft.textRenderer.drawWithShadow(x1,(screenWidth - minecraft.textRenderer.getWidth(x1)) / 2, screenHeight / 2 - 70, 0xffffff);
                }

                this.minecraft.textRenderer.drawWithShadow(this.title, (screenWidth - this.minecraft.textRenderer.getWidth(this.title)) / 2, screenHeight / 2 - 44, 0xffffff);
                this.minecraft.textRenderer.drawWithShadow(this.stage, (screenWidth - this.minecraft.textRenderer.getWidth(this.stage)) / 2, screenHeight / 2 - 4, 0xffffff);

                int bx = 0;
                int by = 0;
                if (Mouse.isInsideWindow()) {
                    bx = Mouse.getX() * screenWidth / minecraft.displayWidth;
                    by = screenHeight - Mouse.getY() * screenHeight / minecraft.displayHeight - 1;
                }

                this.wakeButton.render(minecraft, bx, by);
                Display.update();

                try {
                    Thread.yield();
                } catch (Exception ignored) {
                }
            }
        }
    }

    public int getTicksElapsed() {
        return ticksElapsed;
    }

    public void tick() {
        int progress = ticksElapsed * 100 / ticksTotal;
        progressStagePercentage(progress);

        ticksElapsed++;
    }
}
