package unnamed.network.event;

import unnamed.datastore.DataStoreBuilder;
import unnamed.datastore.IDataVisitor;
import unnamed.network.IdSyncManager;
import unnamed.utils.io.TypeRW;

import com.google.common.base.Preconditions;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderState;

public class NetworkEventManager {

    public static class RegistrationContext {
        private int currentId = 0;

        private final DataStoreBuilder<String, Integer> builder;

        private RegistrationContext() {
            builder = IdSyncManager.INSTANCE.createDataStore("events", String.class, Integer.class);

            builder.setDefaultKeyReaderWriter();
            builder.setValueReaderWriter(TypeRW.VLI_SERIALIZABLE);
        }

        public RegistrationContext register(Class<? extends NetworkEvent> cls) {
            Preconditions.checkState(Loader.instance().isInState(LoaderState.PREINITIALIZATION), "This method can only be called in pre-initialization state");

            builder.addEntry(cls.getName(), currentId++);
            return this;
        }

        void register(IDataVisitor<String, Integer> eventIdVisitor) {
            builder.addVisitor(eventIdVisitor);
            builder.register();
        }
    }

    private NetworkEventManager() {}

    public static final NetworkEventManager INSTANCE = new NetworkEventManager();

    private final NetworkEventRegistry registry = new NetworkEventRegistry();

    private final NetworkEventDispatcher dispatcher = new NetworkEventDispatcher(registry);

    private RegistrationContext registrationContext = new RegistrationContext();

    public RegistrationContext startRegistration() {
        Preconditions.checkState(Loader.instance().isInState(LoaderState.PREINITIALIZATION), "This method can only be called in pre-initialization state");
        return registrationContext;
    }

    public void finalizeRegistration() {
        registrationContext.register(registry);
        registrationContext = null;
    }

    public NetworkEventDispatcher dispatcher() {
        return dispatcher;
    }
}