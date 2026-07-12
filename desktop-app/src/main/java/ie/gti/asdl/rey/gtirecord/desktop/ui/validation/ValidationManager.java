package ie.gti.asdl.rey.gtirecord.desktop.ui.validation;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Manages real-time validation of a group of {@link JTextField} inputs and
 * automatically enables or disables a control button (e.g. a "Save" button)
 * based on whether all registered fields are currently valid.
 * <p>
 * Each field is registered together with a {@link JLabel} (used to display
 * validation feedback) and a {@link Validator} that determines whether the
 * field's current text is valid. Validation is triggered automatically
 * whenever the field's content changes, via a {@link DocumentListener}.
 *
 * @author Andrei Levchenko
 */
public class ValidationManager {

    /** Bindings between text fields, their labels, and their validators. */
    private final List<ValidationBinding> bindings = new ArrayList<>();

    /** The button whose enabled state reflects the overall validity of all fields (e.g. a Save button). */
    private final JButton controlButton; // Save Button f.e.

    /** Tracks the current validity state of each registered text field. */
    private final Map<JTextField, Boolean> fieldValidity = new HashMap<>();

    /**
     * Creates a new {@code ValidationManager} that controls the enabled state
     * of the given button.
     *
     * @param controlButton the button to enable/disable based on overall field validity;
     *                       initially expected to be disabled until fields are validated
     */
    public ValidationManager(JButton controlButton) {
        this.controlButton = controlButton;
    }

    /**
     * Registers a text field for validation, along with a label used to display
     * validation feedback and a validator used to check the field's value.
     * <p>
     * The field starts out marked as invalid, and a {@link DocumentListener} is
     * attached so that validation is re-triggered automatically on every
     * insertion, removal, or change to the field's content.
     *
     * @param textField the text field to validate
     * @param label     the label used to display validation status/messages for this field
     * @param validator the validator used to determine whether the field's text is valid
     */
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

    /**
     * Triggers validation for all registered fields, updating their validity
     * state and the control button's enabled state accordingly.
     */
    public void validateAll() {
        for (ValidationBinding binding : bindings) {
            binding.triggerValidation();
        }
    }

    /**
     * Resets the validation state of all registered fields: clears their
     * status labels, marks all fields as invalid, and disables the control button.
     */
    public void resetValidation() {
        for (ValidationBinding binding : bindings) {
            binding.statusLabel.setText("");
            fieldValidity.put(binding.textField, false);
        }
        controlButton.setEnabled(false);
    }

    /**
     * Updates the recorded validity state for the given field and refreshes
     * the control button's enabled state based on the overall validity of all fields.
     * <p>
     * Invoked as a callback whenever a field's validation result changes.
     *
     * @param field   the field whose validity state changed
     * @param isValid the new validity state of the field
     */
    private void updateStatus(JTextField field, boolean isValid) {
        fieldValidity.put(field, isValid);
        controlButton.setEnabled(allValid());
    }

    /**
     * Checks whether all registered fields are currently valid.
     *
     * @return {@code true} if every registered field is valid, {@code false} otherwise
     */
    public boolean allValid() {
        return fieldValidity.values().stream().allMatch(Boolean::booleanValue);
    }
}
