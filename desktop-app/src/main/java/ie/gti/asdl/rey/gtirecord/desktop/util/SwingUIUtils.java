package ie.gti.asdl.rey.gtirecord.desktop.util;

import ie.gti.asdl.rey.gtirecord.desktop.ui.comp.DataTableModel;
import ie.gti.asdl.rey.gtirecord.model.entity.Module;

import javax.swing.*;
import javax.swing.table.TableRowSorter;

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

}
