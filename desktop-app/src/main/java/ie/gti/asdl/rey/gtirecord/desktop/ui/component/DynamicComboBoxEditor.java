package ie.gti.asdl.rey.gtirecord.desktop.ui.component;

import lombok.Setter;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.util.List;
import java.util.function.Function;

/**
 * @author Andrei Levchenko
 */
public class DynamicComboBoxEditor<R> extends AbstractCellEditor implements TableCellEditor {
    private final JComboBox<R> comboBox;
    private final Function<Integer, List<R>> itemProvider;
    @Setter
    private RowAwareActionListener rowAwareActionListener;
    private int currentRow = -1;
    private boolean doNotTriggerAction = false;

    public DynamicComboBoxEditor(Function<Integer, List<R>> itemProvider) {
        this.comboBox = new JComboBox<>();
        this.itemProvider = itemProvider;
        comboBox.setRenderer(new DataListCellRenderer());
        comboBox.addActionListener(e -> {
            if (! doNotTriggerAction && rowAwareActionListener != null && currentRow >= 0) {
                rowAwareActionListener.actionPerformed(e, currentRow);
            }
            stopCellEditing(); // по желанию
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {

        // Do not trigger while editing combobox items
        doNotTriggerAction = true;
        currentRow = row;

        comboBox.removeAllItems();

        itemProvider.apply(row).forEach(comboBox::addItem);

        comboBox.setSelectedItem(value);
        doNotTriggerAction = false;
        return comboBox;
    }

    @Override
    public Object getCellEditorValue() {
        return comboBox.getSelectedItem();
    }
}