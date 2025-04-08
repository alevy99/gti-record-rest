package ie.gti.asdl.rey.gtirecord.desktop.ui.component;

import ie.gti.asdl.rey.gtirecord.model.util.Pair;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.function.Function;

/**
 * @author Andrei Levchenko
 */
public class DynamicComboBoxEditor<R> extends AbstractCellEditor implements TableCellEditor {
    private final JComboBox<R> comboBox;
    private final Function<Integer, List<R>> itemProvider;
    private RowAwareActionListener rowAwareActionListener;
    private int currentRow = -1;

    public DynamicComboBoxEditor(Function<Integer, List<R>> itemProvider) {
        this.comboBox = new JComboBox<>();
        this.itemProvider = itemProvider;
        comboBox.setRenderer(new DataListCellRenderer());
        comboBox.addActionListener(e -> {
            if (rowAwareActionListener != null && currentRow >= 0) {
                rowAwareActionListener.actionPerformed(e, currentRow);
            }
            stopCellEditing(); // по желанию
        });
    }

    public void setRowAwareActionListener(RowAwareActionListener listener) {
        this.rowAwareActionListener = listener;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {

        currentRow = row;

        comboBox.removeAllItems();

        List<R> items = itemProvider.apply(row);

        for (R item : items) {
            comboBox.addItem(item);
        }

        comboBox.setSelectedItem(value);
        return comboBox;
    }

    @Override
    public Object getCellEditorValue() {
        return comboBox.getSelectedItem();
    }
}