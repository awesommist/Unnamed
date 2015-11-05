package unnamed;

import unnamed.config.properties.ConfigProperty;
import unnamed.config.properties.OnLineModifiable;

public class LibConfig {

    @OnLineModifiable
    @ConfigProperty(category = "debug", name = "dropsDebug", comment = "Control printing of stacktraces in case of unharvested drops")
    public static boolean dropsDebug;
}