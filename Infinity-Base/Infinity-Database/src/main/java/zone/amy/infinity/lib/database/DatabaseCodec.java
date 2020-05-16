package zone.amy.infinity.lib.database;

public interface DatabaseCodec {
    Object encode(Object in);
    Object decode(Object in);
}
