package ie.gti.asdl.rey.gtirecord.desktop.ui.component.unused;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public interface ActionPerformer<T> extends ActionListener {

    void actionPerformed(ActionEvent e, T data);

    default void actionPerformed(ActionEvent e) {
        actionPerformed(e, null);
    }

}
