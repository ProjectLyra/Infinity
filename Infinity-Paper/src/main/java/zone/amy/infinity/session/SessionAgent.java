package zone.amy.infinity.session;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import zone.amy.infinity.Infinity;
import zone.amy.infinity.lib.RepresentableObject;
import zone.amy.infinity.user.IOfflineUser;

import java.util.HashSet;
import java.util.Set;

/**
 * Acts as intervening layer between the Session and the surrounding plugin, handling things like member joining/leaving.
 */
@RequiredArgsConstructor
public class SessionAgent implements Listener, RepresentableObject<SessionIdentity> {
    private final PlayerRouter router;
    @Getter private final InfinitySession session;

    private Set<SessionMember> members = new HashSet<>();

    // Delegation Methods

    @SuppressWarnings("unchecked")
    public boolean canUserJoin(IOfflineUser user, SessionMemberConfiguration configuration) {
        return this.session.canUserJoin(user, configuration);
    }

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

    public void directUser(IOfflineUser user, SessionMemberConfiguration configuration) {
        router.routeWhenAvailable(user, configuration, this);
    }


    @SuppressWarnings("unchecked")
    public void addMember(Player player, SessionMemberConfiguration configuration) {
        SessionMember member = this.session.constructMember(player);
        members.add(member);
        this.session.onMemberJoin(member, configuration);
    }

    @SuppressWarnings("unchecked")
    public void removeMember(Player player) {
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
