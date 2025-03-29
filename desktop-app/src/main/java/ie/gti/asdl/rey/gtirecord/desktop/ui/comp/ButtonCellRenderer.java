package ie.gti.asdl.rey.gtirecord.desktop.ui.comp;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

// ButtonRenderer: Custom cell renderer for displaying buttons
public class ButtonCellRenderer extends JButton  implements TableCellRenderer {

    private final JLabel label;

    public ButtonCellRenderer() {
        setOpaque(true); // Make sure button has a background
        label = new JLabel("");
//        setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {

        if ((value == null) || ((value instanceof String) && ((String) value).isEmpty())) {
            return label;
        } else {
            setText(value.toString());
            return this;
        }

//        if (value != null) {
//            setText(value.toString());
//            return this;
//        } else {
//            return label;
//        }

//        Object rowData = table.getValueAt(row, 0);

        // Проверяем условие (например, отключаем кнопку, если значение — "DISABLED")
//        setEnabled(rowData != null);
//        System.out.println("RENDERER");
//        String title;
//        if (value instanceof TableRowData) {
//            title = ((TableRowData) value).getText();
//        } else {
//            title = "";
//        }

//        setText(title);
//        setMargin(new Insets(5, 5, 5, 5));
//        setBorder(new EmptyBorder(5, 5, 5, 5));
//        return this;
    }



}
