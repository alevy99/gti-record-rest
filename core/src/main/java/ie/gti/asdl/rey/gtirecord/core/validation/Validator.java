package ie.gti.asdl.rey.gtirecord.core.validation;

/**
 * @author Andrei Levchenko
 */
public interface Validator<T> {

    boolean isValid(T data);

}
