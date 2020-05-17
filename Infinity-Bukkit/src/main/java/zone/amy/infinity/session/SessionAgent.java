package zone.amy.infinity.session;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import zone.amy.infinity.Infinity;
import zone.amy.infinity.exception.SessionUserBusyException;
import zone.amy.infinity.lib.RepresentableObject;
import zone.amy.infinity.module.InfinityModule;
import zone.amy.infinity.user.IOfflineUser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Acts as intervening layer between the Session and the surrounding plugin, handling things like member joining/leaving.
 */
@RequiredArgsConstructor
public class SessionAgent implements Listener, RepresentableObject<SessionIdentity> {
    private final SessionManager manager;
    private final InfinityModule module;
    @Getter private final InfinitySession session;

    private Map<IOfflineUser, SessionMemberConfiguration> incomingUserBuffer = new HashMap<>();
    private Set<SessionMember> members = new HashSet<>();

    // Delegation Methods

    public SessionState getState() {
        return this.session.getState();
    }

    @Override
    public SessionIdentity getExternalRepresentation() {
        return this.session.getIdentity();
    }

    // Action Methods

    public void load() {
        Infinity.getInstance().getServer().getPluginManager().registerEvents(this, Infinity.getInstance());
        this.session.onLoad();
    }

    public void unload() {
        this.session.onUnload();
    }

    /**
     * Directs a free user to this session.If the user isn't logged in, they will be buffered to join this session when
     * they do, superseding all previous buffers managed by this Infinity instance.
     * @param user The user to direct
     * @param configuration The configuration to pass to the session
     * @return True if the user was immediately routed, false if they were buffered.
     * @throws SessionUserBusyException When the user is currently in a different session within the manager.
     */
    public boolean directOrExpectUser(IOfflineUser user, SessionMemberConfiguration configuration) throws SessionUserBusyException {
        Player player = module.getServer().getPlayer(user.getUuid());
        if (player != null) {
            if (manager.getCurrentSession(user) == null) {
                addMember(player, configuration);
                return true;
            } else  {
                throw new SessionUserBusyException();
            }
        } else {
            SessionIdentity expectingSession = manager.getExpectingSession(user);
            if (expectingSession != null) manager.getSessionInterface(expectingSession).stopExpecting(user);
            incomingUserBuffer.put(user, configuration);
            return false;
        }
    }

    public boolean isBuffered(IOfflineUser user) {
        return incomingUserBuffer.containsKey(user);
    }
    public SessionMemberConfiguration stopExpecting(IOfflineUser user) {
        return incomingUserBuffer.remove(user);
    }

    public boolean isMember(IOfflineUser user) {
        for (SessionMember member : this.members) {
            if (member.getPlayer().getUniqueId() == user.getUuid()) return true;
        }
        return false;
    }


    // Local player routing methods

    SessionMemberConfiguration getBufferedConfiguration(IOfflineUser user) {
        return incomingUserBuffer.get(user);
    }

    @SuppressWarnings("unchecked")
    boolean isUserAllowedEntry(IOfflineUser user, SessionMemberConfiguration configuration) {
        return this.session.isUserAllowedEntry(user, configuration);
    }


    @SuppressWarnings("unchecked")
    void addMember(Player player, SessionMemberConfiguration configuration) {
        SessionMember member = this.session.constructMember(player);
        members.add(member);
        this.session.onMemberJoin(member, configuration);
    }

    @SuppressWarnings("unchecked")
    void removeMember(Player player) {
        SessionMember member = members.stream().filter(mem -> mem.getPlayer().equals(player)).findFirst().get();
        this.session.onMemberLeave(member);
        members.remove(member);
    }


    @EventHandler
    public void playerQuitEvent(PlayerQuitEvent event) {
        removeMember(event.getPlayer());
    }
    @EventHandler
    public void playerKickEvent(PlayerKickEvent event) {
        removeMember(event.getPlayer());
    }
}
