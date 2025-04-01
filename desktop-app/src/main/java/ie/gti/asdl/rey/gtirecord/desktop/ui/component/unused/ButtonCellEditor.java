package ie.gti.asdl.rey.gtirecord.desktop.ui.component.unused;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;

// ButtonEditor: Custom cell editor for handling button clicks
public class ButtonCellEditor<T> extends DefaultCellEditor implements TableCellEditor {
    private final JButton button;
//    private boolean isPushed;
//    private TableRowData<T> rowData;

//    private JTable table;

    private final JLabel label;

//    private String title;
    private Integer currentRow;

//    private ActionListener actionListener;

    public ButtonCellEditor(ActionPerformer<Integer> actionPerformer) {
        super(new JTextField());
//        super(new JCheckBox());
        label = new JLabel("");
        button = new JButton();
        button.setOpaque(true);
        button.addActionListener(e -> {
//            System.out.println("CLICKED");
//            if (table != null) {
//                Object rowData = table.getValueAt(currentRow, 0);
//
//                if (rowData == null) {
//                    button.setEnabled(false);
//                    fireEditingStopped();
//                    SwingUtilities.invokeLater(() -> ((AbstractTableModel) table.getModel()).fireTableCellUpdated(currentRow, 6));
//                    return;
//                }
//            }

            // Handle button click action here
            actionPerformer.actionPerformed(e, currentRow);
//            button.setEnabled(true);
//            fireEditingStopped();
//            table.repaint();
            // Полностью перерисовываем таблицу через invokeLater()
//            SwingUtilities.invokeLater(() -> {
//                ((AbstractTableModel) table.getModel()).fireTableCellUpdated(currentRow, 6);
////                button.setVisible(true); // Возвращаем видимость кнопки после обновления
//            });
             // Обновляем таблицу, чтобы сохранить состояние кнопки
        });
//        editorComponent = button;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
                                                 int row, int column) {
//        if (value instanceof TableRowData) {
//            rowData = (TableRowData) value;
//        } else {
//            rowData = new TableRowData<T>(row, (T) value);
//        }
//        this.table = table;

//        String title;
//        if (value instanceof TableRowData) {
//            rowData = (TableRowData<T>) value;
//            rowData.setRow(row);
//            title = rowData.getText();
//        } else {
//            title = "";
//        }

        currentRow = row;

        if ((value == null) || ((value instanceof String) && ((String) value).isEmpty())) {
            return label;
        } else {
            button.setText(value.toString());
            return button;
        }

//        if (value != null) {
////            title = value.toString();
//            button.setText(value.toString());
////            editorComponent = button;
//            return button;
//        } else {
////            editorComponent = label;
//            return label;
//        }

//        isPushed = true;
//        return button;
    }

    @Override
    public Object getCellEditorValue() {
//        if (isPushed) {
//            // Perform the action (e.g., print message)
//            System.out.println("Button clicked in row: " + label);
//        }
//        isPushed = false;
        return button.getText();
    }


//    @Override
//    public boolean stopCellEditing() {
////        isPushed = false;
//        return super.stopCellEditing();
//    }
}
