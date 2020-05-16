package zone.amy.infinity;

import zone.amy.infinity.session.SessionConfiguration;
import zone.amy.infinity.session.SessionIdentity;
import zone.amy.infinity.sessions.Session;
import zone.amy.infinity.sessions.SessionHandler;

import java.util.HashMap;
import java.util.Map;

public class CoreAgent implements CoreHook {
    private Map<SessionIdentity, SessionHandler> sessions = new HashMap<>();
    private PlayerRouter playerRouter = new PlayerRouter();

    CoreAgent() {
        Infinity.getInstance().getServer().getPluginManager().registerEvents(playerRouter, Infinity.getInstance());
    }


    @Override
    public SessionIdentity createSession(SessionConfiguration configuration) {
        Class<? extends SessionConfiguration> configClass = configuration.getClass();
        try {
            if (Infinity.getInstance().getSessionClasses().containsKey(configClass)) {
                SessionHandler session = new SessionHandler(playerRouter,
                        Infinity.getInstance().getSessionClasses().get(configClass)
                                .getConstructor(configClass).newInstance(configuration)
                );

                sessions.put(session.getExternalRepresentation(), session);

                return session.getExternalRepresentation();
            } else {
                throw new IllegalStateException("No session code prepared for that configuration type!");
            }
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public Session getSessionInterface(SessionIdentity session) {
        return sessions.get(session);
    }
}
