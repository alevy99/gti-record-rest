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

                // Пропуск финальных полей, которые уже инициализированы
                if (Modifier.isFinal(field.getModifiers())) {
                    continue;
                }

                Class<?> fieldType = field.getType();

                // Пропускаем примитивы и простые типы
                if (fieldType.isPrimitive() || SIMPLE_TYPES.contains(fieldType)) {
                    continue;
                }

                Object valueToSet = null;

                // Обработка Set / List
                if (Set.class.isAssignableFrom(fieldType)) {
                    valueToSet = new HashSet<>();
                } else if (List.class.isAssignableFrom(fieldType)) {
                    valueToSet = new ArrayList<>();
                } else if (Map.class.isAssignableFrom(fieldType)) {
                    valueToSet = new HashMap<>();
                } else {
                    // Обычный вложенный объект
                    valueToSet = create(fieldType);
                }

                field.set(instance, valueToSet);
            }

            return instance;

        } catch (Exception e) {
            throw new RuntimeException("Can't create instance of " + clazz.getSimpleName(), e);
        }
    }
}
