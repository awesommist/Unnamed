package unnamed;

import java.io.File;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import unnamed.block.BlockDropsStore;
import unnamed.config.ConfigStorage;
import unnamed.config.properties.CommandConfig;
import unnamed.config.properties.ConfigProcessing;
import unnamed.handler.DebugScreenHandler;
import unnamed.network.IdSyncManager;
import unnamed.network.event.NetworkEventManager;
import unnamed.network.rpc.RpcCallDispatcher;
import unnamed.network.rpc.targets.EntityRpcTarget;
import unnamed.network.rpc.targets.SyncRpcTarget;
import unnamed.network.rpc.targets.TileEntityRpcTarget;
import unnamed.proxy.IUnnamedProxy;
import unnamed.source.ClassSourceCollector;
import unnamed.source.CommandSource;
import unnamed.sync.SyncChannelHolder;
import unnamed.utils.bitmap.IRpcDirectionBitMap;
import unnamed.utils.bitmap.IRpcIntBitMap;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

@Mod(modid = Unnamed.MODID, name = Unnamed.MODID, version = "$LIB-VERSION$", guiFactory = "unnamed.GuiFactory")
public class Unnamed {

    public static final String MODID = "Unnamed";

    @Instance(MODID)
    public static Unnamed instance;

    @SidedProxy(clientSide = "unnamed.proxy.UnnamedClientProxy", serverSide = "unnamed.proxy.UnnamedServerProxy")
    public static IUnnamedProxy proxy;

    private ClassSourceCollector collector;

    public ClassSourceCollector getCollector() {
        return collector;
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        SyncChannelHolder.ensureLoaded();

        NetworkEventManager.INSTANCE.startRegistration();

        RpcCallDispatcher.INSTANCE.startRegistration()
                .registerInterface(IRpcDirectionBitMap.class)
                .registerInterface(IRpcIntBitMap.class)
                .registerTargetWrapper(EntityRpcTarget.class)
                .registerTargetWrapper(TileEntityRpcTarget.class)
                .registerTargetWrapper(SyncRpcTarget.SyncEntityRpcTarget.class)
                .registerTargetWrapper(SyncRpcTarget.SyncTileEntityRpcTarget.class);

        final File configFile = event.getSuggestedConfigurationFile();
        Configuration config = new Configuration(configFile);
        ConfigProcessing.processAnnotations(MODID, config, LibConfig.class);
        if (config.hasChanged()) config.save();

        MinecraftForge.EVENT_BUS.register(DebugScreenHandler.instance);

        MinecraftForge.EVENT_BUS.register(BlockDropsStore.instance.createForgeListener());

        FMLCommonHandler.instance().bus().register(BlockDropsStore.instance.createFmlListener());

        FMLCommonHandler.instance().bus().register(ConfigStorage.instance);

        collector = new ClassSourceCollector(event.getAsmData());

        proxy.preInit();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        // Integration stuff goes here
        proxy.postInit();

        NetworkEventManager.INSTANCE.finalizeRegistration();
        RpcCallDispatcher.INSTANCE.finishRegistration();

        // must be after all builders are done
        IdSyncManager.INSTANCE.finishLoading();
    }

    @EventHandler
    public void serverStart(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandConfig("un_config_s", true));
        event.registerServerCommand(new CommandSource("un_source_s", true, collector));
    }
}