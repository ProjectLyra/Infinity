package zone.amy.infinity.lib.network;

public interface NetworkSubscriber {
    void onReceiveMessage(String channel, NetworkMessage message);
}
