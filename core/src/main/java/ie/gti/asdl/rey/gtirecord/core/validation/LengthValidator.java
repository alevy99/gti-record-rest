package ie.gti.asdl.rey.gtirecord.core.validation;

/**
 * @author Andrei Levchenko
 */
public class LengthValidator implements Validator<String> {

    private final int min;
    private final int max;

    public LengthValidator(int min, int max) {
        if (min < 0 || max < min) {
            throw new IllegalArgumentException("Invalid length range: min=" + min + ", max=" + max);
        }
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean isValid(String data) {
        if (data == null) return false;

        String trimmed = data.trim();
        int length = trimmed.length();

        return length >= min && length <= max;
    }
}
