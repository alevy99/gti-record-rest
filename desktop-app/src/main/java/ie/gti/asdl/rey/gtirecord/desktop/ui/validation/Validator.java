package ie.gti.asdl.rey.gtirecord.desktop.ui.validation;

/**
 * @author Andrei Levchenko
 */
public interface Validator<T> {

    boolean isValid(T data);

}
