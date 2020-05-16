package zone.amy.infinity;

import zone.amy.infinity.lib.database.DatabaseManager;
import zone.amy.infinity.lib.network.NetworkManager;

public interface PeripheralProvider {
    DatabaseManager getDatabaseManager();
    NetworkManager getNetworkManager();
}
