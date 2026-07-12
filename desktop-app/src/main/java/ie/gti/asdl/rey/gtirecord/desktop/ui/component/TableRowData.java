package ie.gti.asdl.rey.gtirecord.desktop.ui.component;

public abstract class TableRowData<T> {
    private int row;
    private T data;
//    private final String actionName; // Additional fuields could be added

    public TableRowData(T data) {
        this.data = data;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getRow() {
        return row;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public abstract String getText();

}