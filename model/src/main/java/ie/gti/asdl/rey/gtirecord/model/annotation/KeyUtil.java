package ie.gti.asdl.rey.gtirecord.model.annotation;

import ie.gti.asdl.rey.gtirecord.model.util.Pair;

import java.lang.reflect.Field;

/**
 * @author Andrei Levchenko
 */
public class KeyUtil {

    public static boolean hasKey(Object obj) {
        Pair<Field,Object> fieldAndObj = getKeyField(obj);
        if (fieldAndObj == null) return false;
        Field keyField = fieldAndObj.getValue1();
        if (keyField == null) return false;

        keyField.setAccessible(true);
        try {
            Integer value = (Integer) keyField.get(fieldAndObj.getValue2());
            return value != null && value != 0; // Check Id is not null or 0
        } catch (IllegalAccessException e) {
            return false;
        }
    }

    public static void setKey(Object obj, Integer value) {
        Pair<Field,Object> keyFieldAndObj = getKeyField(obj);
        if (keyFieldAndObj == null) throw new RuntimeException("Key field not found");
        Field keyField = keyFieldAndObj.getValue1();
        if (keyField == null) throw new RuntimeException("Key field not found");

        keyField.setAccessible(true);
        try {
            keyField.set(keyFieldAndObj.getValue2(), value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot set key field", e);
        }
    }

    public static Integer getKey(Object obj) {
        Pair<Field,Object> keyFieldAndObj = getKeyField(obj);
        if (keyFieldAndObj == null) return null;
        Field keyField = keyFieldAndObj.getValue1();
        if (keyField == null) return null;

        keyField.setAccessible(true);
        try {
            return (Integer) keyField.get(keyFieldAndObj.getValue2());
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot access key field", e);
        }
    }

    // Get the only field with @KeyField
    private static Pair<Field,Object> getKeyField(Object obj) {
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(KeyField.class)) {
                if (field.getType() == Integer.class) {
                    return new Pair<>(field, obj);
                } else {
                    field.setAccessible(true);
                    try {
                        Object fieldObj = field.get(obj);
                        return getKeyField(fieldObj);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Cannot access key field", e);
                    }
                }
            }
        }
        return null;
    }

}
