package ie.gti.asdl.rey.gtirecord.core.validation;

import java.util.regex.Pattern;

/**
 * @author Andrei Levchenko
 */
public class PhoneNumberValidator implements Validator<String> {

    // PhoneNum regexp
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^\\+?(\\d{1,3})?\\s?(\\(?\\d{3}\\)?)?\\s?\\d{3}[- ]?\\d{2}[- ]?\\d{2}$"
    );

    @Override
    public boolean isValid(String data) {
        if (data == null) return false;
        return PHONE_PATTERN.matcher(data.trim().toUpperCase()).matches();
    }
}
