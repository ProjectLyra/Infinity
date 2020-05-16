package zone.amy.infinity.user;

import zone.amy.infinity.lib.ExternalRepresentation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class InfinityUser implements ExternalRepresentation {
    @Getter private final UUID uuid;
}
