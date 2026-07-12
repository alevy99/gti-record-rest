package ie.gti.asdl.rey.gtirecord.model.annotation;

import ie.gti.asdl.rey.gtirecord.model.util.Pair;

import java.lang.reflect.Field;

/**
 * Utility class for locating and manipulating an entity's key (identifier)
 * field via reflection, based on the {@link KeyField} annotation.
 * <p>
 * The key field may be declared directly on the given object, or on a nested
 * object reachable through a chain of {@link KeyField}-annotated fields — the
 * search recurses until a field of type {@link Integer} is found. The key
 * field is expected to be of type {@link Integer}.
 */
public class KeyUtil {

    /**
     * Checks whether the given object has a valid (non-null, non-zero) key value.
     *
     * @param obj the object whose key field should be checked
     * @return {@code true} if a key field was found and its value is neither
     *         {@code null} nor {@code 0}; {@code false} if no key field was
     *         found, its value is {@code null}/{@code 0}, or it could not be accessed
     */
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

    /**
     * Sets the value of the given object's key field.
     *
     * @param obj   the object whose key field should be set
     * @param value the value to assign to the key field
     * @throws RuntimeException if no key field could be found on the object,
     *                          or if the field could not be set
     */
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

    /**
     * Retrieves the value of the given object's key field.
     *
     * @param obj the object whose key field value should be retrieved
     * @return the value of the key field, or {@code null} if no key field
     *         could be found on the object
     * @throws RuntimeException if the key field could not be accessed
     */
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

    /**
     * Recursively locates the field annotated with {@link KeyField} on the
     * given object, along with the object instance that directly declares it.
     * <p>
     * If the annotated field is of type {@link Integer}, it is returned
     * directly together with {@code obj}. Otherwise, the search continues
     * recursively into the value held by that field, allowing the key to be
     * nested inside another object (e.g. a related entity).
     *
     * @param obj the object to search for a {@link KeyField}-annotated field
     * @return a {@link Pair} containing the key field and the object instance
     *         it belongs to, or {@code null} if no {@link KeyField}-annotated
     *         field was found
     * @throws RuntimeException if a nested field's value could not be accessed
     */
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
