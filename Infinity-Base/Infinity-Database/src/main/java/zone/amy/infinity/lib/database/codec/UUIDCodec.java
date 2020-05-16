package zone.amy.infinity.lib.database.codec;


import zone.amy.infinity.lib.database.DatabaseCodec;

import java.util.UUID;

public class UUIDCodec implements DatabaseCodec {
    @Override
    public Object encode(Object in) {
        return in.toString();
    }

    @Override
    public UUID decode(Object in) {
        return UUID.fromString((String) in);
    }
}
