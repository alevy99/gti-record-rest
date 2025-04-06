package ie.gti.asdl.rey.gtirecord.desktop.ui.component;

import lombok.Setter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.function.Supplier;

import static ie.gti.asdl.rey.gtirecord.desktop.ui.component.GuiConsts.*;


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

        if (!table.isEnabled()) {
            c.setForeground(Color.GRAY);
            Color bg = (row % 2 == 0) ? DISABLED_EVEN_ROW_COLOR : DISABLED_ODD_ROW_COLOR;
            setBackground(bg);
        } else
            if ((table.getSelectedRowCount() > 1)
                && (highlightedRowSupplier != null)
                && (highlightedRowSupplier.get() != null)
                && (highlightedRowSupplier.get() == row)) {
            setBackground(GuiConsts.HIGHLIGHT_ROW_COLOR);
        } else if (isSelected) {
            // Keep selection color
            setBackground(table.getSelectionBackground());
        } else {
            // Apply alternating row color
            c.setForeground(Color.BLACK);
            Color bg = (row % 2 == 0) ? EVEN_ROW_COLOR : ODD_ROW_COLOR;
            setBackground(bg);
        }

        return c;
    }
}