package ie.gti.asdl.rey.gtirecord.desktop.ui.comp;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.regex.Pattern;

public class PhoneNumberValidator implements DocumentListener {
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^\\+?(\\d{1,3})?\\s?(\\(?\\d{3}\\)?)?\\s?\\d{3}[- ]?\\d{2}[- ]?\\d{2}$"
    );

    private final JTextField textField;
    private final JLabel errorLabel;

    public PhoneNumberValidator(JTextField textField, JLabel errorLabel) {
        this.textField = textField;
        this.errorLabel = errorLabel;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        validateInput();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        validateInput();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        // Not needed for plain text components
    }

    private void validateInput() {
        String input = textField.getText().trim();
        if (!input.isEmpty() && !PHONE_PATTERN.matcher(input).matches()) {
            // Set the error icon when the phone number is invalid
            errorLabel.setIcon(new ImageIcon(getClass().getResource("/img/error.png"))); // Path to your error icon image
        } else {
            // Clear the error icon when the phone number is valid
            errorLabel.setIcon(null);
        }
    }
}
