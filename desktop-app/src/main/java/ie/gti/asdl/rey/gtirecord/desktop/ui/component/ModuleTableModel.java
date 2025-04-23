package ie.gti.asdl.rey.gtirecord.desktop.ui.component;

/**
 * @author Andrei Levchenko
 */
public class ModuleTableModel extends DataTableModel<Module>{

    private boolean editable;

    private static final Class[] types = new Class[]{
            java.lang.Integer.class, java.lang.String.class, java.lang.String.class
    };
    private static final boolean[] canEdit = new boolean[]{
            false, true, true
    };

    public ModuleTableModel() {
        this(true);
    }

    public ModuleTableModel(boolean editable) {
        super(new Object[][]{

                },
                new String[]{
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