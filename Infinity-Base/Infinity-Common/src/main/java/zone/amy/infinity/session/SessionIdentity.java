package zone.amy.infinity.session;

import lombok.Getter;
import zone.amy.infinity.lib.ExternalRepresentation;

import java.util.UUID;

public class SessionIdentity implements ExternalRepresentation {
    @Getter UUID uuid;
    @Getter String name;

}
