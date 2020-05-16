package zone.amy.infinity.session;

import zone.amy.infinity.Infinity;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {
    private Map<Class<? extends SessionConfiguration>, Class<? extends InfinitySession>> sessionClasses = new HashMap<>();
    private Map<SessionIdentity, SessionAgent> sessions = new HashMap<>();
    private PlayerRouter playerRouter = new PlayerRouter();

    public SessionManager() {
        Infinity.getInstance().getServer().getPluginManager().registerEvents(playerRouter, Infinity.getInstance());
    }

    public void registerSessionClass(
            Class<? extends SessionConfiguration> config,
            Class<? extends InfinitySession> type) {
        sessionClasses.put(config, type);
    }



    public SessionIdentity createSession(SessionConfiguration configuration) {
        Class<? extends SessionConfiguration> configClass = configuration.getClass();
        try {
            if (sessionClasses.containsKey(configClass)) {
                SessionAgent session = new SessionAgent(
                        playerRouter,
                        sessionClasses.get(configClass).getConstructor(configClass).newInstance(configuration)
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

    public SessionAgent getSessionInterface(SessionIdentity session) {
        return sessions.get(session);
    }
}
