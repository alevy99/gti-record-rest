package ie.gti.asdl.rey.gtirecord.model.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies a default value to assign to a field when it needs to be
 * populated but has no explicit value provided.
 * <p>
 * Used by {@link InstanceFactory#create(Class)} to populate primitive and
 * simple-typed fields (see {@code InstanceFactory.SIMPLE_TYPES}): the
 * annotation's {@link #value()} is parsed into the field's actual type and
 * assigned to the field.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DefaultIfNull {

    /**
     * The default value to assign to the annotated field, expressed as a string
     * and parsed into the field's declared type.
     *
     * @return the string representation of the default value
     */
    String value();
}