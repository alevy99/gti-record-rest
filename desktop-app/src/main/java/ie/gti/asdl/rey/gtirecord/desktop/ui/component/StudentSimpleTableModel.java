package ie.gti.asdl.rey.gtirecord.desktop.ui.component;

import ie.gti.asdl.rey.gtirecord.model.entity.Student;

/**
 * @author Andrei Levchenko
 */
public class StudentSimpleTableModel  extends DataTableModel<Student>{

    private static Class[] types = new Class[]{
            java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
    };

    public StudentSimpleTableModel() {
        super(new Object[][]{

                },
                new String [] {
                        "ID", "First name", "Last name", "Group"
                });
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public Class getColumnClass(int columnIndex) {
        return types[columnIndex];
    }

}