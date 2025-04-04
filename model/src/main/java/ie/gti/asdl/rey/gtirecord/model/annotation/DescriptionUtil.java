package ie.gti.asdl.rey.gtirecord.model.annotation;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Andrei Levchenko
 */
public class DescriptionUtil {

    public static String getShortDescription(Object obj) {
        if (obj == null) {
            return "";
        }
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

                        return applyFormat(value.toString(), format);
                    } catch (Exception e) {
                        return "";
                    }
                })
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(" "));
    }

    // Apply format base on the format type
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
