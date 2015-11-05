package unnamed.config;

import java.util.Collection;

import net.minecraftforge.common.config.Configuration;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ConfigStorage {

    public static final ConfigStorage instance = new ConfigStorage();

    private Multimap<String, Configuration> configs = ArrayListMultimap.create();

    public void register(Configuration value) {
        ModContainer mod = Loader.instance().activeModContainer();
        Preconditions.checkNotNull(mod, "Can't register outside initialization");
        final String modId = mod.getModId();

        configs.put(modId, value);
    }

    public Collection<Configuration> getConfigs(String modId) {
        return configs.get(modId);
    }

    public void saveAll(String modId) {
        for (Configuration config : configs.get(modId))
            if (config.hasChanged()) config.save();
    }

    @SubscribeEvent
    public void onConfigChange(ConfigChangedEvent.PostConfigChangedEvent event) {
        saveAll(event.modID);
    }
}