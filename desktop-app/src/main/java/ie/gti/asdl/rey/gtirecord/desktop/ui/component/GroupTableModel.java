package ie.gti.asdl.rey.gtirecord.desktop.ui.component;

import ie.gti.asdl.rey.gtirecord.model.entity.Group;

/**
 * @author Andrei Levchenko
 */
public class GroupTableModel extends DataTableModel<Group>{

    private boolean editable;

    private Class[] types = new Class [] {
            java.lang.Integer.class, java.lang.String.class, java.lang.Object.class
    };
    private boolean[] canEdit = new boolean [] {
            false, true, true
    };

    public GroupTableModel(boolean editable) {
        super(new Object [][] {
                },
                new String [] {
                        "ID", "Name", "Code"
                });
        this.editable = editable;
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return editable && canEdit[columnIndex];
    }

    public Class getColumnClass(int columnIndex) {
        return types[columnIndex];
    }

}