package zone.amy.infinity.lib.network.lonely;

import zone.amy.infinity.lib.network.NetworkManager;
import zone.amy.infinity.lib.network.NetworkMessage;

public class LonelyNetworkManager extends NetworkManager {
    @Override
    public void send(String channel, NetworkMessage message) {
        receive(channel, message);
    }

    @Override
    protected void onBeginMonitoringChannel(String channel) {

    }

    @Override
    protected void onEndMonitoringChannel(String channel) {

    }
}
