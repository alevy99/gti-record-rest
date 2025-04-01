package ie.gti.asdl.rey.gtirecord.model.annotation;

import java.lang.reflect.Field;

/**
 * @author Andrei Levchenko
 */
public class KeyUtil {

    public static boolean hasKey(Object obj) {
        Field keyField = getKeyField(obj);
        if (keyField == null) return false;

        keyField.setAccessible(true);
        try {
            Integer value = (Integer) keyField.get(obj);
            return value != null && value != 0; // Проверяем, что ID не null и не 0
        } catch (IllegalAccessException e) {
            return false;
        }
    }

    public static void setKey(Object obj, Integer value) {
        Field keyField = getKeyField(obj);
        if (keyField == null) throw new RuntimeException("Key field not found");

        keyField.setAccessible(true);
        try {
            keyField.set(obj, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot set key field", e);
        }
    }

    public static Integer getKey(Object obj) {
        Field keyField = getKeyField(obj);
        if (keyField == null) return null;

        keyField.setAccessible(true);
        try {
            return (Integer) keyField.get(obj);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot access key field", e);
        }
    }

    // Получает единственное поле с @KeyField
    private static Field getKeyField(Object obj) {
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(KeyField.class) && field.getType() == Integer.class) {
                return field;
            }
        }
        return null;
    }

}
