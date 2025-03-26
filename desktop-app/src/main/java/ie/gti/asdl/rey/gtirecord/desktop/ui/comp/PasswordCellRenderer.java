package ie.gti.asdl.rey.gtirecord.desktop.ui.comp;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class PasswordCellRenderer extends DefaultTableCellRenderer {

    @Override
    protected void setValue(Object value) {
        if (value != null) {
            setText("••••••"); // Always show asterisks
        } else {
            setText(""); // Empty if null
        }
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (!isSelected) { // Keep selection color when row is selected
            if (row % 2 == 0) {
                c.setBackground(GuiConsts.EVEN_ROW_COLOR);
            } else {
                c.setBackground(GuiConsts.ODD_ROW_COLOR);
            }
        }

        return c;
    }
}