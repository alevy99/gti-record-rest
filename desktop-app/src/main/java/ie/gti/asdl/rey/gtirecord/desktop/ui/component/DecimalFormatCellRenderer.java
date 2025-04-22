package ie.gti.asdl.rey.gtirecord.desktop.ui.component;

import java.text.DecimalFormat;
import java.util.function.Supplier;

/**
 * @author Andrei Levchenko
 */
public class DecimalFormatCellRenderer extends PaddedCellRenderer {

    private final DecimalFormat formatter = new DecimalFormat("#0.00");

    public DecimalFormatCellRenderer() {
        super();
    }

    public DecimalFormatCellRenderer(Supplier<Integer> highlightedRowSupplier) {
        super(highlightedRowSupplier);
    }

    @Override
    protected void setValue(Object value) {
        if (value instanceof Number) {
            value = formatter.format(value);
        }
        super.setValue(value);
    }
}