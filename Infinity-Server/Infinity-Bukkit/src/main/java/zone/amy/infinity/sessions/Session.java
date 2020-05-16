package zone.amy.infinity.sessions;

import zone.amy.infinity.lib.RepresentableObject;
import zone.amy.infinity.session.SessionIdentity;
import zone.amy.infinity.session.SessionMemberConfiguration;
import zone.amy.infinity.session.SessionState;
import zone.amy.infinity.user.InfinityUser;

public interface Session extends RepresentableObject<SessionIdentity> {
    //   Start (by ident?)
    //   Stop
    //   get network representation x
    //   Alert of incoming player x
    //   Schedule Player direction to Session (Either immediately or when they join) x

    void load();
    void unload();

    boolean canUserJoin(InfinityUser user, SessionMemberConfiguration configuration);
    SessionState getState();

    void directUser(InfinityUser user, SessionMemberConfiguration configuration);
}
