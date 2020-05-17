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

    /**
     *
     * @param player The user requesting entry
     * @param configuration The configuration passed with the entry request
     * @return Whether the user can enter the session at this moment
     */
    public abstract boolean isUserAllowedEntry(IOfflineUser player, MemberConfigurationType configuration);
    public abstract void onMemberJoin(MemberType member, MemberConfigurationType configuration);
    public abstract void onMemberLeave(MemberType user);
}
