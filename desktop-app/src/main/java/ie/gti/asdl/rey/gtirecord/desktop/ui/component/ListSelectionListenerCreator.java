package ie.gti.asdl.rey.gtirecord.desktop.ui.component;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author Andrei Levchenko
 */
public class ListSelectionListenerCreator {

    private final Supplier<Boolean> suppressSelectionEventsSupplier;

    public ListSelectionListenerCreator(Supplier<Boolean> suppressSelectionEventsSupplier) {
        this.suppressSelectionEventsSupplier = suppressSelectionEventsSupplier;
    }

    public ListSelectionListener createSafeListener(Consumer<ListSelectionEvent> handler) {
        return e -> {
            if (!suppressSelectionEventsSupplier.get() && !e.getValueIsAdjusting()) {
                handler.accept(e);
            }
        };
    }

}
