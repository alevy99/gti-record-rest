package ie.gti.asdl.rey.gtirecord.desktop.ui.comp;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

// ButtonRenderer: Custom cell renderer for displaying buttons
public class ButtonCellRenderer extends JButton  implements TableCellRenderer {

    public ButtonCellRenderer() {
        setOpaque(true); // Make sure button has a background
//        setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
        setText((value == null) ? "" : value.toString());
//        setMargin(new Insets(5, 5, 5, 5));
//        setBorder(new EmptyBorder(5, 5, 5, 5));
        return this;
    }
}
