package ie.gti.asdl.rey.gtirecord.desktop.ui.component;

import java.awt.event.ActionEvent;

/**
 * @author Andrei Levchenko
 */
@FunctionalInterface
public interface RowAwareActionListener {
    void actionPerformed(ActionEvent e, int row);
}