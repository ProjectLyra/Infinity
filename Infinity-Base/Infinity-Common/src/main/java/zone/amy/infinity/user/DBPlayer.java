package zone.amy.infinity.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.json.simple.JSONObject;
import zone.amy.infinity.lib.database.Model;
import zone.amy.infinity.lib.database.codec.UUIDCodec;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Player Database State (volatile, should not be considered accurate past initial use)
 */
@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Model.Container("players")
public class DBPlayer extends Model {
    @Model.Key @Codec(UUIDCodec.class) private UUID uuid;
    private String lastName;
    private long firstJoinTime;
    private long lastJoinTime;

    /**
     * Permissions
     */
    protected Map<String, Boolean> permissions;
    protected List<String> groups;

    /**
     * Custom data applied to the user by plugins
     */
    protected JSONObject metadata;
}
