package zone.amy.infinity.lib.network.redis;

import zone.amy.infinity.lib.network.NetworkManager;
import zone.amy.infinity.lib.network.NetworkMessage;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

public class RedisNetworkManager extends NetworkManager {
    private JedisPool pool;

    private Thread listener;

    public RedisNetworkManager(String host, int port) {
        pool = new JedisPool(host, port);
    }

    private Jedis getResource() {
        return pool.getResource();
    }

    @Override
    public void send(String channel, NetworkMessage message) {
        stampCurrentTime(message);
        Jedis resource = getResource();
        resource.publish(channel.getBytes(StandardCharsets.UTF_8), message.toByteArray());
        resource.close();
    }

    @Override
    protected void onBeginMonitoringChannel(String channel) {
        if (listener != null) listener.interrupt();
        listener = Listener.start(this);
    }

    @Override
    protected void onEndMonitoringChannel(String channel) {
        if (listener != null) listener.interrupt();
        if (getMonitoredChannels().size() > 0) {
            listener = Listener.start(this);
        }
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Listener extends BinaryJedisPubSub {
        RedisNetworkManager manager;

        @Override
        public void onMessage(byte[] channel, byte[] data) {
            manager.receive(
                    new String(channel, StandardCharsets.UTF_8),
                    NetworkMessage.fromByteArray(data)
            );
        }

        /* Thread control */
        private static Thread start(RedisNetworkManager manager) {
            Thread thread = new Thread(()->{
                byte[][] channels = new byte[manager.getMonitoredChannels().size()][];
                AtomicInteger i = new AtomicInteger(0);
                manager.getMonitoredChannels().forEach(
                        channel -> channels[i.getAndAdd(1)] = channel.getBytes(StandardCharsets.UTF_8)
                );
                try (Jedis resource = manager.getResource()) {
                    resource.subscribe(new RedisNetworkManager.Listener(manager), channels);
                }
            });
            thread.start();
            return thread;
        }
    }

}
