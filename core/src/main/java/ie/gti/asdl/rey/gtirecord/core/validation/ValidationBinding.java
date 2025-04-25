package ie.gti.asdl.rey.gtirecord.core.validation;

import javax.swing.*;
import java.awt.*;
import java.util.function.BiConsumer;

/**
 * @author Andrei Levchenko
 */
public class ValidationBinding {
    public final JTextField textField;
    public final JLabel statusLabel;
    public final Validator<String> validator;
    public final BiConsumer<JTextField, Boolean> callback;

    public ValidationBinding(JTextField textField, JLabel statusLabel, Validator<String> validator, BiConsumer<JTextField, Boolean> callback) {
        this.textField = textField;
        this.statusLabel = statusLabel;
        this.validator = validator;
        this.callback = callback;
    }

    public void triggerValidation() {
        if (!textField.isEnabled() || !textField.isEditable()) {
            statusLabel.setText("");              // No ✔, ни ✘
            callback.accept(textField, true);  // Considered valid
            return;
        }

        String text = textField.getText();
        boolean isBlank = text == null || text.trim().isEmpty();
        boolean isValid = validator.isValid(text);

        if (isBlank && validator instanceof OptionalValidator) {
            statusLabel.setText("");
            callback.accept(textField, true);
        } else {
            statusLabel.setText(isValid ? "✔" : "✘");
            statusLabel.setForeground(isValid ? Color.GREEN : Color.RED);
            callback.accept(textField, isValid);
        }
    }
}