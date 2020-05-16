package zone.amy.infinity;

import lombok.Getter;
import zone.amy.infinity.lib.database.DatabaseManager;
import zone.amy.infinity.lib.network.NetworkIdentity;
import zone.amy.infinity.lib.network.NetworkManager;
import zone.amy.infinity.lib.network.NetworkMessage;
import zone.amy.infinity.lib.network.NetworkSubscriber;
import zone.amy.infinity.messaging.GlobalChannels;
import zone.amy.infinity.messaging.RegistrationMessages;
import zone.amy.infinity.modules.InfinityModule;

import java.util.HashSet;
import java.util.Set;

@InfinityModule.Meta(
        name = "Infinity Link",
        description = "Networking Link for Redis / DB"
)
public class InfinityLink extends InfinityControlModule implements NetworkSubscriber {
    @Getter private static InfinityLink instance;

    @Getter private NetworkManager networkManager;
    @Getter private DatabaseManager databaseManager;

    @Getter private NetworkIdentity identity;

    // Internal memory
    private Set<NetworkManager.Subscription> subscriptions = new HashSet<>();

    @Override
    protected void onModuleEnable() {
        instance = this;

        try {
            // Set up peripherals
            PeripheralProvider provider = (PeripheralProvider) Class.forName(getConfig().getString("provider")).newInstance();

            // Set up system networking
            this.networkManager = provider.getNetworkManager();
            this.identity = new NetworkIdentity();

            // Set up database access
            this.databaseManager = provider.getDatabaseManager();

            // Announce presence
            this.networkManager.send(
                    GlobalChannels.REGISTRATION,
                    identity,
                    new RegistrationMessages.Introduce(identity.getIdentifier().toString())
            );

            getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
                getNetworkManager().send(
                        GlobalChannels.HEARTBEAT_CHANNEL,
                        identity,
                        new RegistrationMessages.Heartbeat()
                );
            }, 20 * 5, 20 * 5);

            subscriptions.add(this.networkManager.subscribe(GlobalChannels.ALL_BROADCAST, this));
            subscriptions.add(this.networkManager.subscribe(identity.getIdentifier().toString(), this));
        } catch (Exception e) {
            throw new IllegalStateException("Unable to enable Infinity Link: " + e.getMessage(), e);
        }
    }

    @Override
    protected void onModuleDisable() {
        super.onModuleDisable();
        subscriptions.forEach(NetworkManager.Subscription::unsubscribe);
    }

    @Override
    public void onReceiveMessage(String channel, NetworkMessage message) {
        switch (channel) {
            case GlobalChannels.ALL_BROADCAST:
                if (message.getContent() instanceof RegistrationMessages.RequestIntroduction) {
                    this.networkManager.send(
                            GlobalChannels.REGISTRATION,
                            identity,
                            new RegistrationMessages.Introduce(identity.getIdentifier().toString())
                    );
                }
            default:
                break;
        }
    }



    @Override
    public void setHook(CoreHook hook) {
        super.setHook(hook);
        getLogger().info("Hook \"" + hook.getClass().getSimpleName() + "\" caught");
    }
}
