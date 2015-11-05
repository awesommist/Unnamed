package unnamed.network.event;

public interface INetworkEventType {
    NetworkEvent createPacket();

    EventDirection getDirection();

    boolean isCompressed();

    boolean isChunked();
}