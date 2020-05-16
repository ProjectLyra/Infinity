package zone.amy.infinity;

import zone.amy.infinity.session.SessionConfiguration;
import zone.amy.infinity.session.SessionIdentity;
import zone.amy.infinity.sessions.Session;

public interface CoreHook {
    // Create Session
    // Session (provide session managers?)
    //   Start
    //   Stop
    //   get network representation
    //   Check if can enter
    //   Add Player

    SessionIdentity createSession(SessionConfiguration configuration);
    Session getSessionInterface(SessionIdentity data);
}
