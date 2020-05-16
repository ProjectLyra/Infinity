package zone.amy.infinity.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import zone.amy.infinity.lib.RepresentableObject;

import java.util.UUID;

@RequiredArgsConstructor
public class LocalUser implements RepresentableObject<InfinityUser> {
    @Getter private final UUID uuid;
    @Getter private final Player player;

    @Getter(lazy = true) private final InfinityUser externalRepresentation = new InfinityUser(uuid);
}
