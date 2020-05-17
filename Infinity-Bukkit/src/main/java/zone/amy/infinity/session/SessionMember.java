package zone.amy.infinity.session;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import zone.amy.infinity.Infinity;

@RequiredArgsConstructor
public abstract class SessionMember {
    @Setter(AccessLevel.PACKAGE) private SessionAgent agent;
    @Getter private final Player player;

    public abstract boolean isVisibleTo(SessionMember member);

    final void updateSight() {
        for (Player other : Infinity.getInstance().getServer().getOnlinePlayers()) {
            SessionMember otherM = agent.getMember(other);
            if (otherM != null) {
                if (otherM.isVisibleTo(this)) this.getPlayer().showPlayer(Infinity.getInstance(), other);
                else this.getPlayer().hidePlayer(Infinity.getInstance(), other);
            } else {
                this.getPlayer().hidePlayer(Infinity.getInstance(), other);
            }
        }
    }
}
