package unnamed.integration;

import unnamed.conditions.ICondition;
import unnamed.reflection.SafeClassLoad;

import cpw.mods.fml.common.Loader;

public class Conditions extends unnamed.conditions.Conditions {

    public static ICondition classExists(String clsName) {
        final SafeClassLoad cls = new SafeClassLoad(clsName);
        return new ICondition() {
            @Override
            public boolean check() {
                return cls.tryLoad();
            }
        };
    }

    public static ICondition modLoaded(final String modName) {
        return new ICondition() {
            @Override
            public boolean check() {
                return Loader.isModLoaded(modName);
            }
        };
    }
}