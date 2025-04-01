package ie.gti.asdl.rey.gtirecord.desktop.ui.component;

import ie.gti.asdl.rey.gtirecord.model.annotation.DescriptionUtil;

import javax.swing.*;
import java.awt.*;
import java.util.function.Supplier;

public class PaddedDataCellRenderer extends PaddedCellRenderer {

    public PaddedDataCellRenderer() {
        this(null);
    }

    public PaddedDataCellRenderer(Supplier<Integer> highlightedRowSupplier) {
        super(highlightedRowSupplier);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (c instanceof JLabel) {
            if (value != null) {
                ((JLabel) c).setText(DescriptionUtil.getShortDescription(value));
            } else {
                ((JLabel) c).setText("");
            }
        }
        return this;
    }

}
