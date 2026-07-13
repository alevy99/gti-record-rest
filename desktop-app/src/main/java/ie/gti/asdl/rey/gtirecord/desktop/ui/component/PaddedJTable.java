package ie.gti.asdl.rey.gtirecord.desktop.ui.component;

import lombok.Getter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import static javax.swing.SwingConstants.CENTER;

public class PaddedJTable extends JTable {

    private Boolean suppressSelectionEvents = false;

    @Getter
    private final ListSelectionListenerCreator listSelectionListener;

    public PaddedJTable() {
        this(null);
    }

    public PaddedJTable(Supplier<Integer> highlightedRowSupplier) {
        super();
        listSelectionListener = new ListSelectionListenerCreator(() -> suppressSelectionEvents);

        setRowHeight(25); // Increase row height for better spacing
        setDefaultRenderer(Object.class, new PaddedCellRenderer(highlightedRowSupplier));
        setDefaultRenderer(String.class, new PaddedCellRenderer(highlightedRowSupplier));
        setDefaultRenderer(Integer.class, new PaddedCellRenderer(highlightedRowSupplier));
        setDefaultRenderer(Double.class, new DecimalFormatCellRenderer(highlightedRowSupplier));
        setDefaultRenderer(Boolean.class, new BooleanCellRenderer(highlightedRowSupplier));
        setDefaultEditor(Object.class, new PaddedCellEditor());
        // Add sorter to the table
        if (getRowSorter() == null) {
            setRowSorter(new TableRowSorter<>(this.getModel()));
        }

        JTableHeader header = getTableHeader();
        header.setFont(new Font("Comic", Font.PLAIN, 14)); // Plain, larger font
        header.setBackground(Color.LIGHT_GRAY); // Dark background
        header.setForeground(Color.DARK_GRAY); // White text
        header.setOpaque(true);
        header.setPreferredSize(new Dimension(header.getWidth(), 30));
        ((DefaultTableCellRenderer ) header.getDefaultRenderer()).setHorizontalAlignment(CENTER);
    }

    public void setHighlightedRowSupplier(Supplier<Integer> highlightedRowSupplier) {
        if (highlightedRowSupplier == null) {
            return;
        }
        if (getDefaultRenderer(Object.class) instanceof PaddedCellRenderer renderer) {
            renderer.setHighlightedRowSupplier(highlightedRowSupplier);
        }
        if (getDefaultRenderer(String.class) instanceof PaddedCellRenderer renderer) {
            renderer.setHighlightedRowSupplier(highlightedRowSupplier);
        }
        if (getDefaultRenderer(Integer.class) instanceof PaddedCellRenderer renderer) {
            renderer.setHighlightedRowSupplier(highlightedRowSupplier);
        }
        if (getDefaultRenderer(Boolean.class) instanceof BooleanCellRenderer renderer) {
            renderer.setHighlightedRowSupplier(highlightedRowSupplier);
        }
    }

    public void clear() {
        suppressSelectionEvents = true;
        if (getModel() instanceof DefaultTableModel model) {
            model.setRowCount(0);
        }
        if (getModel() instanceof DataTableModel<?> model) {
            model.clear();
            model.setRowCount(0);
        }
        suppressSelectionEvents = false;
    }

    public void setColumnUnique(int column) {
        getColumnModel().getColumn(column).setCellEditor(new UniqueCellEditor(this, column));

    }

    // Custom Cell Editor with Padding
    private static class PaddedCellEditor extends DefaultCellEditor {
        public PaddedCellEditor() {
            super(new JTextField());
            ((JTextField) getComponent()).setBorder(new EmptyBorder(0, 5, 0, 5)); // Add padding to the editor
        }

        public Object getCellEditorValue() {
            Object value = super.getCellEditorValue();
            return (value instanceof String) ? ((String) value).trim() : null;
        }
    }

    private static class UniqueCellEditor extends PaddedCellEditor {

        private boolean isValid = true;

        private final JTextField textField;

        private final JTable table;

        private final int column;

        public UniqueCellEditor(JTable table, int column) {
            super();
            this.textField = (JTextField) super.getComponent();
            this.table = table;
            this.column = column;
            init();
        }

        private void init() {
            // Adding DocumentListener to the input field
            textField.getDocument().addDocumentListener(new DocumentListener() {
                public void insertUpdate(DocumentEvent e) { validateInput(); }
                public void removeUpdate(DocumentEvent e) { validateInput(); }
                public void changedUpdate(DocumentEvent e) { validateInput(); }

                private void validateInput() {
                    SwingUtilities.invokeLater(() -> {
                        String input = textField.getText().trim();
                        int editingRow = table.getEditingRow();
                        Set<String> existing = new HashSet<>();

                        for (int row = 0; row < table.getRowCount(); row++) {
                            if (row != editingRow) {
                                Object value = table.getValueAt(row, column);
                                if (value != null) {
                                    existing.add(value.toString().trim());
                                }
                            }
                        }

                        // Highlight
                        if (existing.contains(input)) {
                            textField.setBackground(new Color(255, 200, 200)); // красный
                            isValid = false;
                        } else {
                            textField.setBackground(Color.WHITE);
                            isValid = true;
                        }
                    });
                }
            });
        }

        @Override
        public boolean stopCellEditing() {
            return isValid && super.stopCellEditing(); // Только если значение уникально
        }

        @Override
        public Object getCellEditorValue() {
            String value = (String) super.getCellEditorValue();
            return value != null ? value.trim() : null;
        }
    }

}