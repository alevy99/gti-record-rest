package ie.gti.asdl.rey.gtirecord.core.validation;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Andrei Levchenko
 */
public class ValidationManager {

    private final List<ValidationBinding> bindings = new ArrayList<>();
    private final JButton controlButton; // Кнопка "Сохранить" или аналог
    private final Map<JTextField, Boolean> fieldValidity = new HashMap<>();

    public ValidationManager(JButton controlButton) {
        this.controlButton = controlButton;
    }

    public void addField(JTextField textField, JLabel label, Validator<String> validator) {
        ValidationBinding binding = new ValidationBinding(textField, label, validator, this::updateStatus);
        bindings.add(binding);
        fieldValidity.put(textField, false); // стартовое состояние
        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { binding.triggerValidation(); }
            @Override public void removeUpdate(DocumentEvent e) { binding.triggerValidation(); }
            @Override public void changedUpdate(DocumentEvent e) { binding.triggerValidation(); }
        });
    }

    public void validateAll() {
        for (ValidationBinding binding : bindings) {
            binding.triggerValidation();
        }
    }

    public void resetValidation() {
        for (ValidationBinding binding : bindings) {
            binding.statusLabel.setText("");
            fieldValidity.put(binding.textField, false);
        }
        controlButton.setEnabled(false);
    }

    private void updateStatus(JTextField field, boolean isValid) {
        fieldValidity.put(field, isValid);
        controlButton.setEnabled(allValid());
    }

    public boolean allValid() {
        return fieldValidity.values().stream().allMatch(Boolean::booleanValue);
    }
}
