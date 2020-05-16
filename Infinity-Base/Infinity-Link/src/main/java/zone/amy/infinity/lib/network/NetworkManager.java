package zone.amy.infinity.lib.network;

import org.joda.time.DateTime;

import java.util.*;

//todo: separate networknode and identity here so that one networkmanager functions for the entire node, but can have multiple identities within

public abstract class NetworkManager {
    private Map<String, Set<NetworkSubscriber>> subscriptions = new HashMap<>();

    public abstract void send(String channel, NetworkMessage message);
    protected abstract void onBeginMonitoringChannel(String channel);
    protected abstract void onEndMonitoringChannel(String channel);

    protected final void stampCurrentTime(NetworkMessage message) {
        message.lastSentAt = DateTime.now();
    }

    public final void send(String channel, NetworkIdentity identity, NetworkMessage.Content data) {
        send(channel, new NetworkMessage(identity, data));
    }

    protected final void receive(String channel, NetworkMessage message) {
        for (NetworkSubscriber subscriber : subscriptions.get(channel)) subscriber.onReceiveMessage(channel, message);
    }

    public final Set<String> getMonitoredChannels() {
        return subscriptions.keySet();
    }

    public final Subscription subscribe(String channel, NetworkSubscriber subscriber) {
        if (!subscriptions.containsKey(channel)) {
            subscriptions.put(channel, new HashSet<>());
            onBeginMonitoringChannel(channel);
        }
        subscriptions.get(channel).add(subscriber);
        return () -> {
            subscriptions.get(channel).remove(subscriber);
            if (subscriptions.get(channel).size() == 0) {
                subscriptions.remove(channel);
                onEndMonitoringChannel(channel);
            }
        };
    }

    public interface Subscription {
        void unsubscribe();
    }
}
