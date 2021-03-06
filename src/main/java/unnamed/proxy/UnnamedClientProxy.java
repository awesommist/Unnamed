package unnamed.proxy;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.world.World;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import unnamed.Unnamed;
import unnamed.block.BlockSelectionHandler;
import unnamed.config.properties.CommandConfig;
import unnamed.gui.ClientGuiHandler;
import unnamed.source.CommandSource;
import unnamed.utils.render.RenderUtils;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.IGuiHandler;

public final class UnnamedClientProxy implements IUnnamedProxy {

    @Override
    public World getClientWorld() {
        return Minecraft.getMinecraft().theWorld;
    }

    @Override
    public World getServerWorld(int dimension) {
        return DimensionManager.getWorld(dimension);
    }

    @Override
    public EntityPlayer getThePlayer() {
        return FMLClientHandler.instance().getClient().thePlayer;
    }

    @Override
    public boolean isClientPlayer(Entity player) {
        return player instanceof EntityPlayerSP;
    }

    @Override
    public long getTicks(World worldObj) {
        if (worldObj != null) return worldObj.getTotalWorldTime();
        World cWorld = getClientWorld();
        if (cWorld != null) return cWorld.getTotalWorldTime();
        return 0;
    }

    @Override
    public File getMinecraftDir() {
        return Minecraft.getMinecraft().mcDataDir;
    }

    @Override
    public String getLogFileName() {
        return "ForgeModLoader-client-0.log";
    }

    @Override
    public IGuiHandler wrapHandler(IGuiHandler modSpecificHandler) {
        return new ClientGuiHandler(modSpecificHandler);
    }

    @Override
    public void preInit() {
        ClientCommandHandler.instance.registerCommand(new CommandConfig("un_config_c", false));
        ClientCommandHandler.instance.registerCommand(new CommandSource("un_source_c", false, Unnamed.instance.getCollector()));

        RenderUtils.registerFogUpdater();

        MinecraftForge.EVENT_BUS.register(new BlockSelectionHandler());
    }

    @Override
    public void init() {}

    @Override
    public void postInit() {}

    @Override
    public void setNowPlayingTitle(String nowPlaying) {
        Minecraft.getMinecraft().ingameGUI.setRecordPlayingMessage(nowPlaying);
    }

    @Override
    public EntityPlayer getPlayerFromHandler(INetHandler handler) {
        if (handler instanceof NetHandlerPlayServer) return ((NetHandlerPlayServer) handler).playerEntity;

        if (handler instanceof NetHandlerPlayClient) return getThePlayer();

        return null;
    }
}