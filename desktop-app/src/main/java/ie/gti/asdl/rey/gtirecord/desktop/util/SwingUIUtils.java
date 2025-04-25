package ie.gti.asdl.rey.gtirecord.desktop.util;

import ie.gti.asdl.rey.gtirecord.core.validation.OptionalValidator;
import ie.gti.asdl.rey.gtirecord.core.validation.Validator;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ItemListener;
import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Andrei Levchenko
 */
public class SwingUIUtils {

    public static void addTableFilter(final JTable table, JTextField filterField) {
        addTableFilter(table, filterField, (val) -> {});
    }

    public static void addTableFilter(final JTable table, JTextField filterField, java.util.function.IntConsumer onRowSelected) {
        if (table.getRowSorter() instanceof TableRowSorter<?> sorter) {
            filterField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                public void insertUpdate(javax.swing.event.DocumentEvent e) {
                    filterTable();
                }

                public void removeUpdate(javax.swing.event.DocumentEvent e) {
                    filterTable();
                }

                public void changedUpdate(javax.swing.event.DocumentEvent e) {
                    filterTable();
                }

                private void filterTable() {
                    String text = filterField.getText().trim();
                    if (text.trim().isEmpty()) {
                        sorter.setRowFilter(null);
                    } else {
                        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                    }

                    SwingUtilities.invokeLater(() -> {
                        int viewRow = table.getRowCount() > 0 ? 0 : -1;

                        if (viewRow >= 0) {
                            table.setRowSelectionInterval(viewRow, viewRow);
                            table.scrollRectToVisible(table.getCellRect(viewRow, 0, true));
                            int modelRow = table.convertRowIndexToModel(viewRow);
                            onRowSelected.accept(modelRow);  // call callback method with selected row index
                        } else {
                            table.clearSelection();
                            onRowSelected.accept(-1); // no selected rows
                        }
                    });
                }
            });
        }
    }

    public static boolean confirmBatchTableAction(Component component, JTable table, int descriptionColumn, String title, String message) {
        if (table.getSelectedRows().length == 0) {
            return false;
        }
        return JOptionPane.showConfirmDialog(component,
                message + "\n" +
                        Arrays.stream(table.getSelectedRows())
                                .mapToObj(row -> {
                                    int modelRow = table.convertRowIndexToModel(row);
                                    var description = table.getModel().getValueAt(modelRow, descriptionColumn);
                                    return description == null ? "" : description.toString();
                                })
                                .collect(Collectors.joining("\n")),
                title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
    }

    // Checks if event is a part of multiple firing events, like f.e. ListSelectionListener
    // returns ListSelectionListener which fires only once
    public static ListSelectionListener createSafeListSelectionListener(Consumer<ListSelectionEvent> consumer) {
        return event -> {
            if (!event.getValueIsAdjusting()) {
                consumer.accept(event);
            }
        };
    }

    public static ItemListener createSafeItemListener(Consumer<ItemListener> consumer) {
        return event -> {
//            if (!event.getValueIsAdjusting()) {
//                consumer.accept(event);
//            }
        };
    }


    public static void addTextFieldValidation(JTextField textField, JLabel lblValidationStatus,
                                              Validator<String> validator, BiConsumer<JTextField, Boolean> validationCallback) {
        addTextFieldValidation(textField, lblValidationStatus, validator, validationCallback, true);
    }

    public static void addTextFieldValidation(JTextField textField, JLabel lblValidationStatus,
                                              Validator<String> validator, BiConsumer<JTextField, Boolean> validationCallback,
                                              boolean validateImmediately) {
        DocumentListener listener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                validate();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validate();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                validate();
            }

            private void validate() {
                String text = textField.getText();
                boolean isBlank = text == null || text.trim().isEmpty();
                boolean isValid = validator.isValid(text);

                if (isBlank && validator instanceof OptionalValidator) {
                    // Don't show any validation sign in UI
                    lblValidationStatus.setText("");
                    validationCallback.accept(textField, true); // considered valid
                } else {
                    lblValidationStatus.setText(isValid ? "✔" : "✘");
                    lblValidationStatus.setForeground(isValid ? Color.GREEN : Color.RED);
                    validationCallback.accept(textField, isValid);
                }
            }
        };

        textField.getDocument().addDocumentListener(listener);

        if (validateImmediately) {
            // Программно валидируем текущее значение
            listener.changedUpdate(null);  // safe null, просто вызывает validate()
        }
    }

}
