package zone.amy.infinity.session;

import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import zone.amy.infinity.Infinity;
import zone.amy.infinity.module.InfinityModule;
import zone.amy.infinity.user.IOfflineUser;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SessionManager {
    private Map<Class<? extends SessionConfiguration>, Class<? extends InfinitySession>> sessionClasses = new HashMap<>();
    private Map<SessionIdentity, SessionAgent> sessions = new HashMap<>();

    public SessionManager() {
        Infinity.getInstance().getServer().getPluginManager().registerEvents(new PlayerRouter(this), Infinity.getInstance());
    }

    public void registerSessionClass(
            Class<? extends SessionConfiguration> config,
            Class<? extends InfinitySession> type) {
        sessionClasses.put(config, type);
    }

    public SessionIdentity createSession(InfinityModule owner, SessionConfiguration configuration) {
        Class<? extends SessionConfiguration> configClass = configuration.getClass();
        try {
            if (sessionClasses.containsKey(configClass)) {
                SessionAgent session = new SessionAgent(
                        this,
                        owner,
                        sessionClasses.get(configClass).getConstructor(configClass).newInstance(configuration)
                );

                sessions.put(session.getExternalRepresentation(), session);

                return session.getExternalRepresentation();
            } else {
                throw new IllegalStateException("No session code prepared for that configuration type!");
            }
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public SessionAgent getSessionInterface(SessionIdentity session) {
        return sessions.get(session);
    }

    public SessionIdentity getExpectingSession(IOfflineUser player) {
        for (SessionIdentity session : sessions.keySet()) {
            if (getSessionInterface(session).isBuffered(player)) return session;
        }
        return null;
    }
    public SessionIdentity getCurrentSession(IOfflineUser player) {
        for (SessionIdentity session : sessions.keySet()) {
            if (getSessionInterface(session).isMember(player)) return session;
        }
        return null;
    }

    @RequiredArgsConstructor
    private static class PlayerRouter implements Listener {
        private final SessionManager sessionManager;

        @EventHandler
        public void playerLoginEvent(PlayerLoginEvent event) {
            IOfflineUser user = new IOfflineUser(event.getPlayer().getUniqueId());
            SessionIdentity session = sessionManager.getExpectingSession(user);
            if (session != null) {
                SessionAgent agent = sessionManager.getSessionInterface(session);
                if (agent.isBuffered(user)) {
                    if (agent.isUserAllowedEntry(user, agent.getBufferedConfiguration(user))) {
                        event.allow();
                    } else {
                        event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Session denied user entry.");
                    }
                }
            } else {
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "No sessions marked for user!");
            }
        }

        @EventHandler
        public void playerJoinEvent(PlayerJoinEvent event) {
            IOfflineUser user = new IOfflineUser(event.getPlayer().getUniqueId());
            SessionIdentity session = sessionManager.getExpectingSession(user);
            if (session != null) {
                SessionAgent agent = sessionManager.getSessionInterface(session);
                agent.addMember(event.getPlayer(), agent.stopExpecting(user));
            }
        }
    }
}
