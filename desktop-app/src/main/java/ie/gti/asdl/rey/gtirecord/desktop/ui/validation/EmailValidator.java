package ie.gti.asdl.rey.gtirecord.desktop.ui.validation;

import java.util.regex.Pattern;

/**
 * @author Andrei Levchenko
 */
public class EmailValidator implements Validator<String> {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    @Override
    public boolean isValid(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
}
