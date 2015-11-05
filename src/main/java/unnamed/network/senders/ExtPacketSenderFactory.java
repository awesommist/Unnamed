package unnamed.network.senders;

import io.netty.channel.Channel;

import net.minecraft.entity.Entity;
import unnamed.network.ExtendedOutboundHandler;
import unnamed.network.IPacketTargetSelector;
import unnamed.network.targets.SelectChunkWatchers;
import unnamed.network.targets.SelectEntityWatchers;
import unnamed.utils.vec.DimCoord;

public class ExtPacketSenderFactory {

    public static ITargetedPacketSender<DimCoord> createBlockSender(Channel channel) {
        return new ExtTargetedPacketSender<DimCoord>(channel, SelectChunkWatchers.INSTANCE) {
            @Override
            protected void configureChannel(Channel channel, DimCoord target) {
                super.configureChannel(channel, target);
                setTargetAttr(channel, target);
            }
        };
    }

    public static ITargetedPacketSender<Entity> createEntitySender(Channel channel) {
        return new ExtTargetedPacketSender<Entity>(channel, SelectEntityWatchers.INSTANCE) {
            @Override
            protected void configureChannel(Channel channel, Entity target) {
                super.configureChannel(channel, target);
                setTargetAttr(channel, target);
            }
        };
    }

    private static class ExtTargetedPacketSender<T> extends TargetedPacketSenderBase<T> {
        public final IPacketTargetSelector selector;

        public ExtTargetedPacketSender(Channel channel, IPacketTargetSelector selector) {
            super (channel);
            this.selector = selector;
        }

        @Override
        protected void configureChannel(Channel channel, T target) {
            channel.attr(ExtendedOutboundHandler.MESSAGE_TARGET).set(selector);
        }
    }
}