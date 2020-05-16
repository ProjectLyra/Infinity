package zone.amy.infinity.session;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import zone.amy.infinity.user.IOfflineUser;
import zone.amy.infinity.user.LocalOfflineUser;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerRouter implements Listener {
    private Map<UUID, SessionAgent> buffer = new HashMap<>();
    private Map<UUID, SessionMemberConfiguration> configBuffer = new HashMap<>();

    public void routeWhenAvailable(IOfflineUser user, SessionMemberConfiguration configuration, SessionAgent session) {
        if (Bukkit.getPlayer(user.getUuid()) != null) {
            add(Bukkit.getPlayer(user.getUuid()), session, configuration);
        } else {
            buffer.put(user.getUuid(), session);
            configBuffer.put(user.getUuid(), configuration);
        }
    }

    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent event) {
        if (buffer.containsKey(event.getPlayer().getUniqueId())) {
            add(event.getPlayer(), buffer.remove(event.getPlayer().getUniqueId()), configBuffer.remove(event.getPlayer().getUniqueId()));
        }
    }

    private void add(Player player, SessionAgent session, SessionMemberConfiguration configuration) {
        session.addMember(new LocalOfflineUser(player.getUniqueId(), player), configuration);
    }
}
