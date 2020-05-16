package zone.amy.infinity.lib.database;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;

/**
 * Represents a value in the database (e.g. table/collection).
 * An instance represents a record from the database.
 * todo Ideally relationship stuff however that would only work if each model was accessible from the manager
 */
public abstract class Model {
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Container {
        String value();
    }
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Key {
    }
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Codec {
        Class<? extends DatabaseCodec> value();
    }

    public static Field getKeyField(Class<? extends Model> model) {
        for (Field field : model.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Model.Key.class)) return field;
        }
        return null;
    }

    public static String getContainerName(Class<? extends Model> model) {
        Model.Container container = model.getAnnotation(Model.Container.class);
        if (container == null) throw new IllegalStateException("Container annotation not found on model " + model.getName());
        else return container.value();
    }

    public static DatabaseCodec getCodec(Field field) {
        Model.Codec codecAnnotation = field.getAnnotation(Model.Codec.class);
        if (codecAnnotation == null) return null;

        try {
            return codecAnnotation.value().newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }
}
