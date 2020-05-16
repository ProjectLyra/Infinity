package zone.amy.infinity.lib.database.flat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.CaseFormat;
import database.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import zone.amy.infinity.database.*;
import zone.amy.infinity.lib.database.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * Stuck on encoded UUID not being equal to test UUID
 */
public class FlatfileDatabaseManager implements DatabaseManager {
    private ObjectMapper mapper;
    private File file = null;
    private JSONParser parser;

    public FlatfileDatabaseManager(File file) {
        this.file = file;
        this.mapper = new ObjectMapper();
        this.parser = new JSONParser();
    }

    private JSONObject getDatabase() {
        try {
            return (JSONObject) parser.parse(new FileReader(file));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public void saveDatabase(JSONObject object) {
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(object.toJSONString());
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private JSONObject getContainer(Class<? extends Model> model) {
        JSONObject container = (JSONObject) getDatabase().get(Model.getContainerName(model));
        if (container == null) container = new JSONObject();
        return container;
    }
    
    public List<Model> pullAllRecords(Class<? extends Model> model) {
        JSONObject result = getContainer(model);

        List<Model> models = new ArrayList<>();
        for (Object key : result.keySet()) {
            models.add(objectToModel(model, (String) key, (JSONObject) result.get(key)));
        }

        return models;
    }

    
    public List<Model> pullRecords(Class<? extends Model> model, Query query) {
        JSONObject result = getContainer(model);

        List<Model> models = new ArrayList<>();
        for (Object key : result.keySet()) {
            JSONObject object = (JSONObject) result.get(key);
            if (queryMatchesObject(query, object, model))  models.add(objectToModel(model, (String) key, object));
        }

        return models;
    }

    
    public Model pullRecord(Class<? extends Model> model, Query query) {
        JSONObject result = getContainer(model);

        for (Object key : result.keySet()) {
            JSONObject object = (JSONObject) result.get(key);
            if (queryMatchesObject(query, object, model)) return objectToModel(model, (String) key, object);
        }
        return null;
    }

    
    public Model pullRecord(Class<? extends Model> model, Object key) {
        return pullRecord(model, new Query(hFieldName(Model.getKeyField(model)), Operator.EQUAL_TO.of(key)));
    }

    
    public void pushRecord(Model model) {
        JSONObject object = new JSONObject();

        Field keyField = Model.getKeyField(model.getClass());

        try {
            for (Field field : model.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object obj = field.get(model);

                DatabaseCodec codec = Model.getCodec(field);
                if (codec != null) {
                    obj = codec.encode(obj);
                }

                if (obj instanceof Map) {
                    object.put(hFieldName(field), new JSONObject((Map) obj));
                } else if (obj instanceof Collection) {
                    JSONArray array = new JSONArray();
                    array.addAll((Collection) obj);
                    object.put(hFieldName(field), array);
                } else {
                    object.put(hFieldName(field), obj);
                }
            }
        }
        catch (Exception e) {
            throw new IllegalStateException("Unable to convert record: " + e.getMessage(), e);
        }

        try {
            JSONObject container = getContainer(model.getClass());
            container.put(keyField.get(model).toString(), object);
            JSONObject database = getDatabase();
            database.put(Model.getContainerName(model.getClass()), container);
            saveDatabase(database);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Unable to construct query: " + e.getMessage(), e);
        }
    }

    private Model objectToModel(Class<? extends Model> model, String key, JSONObject object) {
        Model record;

        try {
            record = model.getConstructor().newInstance();
            for (Field field : model.getDeclaredFields()) {
                field.setAccessible(true);
                Object out = object.get(hFieldName(field));

                DatabaseCodec codec = Model.getCodec(field);
                if (codec != null) {
                    out = codec.decode(out);
                }

                if (out instanceof JSONObject && field.getType() == Map.class) {
                    out = new ObjectMapper().readValue(((JSONObject) out).toJSONString(), new TypeReference<Map<String, Object>>(){});
                }
                if (out instanceof JSONArray) out = ((JSONArray) out).stream().collect(Collectors.toList());
                field.set(record, out);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Unable to construct record: " + e.getMessage(), e);
        }
        return record;
    }

    public boolean queryMatchesObject(Query query, JSONObject object, Class<? extends Model> model) {
        int conditionsMet = 0;

        object = (JSONObject) object.clone();

        for (Field field : model.getDeclaredFields()) {
            DatabaseCodec codec = Model.getCodec(field);
            if (codec != null) {
                Object value = object.get(hFieldName(field));
                object.put(hFieldName(field), codec.decode(value));
            }
        }

        for (Map.Entry<String, Query.Condition> component : query.getConditions().entrySet()) {
            Object value = object.get(component.getKey());

            switch (component.getValue().getOperator()) {
                case EQUAL_TO:
                    if (value.hashCode() == (component.getValue().getSubject().hashCode())) conditionsMet++;
                    break;
                case GREATER_THAN:
                    if ((Float) value > (Float) component.getValue().getSubject()) conditionsMet++;
                    break;
                case LESS_THAN:
                    if ((Float) value < (Float) component.getValue().getSubject()) conditionsMet++;
                    break;
            }
        }
        return conditionsMet == query.getConditions().size();
    }

    public static String hFieldName(Field field) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, field.getName());
    }
}
