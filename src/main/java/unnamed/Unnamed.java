package unnamed;

import net.minecraftforge.common.MinecraftForge;
import unnamed.proxy.IUnnamedProxy;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Unnamed.MODID, name = "Unnamed", version = "$LIB-VERSION$")
public class Unnamed {

    public static final String MODID = "unnamed";

    @Mod.Instance(MODID)
    public static Unnamed instance;

    @SidedProxy(clientSide = "unnamed.proxy.UnnamedClientProxy", serverSide = "unnamed.proxy.UnnamedServerProxy")
    public static IUnnamedProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new DebugScreenHandler()); // temporary
        proxy.postInit();
    }
}