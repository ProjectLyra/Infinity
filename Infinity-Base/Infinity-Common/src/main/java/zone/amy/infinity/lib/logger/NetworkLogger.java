package zone.amy.infinity.lib.logger;

import lombok.RequiredArgsConstructor;
import zone.amy.infinity.lib.network.NetworkIdentity;
import zone.amy.infinity.lib.network.NetworkManager;

@RequiredArgsConstructor
public class NetworkLogger implements Logger {
    private NetworkIdentity identity;
    private NetworkManager manager;

    private void log(LogMessage.Type type, String message) {
        manager.
    }

    @Override
    public void info(String message) {
        log(LogMessage.Type.INFO, message);
    }

    @Override
    public void error(String message) {

    }

    @Override
    public void warn(String message) {

    }
}
