package zone.amy.infinity.lib.logger;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
import zone.amy.infinity.lib.network.NetworkMessage;

@RequiredArgsConstructor
public class LogMessage extends NetworkMessage.Content {
    @Getter private Type type;
    @Getter private DateTime time;
    @Getter private String message;

    protected enum Type {
        INFO,
        ERROR,
        WARN
    }
}
