package unnamed;

import java.io.File;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import unnamed.config.properties.ConfigProcessing;
import unnamed.handler.DebugScreenHandler;
import unnamed.proxy.IUnnamedProxy;
import unnamed.sync.SyncChannelHolder;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Unnamed.MODID, name = Unnamed.MODID, version = "$LIB-VERSION$", guiFactory = "unnamed.GuiFactory")
public class Unnamed {

    public static final String MODID = "Unnamed";

    @Mod.Instance(MODID)
    public static Unnamed instance;

    @SidedProxy(clientSide = "unnamed.proxy.UnnamedClientProxy", serverSide = "unnamed.proxy.UnnamedServerProxy")
    public static IUnnamedProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        SyncChannelHolder.ensureLoaded();

        final File configFile = event.getSuggestedConfigurationFile();
        Configuration config = new Configuration(configFile);
        ConfigProcessing.processAnnotations(MODID, config, LibConfig.class);
        if (config.hasChanged()) config.save();

        MinecraftForge.EVENT_BUS.register(DebugScreenHandler.instance);

        proxy.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit();
    }
}