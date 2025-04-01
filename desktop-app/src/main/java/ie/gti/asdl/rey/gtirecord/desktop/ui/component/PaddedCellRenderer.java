package ie.gti.asdl.rey.gtirecord.desktop.ui.component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.Arrays;
import java.util.function.IntConsumer;

public class PaddedCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (c instanceof JLabel) {
            ((JLabel) c).setBorder(new EmptyBorder(5, 5, 5, 5)); // Top, Left, Bottom, Right
        }

        // Check if the value is of type Number
        if (value instanceof Number) {
            setHorizontalAlignment(SwingConstants.RIGHT); // Align Long values to the right
        } else {
            setHorizontalAlignment(SwingConstants.LEFT); // Default alignment for other types
        }

        if (isSelected) {
            // Keep selection color
            setBackground(table.getSelectionBackground());

        } else {
            // Apply alternating row colors
            Color bg = (row % 2 == 0) ? GuiConsts.EVEN_ROW_COLOR : GuiConsts.ODD_ROW_COLOR;
            setBackground(bg);
        }

        return c;
    }
}