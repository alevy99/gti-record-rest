package ie.gti.asdl.rey.gtirecord.model.annotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Factory to create model entity's instances in a controlled way.
 * <p>
 * Uses reflection to recursively instantiate an object graph: primitive and
 * simple-typed fields (see {@link #SIMPLE_TYPES}) are populated with a default
 * value only if annotated with {@link DefaultIfNull}, collection-typed fields
 * ({@link Set}, {@link List}, {@link Map}) are initialized with empty mutable
 * instances, and all other non-final fields are recursively populated by
 * creating nested instances of their declared type.
 * <p>
 * This is typically used to build fully-initialized "empty" entity instances,
 * avoiding {@code null} references for nested objects and collections.
 *
 */
public class InstanceFactory {

    /** Types treated as simple/leaf values that are not recursively instantiated. */
    private static final Set<Class<?>> SIMPLE_TYPES = Set.of(
            String.class, LocalDate.class, LocalDateTime.class,
            Integer.class, Long.class,
            Boolean.class, Double.class, Float.class,
            Short.class, Byte.class, Character.class
    );

    /**
     * Creates a new instance of the given class, recursively populating its
     * fields.
     * <p>
     * Behavior per field:
     * <ul>
     *     <li>Final fields are left untouched.</li>
     *     <li>Primitive or {@link #SIMPLE_TYPES} fields are only set if
     *         annotated with {@link DefaultIfNull}, using the annotation's
     *         value parsed to the field's type; otherwise left as-is.</li>
     *     <li>{@link Set}, {@link List}, and {@link Map} fields are initialized
     *         with empty {@link HashSet}, {@link ArrayList}, and {@link HashMap}
     *         instances respectively.</li>
     *     <li>All other fields are populated by recursively calling this
     *         method on the field's declared type.</li>
     * </ul>
     * The class must declare a no-argument constructor (which may be private);
     * it will be made accessible via reflection.
     *
     * @param clazz the class to instantiate
     * @param <T>   the type of the instance to create
     * @return a fully-populated instance of {@code clazz}
     * @throws RuntimeException if the instance could not be created or its
     *                          fields could not be populated (e.g. missing
     *                          no-arg constructor, reflection access failure,
     *                          or unsupported default value type)
     */
    public static <T> T create(Class<T> clazz) {
        try {
            Constructor<T> ctor = clazz.getDeclaredConstructor();
            ctor.setAccessible(true);   // allow private-constructor
            T instance = ctor.newInstance();
//            T instance = clazz.getDeclaredConstructor().newInstance();

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

    /**
     * Parses the given string value into an instance of the specified target type.
     * <p>
     * Supports {@link String}, boxed and primitive numeric/boolean/character
     * types, as well as {@link LocalDate} and {@link LocalDateTime}.
     *
     * @param value the string representation of the value to parse
     * @param type  the target type to parse the value into
     * @return the parsed value as an instance of {@code type}
     * @throws IllegalArgumentException if {@code type} is not one of the supported types
     */
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