package unnamed;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.Direction;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public final class DebugScreenHandler {

    @SubscribeEvent
    public void doRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
        if (event.type == RenderGameOverlayEvent.ElementType.DEBUG) {
            event.setCanceled(true);
            Minecraft mc = Minecraft.getMinecraft();
            FontRenderer fontrenderer = mc.fontRenderer;
            List<String> left = new ArrayList<String>();
            List<String> right = new ArrayList<String>();
            mc.mcProfiler.startSection("debug");
            GL11.glPushMatrix();

            left.add("Minecraft " + MinecraftForge.MC_VERSION + " (" + MinecraftForge.getBrandingVersion() + ")");
            left.add(mc.debug.substring(0, mc.debug.lastIndexOf(',')) + " (" + mc.debug.substring(mc.debug.lastIndexOf(',') + 2) + ")");
            left.add(mc.debugInfoRenders()); // can't do much about this line
            left.add(mc.getEntityDebug());
            left.add(mc.debugInfoEntities());
            left.add(mc.getWorldProviderName());
            left.add(null);

            int x = MathHelper.floor_double(mc.thePlayer.posX);
            int y = MathHelper.floor_double(mc.thePlayer.boundingBox.minY);
            int z = MathHelper.floor_double(mc.thePlayer.posZ);
            float yaw = mc.thePlayer.rotationYaw;
            float pitch = mc.thePlayer.rotationPitch;
            int heading = MathHelper.floor_double((double)(mc.thePlayer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

            left.add(String.format("XYZ: %.3f / %.5f / %.3f", mc.thePlayer.posX, mc.thePlayer.boundingBox.minY, mc.thePlayer.posZ));
            left.add(String.format("Block: %d %d %d", x, y, z));
            left.add(String.format("Chunk: %d %d %d in %d %d %d", x & 15, y & 15, z & 15, x >> 4, y >> 4, z >> 4));
            left.add(String.format("Facing: %s (Towards %s %s) (%.1f / %.1f)", Direction.directions[heading].toLowerCase(), ((heading & 1) ^ (heading >> 1 & 1)) != 0 ? "negative" : "positive", (heading & 1) != 0 ? "X" : "Z", yaw % 180, pitch));

            if (mc.theWorld != null && mc.theWorld.blockExists(x, y, z)) {
                Chunk chunk = mc.theWorld.getChunkFromBlockCoords(x, z);
                left.add(String.format("Biome: %s", chunk.getBiomeGenForWorldCoords(x & 15, z & 15, mc.theWorld.getWorldChunkManager()).biomeName));
                left.add(String.format("Light: %d (%d sky, %d block)", chunk.getBlockLightValue(x & 15, y, z & 15, 0), chunk.getSavedLightValue(EnumSkyBlock.Sky, x & 15, y, z & 15), chunk.getSavedLightValue(EnumSkyBlock.Block, x & 15, y, z & 15)));
                // add looking at block here
            }

            right.add(String.format("Java: %s %s", System.getProperty("java.version"), mc.isJava64bit() ? "64bit" : "32bit"));

            long max = Runtime.getRuntime().maxMemory();
            long total = Runtime.getRuntime().totalMemory();
            long free = Runtime.getRuntime().freeMemory();
            long used = total - free;

            right.add(String.format("Mem:  %d%% %d/%dMB", used * 100L / max, used / 1024L / 1024L, max / 1024L / 1024L));
            right.add(String.format("Allocated:  %d%% %dMB", total * 100L / max, total / 1024L / 1024L));
            right.add(null);
            right.add(String.format("CPU: %dx %s", Runtime.getRuntime().availableProcessors(), "WorkInProgress")); // CPU info
            right.add(null);
            right.add(String.format("Display: %dx%d (%s)", Display.getWidth(), Display.getHeight(), GL11.glGetString(GL11.GL_VENDOR)));
            right.add(String.format("%s", GL11.glGetString(GL11.GL_RENDERER)));
            right.add(String.format("%s", GL11.glGetString(GL11.GL_VERSION)));

            for (int i = 0; i < left.size(); ++i) {
                String msg = left.get(i);
                if (msg == null) continue;
                int w = fontrenderer.getStringWidth(msg);
                Gui.drawRect(1, 1 + i * 9, 3 + w, 10 + i * 9, 0x7F3F3F3F);
                fontrenderer.drawString(msg, 2, 2 + i * 9, 0xFFFFFF);
            }
            for (int i = 0; i < right.size(); ++i) {
                String msg = right.get(i);
                if (msg == null) continue;
                int w = fontrenderer.getStringWidth(msg);
                Gui.drawRect(event.resolution.getScaledWidth() - w - 3, 1 + i * 9, event.resolution.getScaledWidth() - 1, 10 + i * 9, 0x7F3F3F3F);
                fontrenderer.drawString(msg, event.resolution.getScaledWidth() - w - 2, 2 + i * 9, 0xFFFFFF);
            }
            GL11.glPopMatrix();
            mc.mcProfiler.endSection();
        }
    }
}