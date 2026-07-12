package ie.gti.asdl.rey.gtirecord.model.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Andrei Levchenko
 */
/**
 * Marks a field as part of an entity's short description, used to generate a
 * concise, human-readable summary (e.g. for display in UI lists, tables, or logs).
 * <p>
 * Fields annotated with {@code @ShortDescriptionField} are typically collected
 * via reflection and assembled into a description string, ordered according to
 * {@link #order()} and formatted according to {@link #format()}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ShortDescriptionField {

    /**
     * The order in which this field should appear relative to other
     * {@code @ShortDescriptionField}-annotated fields when building the
     * short description. Lower values appear first.
     *
     * @return the display order of this field, defaulting to {@code 0}
     */
    int order() default 0; // Order of fields in Description

    /**
     * The format used to render this field's value within the short description.
     *
     * @return the description format to apply to this field, defaulting to {@link ShortDescriptionFormat#DEFAULT}
     */
    ShortDescriptionFormat format() default ShortDescriptionFormat.DEFAULT; // Field description format
}
