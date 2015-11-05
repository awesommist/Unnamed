package unnamed.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.*;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import unnamed.Log;
import unnamed.Unnamed;
import unnamed.datastore.*;

import com.google.common.base.Preconditions;
import com.google.common.io.Closer;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderState;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.common.network.NetworkHandshakeEstablished;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;

public class IdSyncManager extends DataStoreManager {

    private static final String CHANNEL_NAME = "Unnamed|I";
    public static final IdSyncManager INSTANCE = new IdSyncManager();

    @Sharable
    private class InboundHandler extends SimpleChannelInboundHandler<FMLProxyPacket> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, FMLProxyPacket msg) throws Exception {
            ByteBuf buf = msg.payload();

            try {
                decodeIds(buf);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object event) throws Exception {
            if (event instanceof NetworkHandshakeEstablished) {
                Log.debug("Sending id data for player: %s", Unnamed.proxy.getPlayerFromHandler(((NetworkHandshakeEstablished) event).netHandler));
                sendAllIds(ctx);
            } else {
                ctx.fireUserEventTriggered(event);
            }
        }
    }

    private static FMLProxyPacket serializeToPacket(DataStoreKey<?, ?> key, DataStoreWriter<?, ?> writer) {
        ByteBuf payload = Unpooled.buffer();

        Closer closer = Closer.create();

        try {
            try {
                OutputStream raw = closer.register(new ByteBufOutputStream(payload));
                OutputStream compressed = closer.register(new GZIPOutputStream(raw));
                DataOutput output = new DataOutputStream(compressed);
                output.writeUTF(key.id);
                writer.write(output);
            } finally {
                closer.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new FMLProxyPacket(payload.copy(), CHANNEL_NAME);
    }

    private IdSyncManager() {
        NetworkRegistry.INSTANCE.newChannel(CHANNEL_NAME, new InboundHandler());
    }

    public <K, V> DataStoreBuilder<K, V> createDataStore(String domain, String id, Class<? extends K> keyClass, Class<? extends V> valueClass) {
        final String fullId = domain + ":" + id;
        return createDataStore(fullId, keyClass, valueClass);
    }

    @Override
    public <K, V> DataStoreBuilder<K, V> createDataStore(String id, Class<? extends K> keyClass, Class<? extends V> valueClass) {
        Preconditions.checkState(!Loader.instance().hasReachedState(LoaderState.POSTINITIALIZATION), "This method cannot be called in post-initialization state and later");
        return super.createDataStore(id, keyClass, valueClass);
    }

    private void sendAllIds(ChannelHandlerContext ctx) {
        validate();

        for (Map.Entry<DataStoreKey<?, ?>, DataStoreWrapper<?, ?>> e : dataStoreMeta.entrySet()) {
            FMLProxyPacket packet = serializeToPacket(e.getKey(), e.getValue().createWriter());
            ctx.write(packet);
        }
    }

    private void decodeIds(ByteBuf buf) throws IOException {
        Closer closer = Closer.create();
        try {
            InputStream raw = closer.register(new ByteBufInputStream(buf));
            InputStream compressed = closer.register(new GZIPInputStream(raw));
            DataInput input = new DataInputStream(compressed);

            String keyId = input.readUTF();

            Log.debug("Received data store for key %s, packet size = %d", keyId, buf.writerIndex());
            DataStoreWrapper<?, ?> wrapper = getDataStoreMeta(keyId);
            DataStoreReader<?, ?> reader = wrapper.createReader();
            reader.read(input);
        } finally {
            closer.close();
        }
    }

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent evt) {
        Log.debug("Disconnected, restoring local data");
        activateLocalData();
    }

    public void finishLoading() {
        validate();
        FMLCommonHandler.instance().bus().register(this);
    }
}