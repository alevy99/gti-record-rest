package ie.gti.asdl.rey.gtirecord.desktop.util;

import ie.gti.asdl.rey.gtirecord.core.validation.Validator;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Andrei Levchenko
 */
public class SwingUIUtils {

    public static void addTableFilter(final JTable table, JTextField filterField) {
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
                    String text = filterField.getText();
                    if (text.trim().isEmpty()) {
                        sorter.setRowFilter(null);
                    } else {
                        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                    }
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
                        Arrays.stream(table.getSelectedRows()).
                                mapToObj(row -> table.getModel().getValueAt(row, descriptionColumn).toString()).
                                collect(Collectors.joining(", ")),
                title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
    }

    // Checks if event is a part of multiple firing events, like f.e. ListSelectionListener
    // returns ListSelectionListener which fires only once
    public static ListSelectionListener createSafeListener(Consumer<ListSelectionEvent> consumer) {
        return event -> {
            if (!event.getValueIsAdjusting()) {
                consumer.accept(event);
            }
        };
    }

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public static void addTextFieldValidation(JTextField textField, JLabel lblValidationStatus, Validator<String> validator) {
        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                validateEmail();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validateEmail();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                validateEmail();
            }

            private void validateEmail() {
                String text = textField.getText();
                if (validator.isValid(text)) {
                    lblValidationStatus.setText("✔");
                    lblValidationStatus.setForeground(Color.GREEN);
                } else {
                    lblValidationStatus.setText("✘");
                    lblValidationStatus.setForeground(Color.RED);
                }
            }
        });

    }

}
