package zone.amy.infinity.sessions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import zone.amy.infinity.Infinity;
import zone.amy.infinity.PlayerRouter;
import zone.amy.infinity.session.SessionIdentity;
import zone.amy.infinity.session.SessionMemberConfiguration;
import zone.amy.infinity.session.SessionState;
import zone.amy.infinity.user.InfinityUser;
import zone.amy.infinity.user.LocalUser;

import java.util.HashSet;
import java.util.Set;

/**
 * Acts as intervening layer between the Session and the surrounding plugin, handling things like member joining/leaving.
 */
@RequiredArgsConstructor
public class SessionHandler implements Session, Listener {
    private final PlayerRouter router;
    @Getter private final InfinitySession session;

    private Set<SessionMember> members = new HashSet<>();

    // Delegation Methods

    @Override
    @SuppressWarnings("unchecked")
    public boolean canUserJoin(InfinityUser user, SessionMemberConfiguration configuration) {
        return this.session.canUserJoin(user, configuration);
    }

    @Override
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

    @Override
    public void directUser(InfinityUser user, SessionMemberConfiguration configuration) {
        router.routeWhenAvailable(user, configuration, this);
    }


    @SuppressWarnings("unchecked")
    public void addMember(LocalUser user, SessionMemberConfiguration configuration) {
        SessionMember member = this.session.constructMember(user);
        members.add(member);
        this.session.onMemberJoin(member, configuration);
    }

    @SuppressWarnings("unchecked")
    public void removeMember(LocalUser user) {
        SessionMember member = members.stream().filter(mem -> mem.getUser().getUuid().equals(user.getUuid())).findFirst().get();
        this.session.onMemberLeave(member);
        members.remove(member);
    }


    @EventHandler
    public void playerQuitEvent(PlayerQuitEvent event) {
        removeMember(new LocalUser(event.getPlayer().getUniqueId(), event.getPlayer()));
    }
    @EventHandler
    public void playerKickEvent(PlayerKickEvent event) {
        removeMember(new LocalUser(event.getPlayer().getUniqueId(), event.getPlayer()));
    }
}
