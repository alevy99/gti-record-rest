package ie.gti.asdl.rey.gtirecord.model.annotation;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Andrei Levchenko
 */
public class DescriptionUtil {

    public static String getShortDescription(Object obj) {
        return getShortDescriptionRecursive(obj, new HashSet<>()); // избегаем циклов
    }

    private static String getShortDescriptionRecursive(Object obj, Set<Object> visited) {
        if (obj == null || visited.contains(obj)) {
            return "";
        }

        visited.add(obj); // чтобы не зациклиться на взаимных ссылках

        List<Field> fields = Arrays.stream(obj.getClass().getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(ShortDescriptionField.class))
                .sorted(Comparator.comparingInt(f -> f.getAnnotation(ShortDescriptionField.class).order()))
                .toList();

        return fields.stream()
                .map(field -> {
                    field.setAccessible(true);
                    try {
                        Object value = field.get(obj);
                        if (value == null) return "";

                        ShortDescriptionFormat format = field.getAnnotation(ShortDescriptionField.class).format();

                        if (isSimpleType(value)) {
                            return applyFormat(value.toString(), format);
                        } else {
                            // Рекурсивный вызов для вложенных объектов
                            return getShortDescriptionRecursive(value, visited);
                        }
                    } catch (Exception e) {
                        return "";
                    }
                })
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(" "));
    }

    private static boolean isSimpleType(Object value) {
        Class<?> type = value.getClass();
        return type.isPrimitive() ||
                type == String.class ||
                Number.class.isAssignableFrom(type) ||
                type == Boolean.class ||
                type == Character.class;
    }

    private static String applyFormat(String value, ShortDescriptionFormat format) {
        return switch (format) {
            case FIRST_LETTER -> value.toUpperCase().charAt(0) + ".";
            case UPPERCASE -> value.toUpperCase();
            case LOWERCASE -> value.toLowerCase();
            case NAME_FORMAT -> value.toUpperCase().charAt(0) + value.substring(1);
            default -> value; // DEFAULT — as is
        };
    }
}
