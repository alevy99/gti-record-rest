package ie.gti.asdl.rey.gtirecord.model.annotation;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * @author Andrei Levchenko
 */
public class InstanceFactory {

    // Типы, которые не нужно инициализировать
    private static final Set<Class<?>> SIMPLE_TYPES = Set.of(
            String.class,
            Integer.class,
            Long.class,
            Boolean.class,
            Double.class,
            Float.class,
            Short.class,
            Byte.class,
            Character.class
    );

    public static <T> T create(Class<T> clazz) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();

            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);

                Class<?> fieldType = field.getType();

                // Пропускаем простые типы и примитивы
                if (fieldType.isPrimitive() || SIMPLE_TYPES.contains(fieldType)) {
                    continue;
                }

                Object nestedInstance = create(fieldType);
                field.set(instance, nestedInstance);
            }

            return instance;

        } catch (Exception e) {
            throw new RuntimeException("Can't create instance of " + clazz.getSimpleName(), e);
        }
    }
}