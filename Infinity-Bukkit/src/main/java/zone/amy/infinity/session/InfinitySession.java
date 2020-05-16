package zone.amy.infinity.session;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import zone.amy.infinity.user.IOfflineUser;

@RequiredArgsConstructor
public abstract class InfinitySession<
        ConfigurationType extends SessionConfiguration,
        MemberConfigurationType extends SessionMemberConfiguration,
        MemberType extends SessionMember,
        SessionStateType extends SessionState
        > {

    private final ConfigurationType configuration;

    public abstract SessionIdentity getIdentity();
    public abstract SessionStateType getState();

    protected abstract MemberType constructMember(Player user);

    // Events
    protected abstract void onLoad();
    protected abstract void onUnload();

    public abstract boolean canUserJoin(IOfflineUser player, MemberConfigurationType configuration);
    public abstract void onMemberJoin(MemberType member, SessionMemberConfiguration configuration);
    public abstract void onMemberLeave(MemberType user);
}
