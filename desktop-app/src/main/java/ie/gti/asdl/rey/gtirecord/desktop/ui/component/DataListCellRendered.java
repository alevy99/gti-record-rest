package ie.gti.asdl.rey.gtirecord.desktop.ui.component;

import ie.gti.asdl.rey.gtirecord.model.annotation.DescriptionUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * @author Andrei Levchenko
 */
public class DataListCellRendered extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value != null) {
            setText(DescriptionUtil.getShortDescription(value)); // Используем кастомное строковое представление
        } else {
            setText("");
        }
        return this;
    }

}
