package zone.amy.infinity.lib.network;


import lombok.Getter;
import lombok.NonNull;
import org.joda.time.DateTime;

import java.io.*;
import java.util.UUID;

public final class NetworkMessage implements Serializable {
    @Getter @NonNull private final UUID identifier;
    @Getter @NonNull private final NetworkIdentity source;
    @Getter @NonNull private final Content content;
    @Getter protected DateTime lastSentAt;

    public NetworkMessage(NetworkIdentity source, Content content) {
        this.identifier = UUID.randomUUID(); // This will only ever be run on initialisation, NOT deserialisation
        this.source = source;
        this.content = content;
    }

    public byte[] toByteArray() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeObject(this);
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static NetworkMessage fromByteArray(byte[] bytes) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInput in = new ObjectInputStream(bis);
            return (NetworkMessage) in.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Could not interpret NetworkMessage");
            e.printStackTrace();
        }
        return null;
    }

    public static class Content implements Serializable {

    }
}
