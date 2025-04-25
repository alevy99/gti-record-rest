package ie.gti.asdl.rey.gtirecord.core.validation;

/**
 * @author Andrei Levchenko
 */
public class OptionalValidator<T> implements Validator<T> {

    private final Validator<T> innerValidator;

    public OptionalValidator(Validator<T> innerValidator) {
        this.innerValidator = innerValidator;
    }

    @Override
    public boolean isValid(T data) {
        if (data == null) return true;
        if (data instanceof String str && str.trim().isEmpty()) return true;

        return innerValidator.isValid(data);
    }
}
