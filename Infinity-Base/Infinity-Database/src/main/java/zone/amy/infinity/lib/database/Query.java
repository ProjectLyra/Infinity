package zone.amy.infinity.lib.database;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a database query
 * todo or statements
 */
public class Query {
    @Getter protected Map<String, Condition> conditions = new HashMap<>();

    public Query(String key, Condition value) {
        conditions.put(key, value);
    }

    public Query and(String key, Condition value) {
        conditions.put(key, value);
        return this;
    }

    @Getter
    @AllArgsConstructor
    public static class Condition {
        private Operator operator;
        private Object subject;
    }
}
