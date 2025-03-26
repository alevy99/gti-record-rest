package ie.gti.asdl.rey.gtirecord.desktop.ui.comp;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// ButtonEditor: Custom cell editor for handling button clicks
public class ButtonCellEditor<T> extends AbstractCellEditor implements TableCellEditor {
    private final JButton button;
//    private boolean isPushed;
    private TableRowData<T> rowData;

//    private ActionListener actionListener;

    public ButtonCellEditor(ActionPerformer<TableRowData<T>> actionPerformer) {
//        super(new JCheckBox());
        button = new JButton();
        button.setOpaque(true);
        button.addActionListener(e -> {
            fireEditingStopped();
            // Handle button click action here
            actionPerformer.actionPerformed(e, rowData);
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
                                                 int row, int column) {
//        if (value instanceof TableRowData) {
//            rowData = (TableRowData) value;
//        } else {
//            rowData = new TableRowData<T>(row, (T) value);
//        }

        String title;
        if (value instanceof TableRowData) {
            rowData = (TableRowData<T>) value;
            rowData.setRow(row);
            title = rowData.getText();
        } else {
            title = "";
        }

        button.setText(title);
//        isPushed = true;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
//        if (isPushed) {
//            // Perform the action (e.g., print message)
//            System.out.println("Button clicked in row: " + label);
//        }
//        isPushed = false;
        return rowData;
    }

    @Override
    public boolean stopCellEditing() {
//        isPushed = false;
        return super.stopCellEditing();
    }
}
