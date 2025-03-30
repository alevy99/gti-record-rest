package ie.gti.asdl.rey.gtirecord.desktop.ui.comp;

import ie.gti.asdl.rey.gtirecord.model.entity.CourseType;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class DataCellRenderer<T extends Nameable> extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//        if (value instanceof T) {
//            setText(((T) value).getName()); // Show only department name
//        } else {
//            setText("");
//        }
        return this;
    }
    
}
