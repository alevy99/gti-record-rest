package ie.gti.asdl.rey.gtirecord.desktop.ui.component;

/**
 * @author Andrei Levchenko
 */

import lombok.Setter;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.function.Supplier;

// Custom Renderer for Boolean Cells (CheckBoxes)
@Setter
public class BooleanCellRenderer extends JCheckBox implements TableCellRenderer {

    private Supplier<Integer> highlightedRowSupplier;

    public BooleanCellRenderer(Supplier<Integer> highlightedRowSupplier) {
        super();
        this.highlightedRowSupplier = highlightedRowSupplier;
        setHorizontalAlignment(CENTER); // Center the checkbox
        setOpaque(true); // Allow background color changes
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value instanceof Boolean) {
            setSelected((Boolean) value);
        }

        // Apply alternating row colors
        if ((table.getSelectedRowCount() > 1)
                && (highlightedRowSupplier != null)
                && (highlightedRowSupplier.get() != null)
                && (highlightedRowSupplier.get() == row)) {
            setBackground(GuiConsts.HIGHLIGHT_ROW_COLOR);
        } else if (isSelected) {
            setBackground(table.getSelectionBackground()); // Keep selection color
        } else {
            Color bg = (row % 2 == 0) ? GuiConsts.EVEN_ROW_COLOR : GuiConsts.ODD_ROW_COLOR; // Match Nimbus striping
            setBackground(bg);
        }

        return this;
    }

}
