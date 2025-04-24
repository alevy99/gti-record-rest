package ie.gti.asdl.rey.gtirecord.model.annotation;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author Andrei Levchenko
 */
public class InstanceFactory {

    private static final Set<Class<?>> SIMPLE_TYPES = Set.of(
            String.class, LocalDate.class, LocalDateTime.class,
            Integer.class, Long.class,
            Boolean.class, Double.class, Float.class,
            Short.class, Byte.class, Character.class
    );

    public static <T> T create(Class<T> clazz) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();

            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);

                // Skip final fields
                if (Modifier.isFinal(field.getModifiers())) {
                    continue;
                }

                Class<?> fieldType = field.getType();

                Object valueToSet = null;

                if (fieldType.isPrimitive() || SIMPLE_TYPES.contains(fieldType)) {
                    // Check on DefaultIfNull annotation
                    DefaultIfNull defaultIfNull = field.getAnnotation(DefaultIfNull.class);
                    if (defaultIfNull != null) {
                        valueToSet = parseValue(defaultIfNull.value(), fieldType);
                        field.set(instance, valueToSet);
                    }
                    continue;
                }

                // Handle collections
                if (Set.class.isAssignableFrom(fieldType)) {
                    valueToSet = new HashSet<>();
                } else if (List.class.isAssignableFrom(fieldType)) {
                    valueToSet = new ArrayList<>();
                } else if (Map.class.isAssignableFrom(fieldType)) {
                    valueToSet = new HashMap<>();
                } else {
                    valueToSet = create(fieldType);
                }

                field.set(instance, valueToSet);
            }

            return instance;

        } catch (Exception e) {
            throw new RuntimeException("Can't create instance of " + clazz.getSimpleName(), e);
        }
    }

    private static Object parseValue(String value, Class<?> type) {
        if (type == String.class) return value;
        if (type == Integer.class || type == int.class) return Integer.valueOf(value);
        if (type == Long.class || type == long.class) return Long.valueOf(value);
        if (type == Boolean.class || type == boolean.class) return Boolean.valueOf(value);
        if (type == Double.class || type == double.class) return Double.valueOf(value);
        if (type == Float.class || type == float.class) return Float.valueOf(value);
        if (type == Short.class || type == short.class) return Short.valueOf(value);
        if (type == Byte.class || type == byte.class) return Byte.valueOf(value);
        if (type == Character.class || type == char.class) return value.charAt(0);
        if (type == LocalDate.class) return LocalDate.parse(value);
        if (type == LocalDateTime.class) return LocalDateTime.parse(value);
        throw new IllegalArgumentException("Unsupported default value type: " + type.getName());
    }
}
