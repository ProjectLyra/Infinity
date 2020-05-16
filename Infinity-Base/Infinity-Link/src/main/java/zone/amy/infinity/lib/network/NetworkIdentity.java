package zone.amy.infinity.lib.network;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

// aka network endpoint
@RequiredArgsConstructor
public class NetworkIdentity implements Serializable {
    @Getter private final UUID identifier = UUID.randomUUID();

    @Override
    public boolean equals(Object obj) {
        return obj.hashCode() == hashCode();
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }
}
