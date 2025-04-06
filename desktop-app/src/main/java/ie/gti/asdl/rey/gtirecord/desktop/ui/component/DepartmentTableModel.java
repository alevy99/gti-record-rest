package ie.gti.asdl.rey.gtirecord.desktop.ui.component;

import ie.gti.asdl.rey.gtirecord.model.entity.Department;

/**
 * @author Andrei Levchenko
 */
public class DepartmentTableModel extends DataTableModel<Department>{

    private boolean editable;

    private Class[] types = new Class[]{
            Integer.class, String.class
    };
    private boolean[] canEdit = new boolean[]{
            false, true
    };

    public DepartmentTableModel(boolean editable) {
        super(new Object[][]{
                },
                new String[]{
                        "ID", "Name"
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