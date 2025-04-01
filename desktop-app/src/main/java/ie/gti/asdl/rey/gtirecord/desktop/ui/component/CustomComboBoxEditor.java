package ie.gti.asdl.rey.gtirecord.desktop.ui.component;

import ie.gti.asdl.rey.gtirecord.model.annotation.DescriptionUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * @author Andrei Levchenko
 */
public class CustomComboBoxEditor<T> implements ComboBoxEditor {
    private final JTextField editor;
    private T selectedItem;

    public CustomComboBoxEditor() {
        editor = new JTextField();
        editor.setBorder(null);
    }

    @Override
    public Component getEditorComponent() {
        return editor;
    }

    @Override
    public void setItem(Object item) {
//        if (item instanceof T) {
            selectedItem = (T) item;
            editor.setText(DescriptionUtil.getShortDescription(selectedItem)); // Устанавливаем правильное отображение
//        } else {
//            editor.setText("");
//        }
    }

    @Override
    public Object getItem() {
        return selectedItem;
    }

    @Override
    public void selectAll() {
        editor.selectAll();
    }

    @Override
    public void addActionListener(ActionListener l) {
        editor.addActionListener(l);
    }

    @Override
    public void removeActionListener(ActionListener l) {
        editor.removeActionListener(l);
    }
}
