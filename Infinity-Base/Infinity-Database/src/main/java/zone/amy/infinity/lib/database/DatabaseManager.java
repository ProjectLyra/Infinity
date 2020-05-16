package zone.amy.infinity.lib.database;

import java.util.List;

/**
 * Really abstract model-based database manager
 */
public interface DatabaseManager {
    List<Model> pullAllRecords(Class<? extends Model> model);
    List<Model> pullRecords(Class<? extends Model> model, Query query);
    Model pullRecord(Class<? extends Model> model, Object key);
    Model pullRecord(Class<? extends Model> model, Query query);
    void pushRecord(Model model);
}
