package zone.amy.infinity.lib.database;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Operator {
    GREATER_THAN,
    LESS_THAN,
    EQUAL_TO;

    public Query.Condition of(Object subject) {
        return new Query.Condition(this, subject);
    }
}