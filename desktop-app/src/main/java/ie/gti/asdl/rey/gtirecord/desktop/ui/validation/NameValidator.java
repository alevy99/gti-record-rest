package ie.gti.asdl.rey.gtirecord.desktop.ui.validation;

import java.util.regex.Pattern;

/**
 * @author Andrei Levchenko
 */
public class NameValidator implements Validator<String> {

    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z'-]{2,50}$");

    @Override
    public boolean isValid(String name) {
        return name != null && NAME_PATTERN.matcher(name).matches();
    }
}
