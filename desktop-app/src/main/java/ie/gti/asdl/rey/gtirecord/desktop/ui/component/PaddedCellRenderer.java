package ie.gti.asdl.rey.gtirecord.desktop.ui.component;

import lombok.Setter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.Arrays;
import java.util.function.IntConsumer;
import java.util.function.Supplier;


@Setter
public class PaddedCellRenderer extends DefaultTableCellRenderer {

    private Supplier<Integer> highlightedRowSupplier;

    private static final int PADDING = 5;

    public PaddedCellRenderer() {
        super();
    }

    public PaddedCellRenderer(Supplier<Integer> highlightedRowSupplier) {
        this.highlightedRowSupplier = highlightedRowSupplier;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (c instanceof JLabel) {
            ((JLabel) c).setBorder(new EmptyBorder(PADDING, PADDING, PADDING, PADDING)); // Top, Left, Bottom, Right
        }

        // Check if the value is of type Number
        if (value instanceof Number) {
            setHorizontalAlignment(SwingConstants.RIGHT); // Align Long values to the right
        } else {
            setHorizontalAlignment(SwingConstants.LEFT); // Default alignment for other types
        }

        if ((table.getSelectedRowCount() > 1)
                && (highlightedRowSupplier != null)
                && (highlightedRowSupplier.get() != null)
                && (highlightedRowSupplier.get() == row)) {
            setBackground(GuiConsts.HIGHLIGHT_ROW_COLOR);
        } else if (isSelected) {
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