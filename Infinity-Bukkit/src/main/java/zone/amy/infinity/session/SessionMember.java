package zone.amy.infinity.session;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import zone.amy.infinity.Infinity;

@RequiredArgsConstructor
public abstract class SessionMember {
    private final SessionAgent agent;
    @Getter private Player player;

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
