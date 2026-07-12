package ie.gti.asdl.rey.gtirecord.model.annotation;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Utility class for generating a concise, human-readable short description of
 * an object, based on fields annotated with {@link ShortDescriptionField}.
 * <p>
 * Annotated fields are collected via reflection, ordered according to
 * {@link ShortDescriptionField#order()}, formatted according to
 * {@link ShortDescriptionFormat}, and joined together with spaces. Nested
 * non-simple-typed fields are processed recursively, with cycle detection to
 * avoid infinite recursion on mutually-referencing objects.
 */
public class DescriptionUtil {

    /**
     * Builds a short description string for the given object, based on its
     * fields annotated with {@link ShortDescriptionField}.
     *
     * @param obj the object to describe
     * @return a short description string composed of the object's annotated
     *         field values, joined with spaces; an empty string if the object
     *         has no annotated fields or is {@code null}
     */
    public static String getShortDescription(Object obj) {
        return getShortDescriptionRecursive(obj, new HashSet<>()); // avoid cycles
    }

    /**
     * Recursively builds a short description for the given object, tracking
     * already-visited objects to avoid infinite recursion caused by cyclic
     * (e.g. mutual) references.
     *
     * @param obj     the object to describe
     * @param visited the set of objects already visited during this recursive
     *                call chain, used to detect and break cycles
     * @return a short description string composed of the object's annotated
     *         field values (recursing into nested non-simple-typed fields),
     *         or an empty string if {@code obj} is {@code null}, already
     *         visited, or has no describable content
     */
    private static String getShortDescriptionRecursive(Object obj, Set<Object> visited) {
        if (obj == null || visited.contains(obj)) {
            return "";
        }

        visited.add(obj); // to avoid getting stuck on mutual links

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
                            // Recursive call for nested objects
                            return getShortDescriptionRecursive(value, visited);
                        }
                    } catch (Exception e) {
                        return "";
                    }
                })
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(" "));
    }

    /**
     * Checks whether the given value is of a "simple" type that can be
     * rendered directly via {@link Object#toString()}, as opposed to a
     * complex/nested object requiring recursive description.
     *
     * @param value the value to check
     * @return {@code true} if the value's type is primitive, {@link String},
     *         a {@link Number} subtype, {@link Boolean}, or {@link Character}
     */
    private static boolean isSimpleType(Object value) {
        Class<?> type = value.getClass();
        return type.isPrimitive() ||
                type == String.class ||
                Number.class.isAssignableFrom(type) ||
                type == Boolean.class ||
                type == Character.class;
    }

    /**
     * Applies the given formatting style to a field's string value.
     *
     * @param value  the raw string value to format
     * @param format the formatting style to apply
     * @return the formatted string according to {@code format}; if
     *         {@link ShortDescriptionFormat#DEFAULT}, the value is returned unchanged
     */
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
