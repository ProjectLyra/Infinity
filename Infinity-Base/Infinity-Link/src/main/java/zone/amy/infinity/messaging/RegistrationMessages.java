package zone.amy.infinity.messaging;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import zone.amy.infinity.lib.network.NetworkMessage;

public class RegistrationMessages {
    @Getter
    @RequiredArgsConstructor
    public static class Introduce extends NetworkMessage.Content {
        private final String privateChannel;
    }
    @Getter
    @RequiredArgsConstructor
    public static class RequestIntroduction extends NetworkMessage.Content {
        private final String privateChannel;
    }
    @Getter
    @RequiredArgsConstructor
    public static class Heartbeat extends NetworkMessage.Content {
    }
}
