package zone.amy.infinity.lib.database.mongo;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.CaseFormat;
import com.mongodb.*;
import zone.amy.infinity.lib.database.DatabaseManager;
import zone.amy.infinity.lib.database.Model;
import zone.amy.infinity.lib.database.Operator;
import lombok.Getter;
import org.json.simple.JSONObject;
import zone.amy.infinity.lib.database.Query;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MongoDatabaseManager implements DatabaseManager {
    @Getter
    private DB database;
    private ObjectMapper mapper;

    public MongoDatabaseManager(String host, int port, String database) {
        MongoClient client = new MongoClient(host, port);
        this.database = client.getDB(database);
        this.mapper = new ObjectMapper();
    }

    
    public List<Model> pullAllRecords(Class<? extends Model> model) {
        DBCursor result = getCollection(model).find();

        List<Model> models = new ArrayList<>();
        for (DBObject document : result) {
            models.add(documentToModel(model, document));
        }

        return models;
    }

    
    public List<Model> pullRecords(Class<? extends Model> model, Query query) {
        DBCursor result = getCollection(model).find(queryToDBObject(query));

        List<Model> models = new ArrayList<>();
        for (DBObject document : result) {
            models.add(documentToModel(model, document));
        }

        return models;
    }

    
    public Model pullRecord(Class<? extends Model> model, Query query) {
        DBObject result = getCollection(model).findOne(queryToDBObject(query));

        if (result == null) return null;

        return documentToModel(model, result);
    }

    
    public Model pullRecord(Class<? extends Model> model, Object key) {
        return pullRecord(model, new Query(fieldName(Model.getKeyField(model)), Operator.EQUAL_TO.of(key)));
    }

    
    public void pushRecord(Model model) {
        BasicDBObject document = new BasicDBObject();

        try {
            for (Field field : model.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                if (!field.isAnnotationPresent(Model.Key.class)) {
                    Object obj = field.get(model);
                    document.append(fieldName(field), obj);
                }
            }
        }
        catch (Exception e) {
            throw new IllegalStateException("Unable to convert record: " + e.getMessage(), e);
        }

        Field keyField = Model.getKeyField(model.getClass());

        try {
            getCollection(model.getClass()).update(
                    new BasicDBObject(fieldName(keyField), keyField.get(model)),
                    new BasicDBObject("$set", document),
                    true,
                    false
            );
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Unable to construct query: " + e.getMessage(), e);
        }
    }

    private Model documentToModel(Class<? extends Model> model, DBObject document) {
        Model record;

        try {
            record = model.getConstructor().newInstance();
            for (Field field : model.getDeclaredFields()) {
                field.setAccessible(true);
                Object out = document.get(fieldName(field));
                if (out instanceof BasicDBObject) {
                    if (field.getType() == JSONObject.class) {
                        out = new JSONObject(((BasicDBObject) out).toMap());
                    } else if (field.getType() == Map.class) {
                        out = ((BasicDBObject) out).toMap();
                    }
                }
                if (out instanceof BasicDBList) out = ((BasicDBList) out).stream().collect(Collectors.toList());
                field.set(record, out);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Unable to construct record: " + e.getMessage(), e);
        }
        return record;
    }

    private DBCollection getCollection(Class<? extends Model> model) {
        return database.getCollection(Model.getContainerName(model));
    }

    public static DBObject queryToDBObject(Query query) {
        BasicDBObject dbQuery = new BasicDBObject();
        for (Map.Entry<String, Query.Condition> component : query.getConditions().entrySet()) {
            BasicDBObject dbComponent;
            switch (component.getValue().getOperator()) {
                case GREATER_THAN:
                    dbComponent = new BasicDBObject("$gt", component.getValue().getSubject());
                    break;
                case LESS_THAN:
                    dbComponent = new BasicDBObject("$lt", component.getValue().getSubject());
                    break;
                case EQUAL_TO:
                    dbComponent = new BasicDBObject("$eq", component.getValue().getSubject());
                    break;
                default:
                    throw new IllegalStateException("No mongo equivalent query operator set for " + component.getValue().getOperator().name());//Shouldn't Happen
            }
            dbQuery.append(component.getKey(), dbComponent);
        }
        return dbQuery;
    }

    public static String fieldName(Field field) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, field.getName());
    }
}
