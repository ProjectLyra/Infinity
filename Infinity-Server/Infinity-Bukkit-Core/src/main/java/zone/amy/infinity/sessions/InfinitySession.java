package zone.amy.infinity.sessions;

import lombok.RequiredArgsConstructor;
import zone.amy.infinity.session.SessionConfiguration;
import zone.amy.infinity.session.SessionIdentity;
import zone.amy.infinity.session.SessionMemberConfiguration;
import zone.amy.infinity.session.SessionState;
import zone.amy.infinity.user.InfinityUser;
import zone.amy.infinity.user.LocalUser;

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

    protected abstract MemberType constructMember(LocalUser user);

    // Events
    protected abstract void onLoad();
    protected abstract void onUnload();

    public abstract boolean canUserJoin(InfinityUser player, MemberConfigurationType configuration);
    public abstract void onMemberJoin(MemberType member, SessionMemberConfiguration configuration);
    public abstract void onMemberLeave(MemberType user);
}
