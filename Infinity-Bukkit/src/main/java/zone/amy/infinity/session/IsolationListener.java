package zone.amy.infinity.session;

import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import zone.amy.infinity.user.IOfflineUser;

import java.util.stream.Collectors;

/**
 * Handles everything to do with isolating communication between users.
 */
@RequiredArgsConstructor
public class IsolationListener implements Listener {
    private final SessionManager sessionManager;

    // TODO: Player hide/showing shite, w/ priority to allow for sessions to hide internal players from others -- isVisibleTo in the session code?

    public SessionAgent getSession(IOfflineUser user) {
        return sessionManager.getSessionInterface(
                sessionManager.getCurrentSession(user)
        );
    }

    @EventHandler
    public void playerChatEvent(AsyncPlayerChatEvent event) {
        IOfflineUser user = new IOfflineUser((event.getPlayer().getUniqueId()));
        SessionAgent session = getSession(user);

        event.getRecipients().removeAll(
                event.getRecipients().stream()
                        .filter(player -> !session.isMember(user))
                        .collect(Collectors.toSet())
        );
    }

    @EventHandler
    public void playerDeathEvent(PlayerDeathEvent event) {
        event.setDeathMessage(null);
    }
    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent event) {
        event.setJoinMessage(null);
    }
    @EventHandler
    public void playerQuitEvent(PlayerQuitEvent event) {
        event.setQuitMessage(null);
    }
    @EventHandler
    public void playerKickEvent(PlayerKickEvent event) {
        event.setLeaveMessage("");
    }
}
