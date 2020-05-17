package zone.amy.infinity.session;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import zone.amy.infinity.lib.ExternalRepresentation;

import java.util.UUID;

@EqualsAndHashCode
public class SessionIdentity implements ExternalRepresentation {
    @Getter UUID uniqueId;

}
