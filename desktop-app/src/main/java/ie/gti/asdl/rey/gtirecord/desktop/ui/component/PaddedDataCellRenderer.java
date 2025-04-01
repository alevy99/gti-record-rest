package ie.gti.asdl.rey.gtirecord.desktop.ui.component;

import javax.swing.*;
import java.awt.*;

public class PaddedDataCellRenderer extends PaddedCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (c instanceof JLabel) {
            if (value != null) {
                ((JLabel) c).setText(value.toString()); // Show only department name
            } else {
                ((JLabel) c).setText("");
            }
        }
        return this;
    }

}
