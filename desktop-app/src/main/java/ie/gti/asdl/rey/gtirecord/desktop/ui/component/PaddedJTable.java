package ie.gti.asdl.rey.gtirecord.desktop.ui.component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.util.function.Supplier;

import static javax.swing.SwingConstants.CENTER;

public class PaddedJTable extends JTable {

    public PaddedJTable() {
        this(null);
    }

    public PaddedJTable(Supplier<Integer> highlightedRowSupplier) {
        super();
//        java.awt.EventQueue.invokeLater(() -> {
            setRowHeight(25); // Increase row height for better spacing
            setDefaultRenderer(Object.class, new PaddedCellRenderer(highlightedRowSupplier));
            setDefaultRenderer(String.class, new PaddedCellRenderer(highlightedRowSupplier));
            setDefaultRenderer(Integer.class, new PaddedCellRenderer(highlightedRowSupplier));
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
//        });
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
        if (getModel() instanceof DefaultTableModel model) {
            model.setRowCount(0);
        }
        if (getModel() instanceof DataTableModel<?> model) {
            model.clear();
            model.setRowCount(0);
        }
    }

    // Custom Cell Editor with Padding
    private static class PaddedCellEditor extends DefaultCellEditor {
        public PaddedCellEditor() {
            super(new JTextField());
            ((JTextField) getComponent()).setBorder(new EmptyBorder(0, 5, 0, 5)); // Add padding to the editor
        }
    }

    // Method to check if a cell contains a String
//    public boolean isCellStringType(int row, int column) {
//        Object value = getValueAt(row, column);
//        return value instanceof String;
//    }

}