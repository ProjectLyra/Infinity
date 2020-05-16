package zone.amy.infinity.messaging;

import zone.amy.infinity.lib.network.NetworkMessage;

public class GlobalChannels {
    public static final String ALL_BROADCAST = "SYSTEM_BROADCAST";
    public static final String REGISTRATION = "REGISTRATION";
    public static final String HEARTBEAT_CHANNEL = "HEARTBEATS";

    public static class HEARTBEAT {
        public static class BROADCAST extends NetworkMessage.Content {

        }
        public static class RETURN extends NetworkMessage.Content {

        }
    }
}
