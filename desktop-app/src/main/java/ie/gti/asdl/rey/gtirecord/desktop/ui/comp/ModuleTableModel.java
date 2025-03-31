package ie.gti.asdl.rey.gtirecord.desktop.ui.comp;

/**
 * @author Andrei Levchenko
 */
public class ModuleTableModel extends DataTableModel<Module>{

    private static Class[] types = new Class[]{
            java.lang.Integer.class, java.lang.String.class, java.lang.String.class
    };
    private static boolean[] canEdit = new boolean[]{
            false, true, true
    };

    public ModuleTableModel() {
        super(new Object[][]{
                        {null, null, null}
                },
                new String[]{
                        "ID", "Name", "Code"
                });
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return canEdit[columnIndex];
    }

    public Class getColumnClass(int columnIndex) {
        return types[columnIndex];
    }

}