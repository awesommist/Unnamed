package unnamed.network.event;

import java.util.Map;

import unnamed.network.Dispatcher;
import unnamed.network.ExtendedOutboundHandler;

import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;

public class NetworkEventDispatcher extends Dispatcher {

    public static final String CHANNEL_NAME = "Unnamed|E";

    private final Map<Side, FMLEmbeddedChannel> channels;

    public final Senders senders;

    public NetworkEventDispatcher(NetworkEventRegistry registry) {
        channels = NetworkRegistry.INSTANCE.newChannel(CHANNEL_NAME, new NetworkEventCodec(registry), new NetworkEventInboundHandler());
        ExtendedOutboundHandler.install(this.channels);

        senders = new Dispatcher.Senders();
    }

    @Override
    protected FMLEmbeddedChannel getChannel(Side side) {
        return channels.get(side);
    }
}