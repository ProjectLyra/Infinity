package zone.amy.infinity.peripherals;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import zone.amy.infinity.InfinityLink;
import zone.amy.infinity.PeripheralProvider;
import zone.amy.infinity.lib.database.DatabaseManager;
import zone.amy.infinity.lib.database.mongo.MongoDatabaseManager;
import zone.amy.infinity.lib.network.NetworkManager;
import zone.amy.infinity.lib.network.redis.RedisNetworkManager;

public class DistributedProvider implements PeripheralProvider {
    @Getter
    private DatabaseManager databaseManager;
    @Getter private NetworkManager networkManager;

    public DistributedProvider() {
        ConfigurationSection mongoConfig = InfinityLink.getInstance().getConfig().getConfigurationSection("mongo");
        databaseManager = new MongoDatabaseManager(mongoConfig.getString("host"),
                mongoConfig.getInt("port"),
                mongoConfig.getString("database"));
        ConfigurationSection redisConfig = InfinityLink.getInstance().getConfig().getConfigurationSection("redis");
        networkManager = new RedisNetworkManager(redisConfig.getString("host"),
                redisConfig.getInt("port"));
    }
}
