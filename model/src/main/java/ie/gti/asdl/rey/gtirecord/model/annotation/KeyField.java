package ie.gti.asdl.rey.gtirecord.model.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field as the key (identifier) field of an entity.
 * <p>
 * Fields annotated with {@code @KeyField} are typically discovered via
 * reflection to identify which field uniquely identifies an instance of the
 * entity, for example when building lookups, comparisons, or descriptions.
 *
 * @author Andrei Levchenko
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface KeyField {
}
