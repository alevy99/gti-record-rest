package ie.gti.asdl.rey.gtirecord.core.validation;

import java.util.regex.Pattern;

/**
 * @author Andrei Levchenko
 */
public class EircodeValidator implements Validator<String> {

    // Регулярное выражение для проверки формата Eircode
    private static final Pattern EIRCODE_PATTERN = Pattern.compile("^[A-Za-z]\\d{2} ?[A-Za-z0-9]{4}$");

    @Override
    public boolean isValid(String data) {
        if (data == null) return false;
        return EIRCODE_PATTERN.matcher(data.trim().toUpperCase()).matches();
    }
}