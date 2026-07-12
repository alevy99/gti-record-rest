package ie.gti.asdl.rey.gtirecord.desktop.ui.validation;

import java.util.regex.Pattern;

/**
 * @author Andrei Levchenko
 */
public class PpsnValidator implements Validator<String> {

    private static final Pattern PPSN_PATTERN = Pattern.compile("^\\d{7}[A-Za-z]{1,2}$");

    @Override
    public boolean isValid(String data) {
        if (data == null) return false;
        return PPSN_PATTERN.matcher(data.trim()).matches();
    }
}