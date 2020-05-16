package zone.amy.infinity.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import zone.amy.infinity.lib.ExternalRepresentation;

import java.util.UUID;

@RequiredArgsConstructor
public class IOfflineUser implements ExternalRepresentation {
    @Getter private final UUID uuid;
}
