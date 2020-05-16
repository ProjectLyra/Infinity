package zone.amy.infinity;

import zone.amy.infinity.lib.network.NetworkManager;
import zone.amy.infinity.lib.network.redis.RedisNetworkManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class InfinityHead {
    public static void main(String[] args) throws IOException {
        NetworkManager manager = new RedisNetworkManager("127.0.0.1", 6379);

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String in;
        while (true) {
            in = reader.readLine();
        }
    }
}
