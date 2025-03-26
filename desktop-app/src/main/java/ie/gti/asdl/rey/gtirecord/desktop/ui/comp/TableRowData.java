package ie.gti.asdl.rey.gtirecord.desktop.ui.comp;

public class TableRowData {
    private final int userId;
//    private final String actionName; // Можно добавить дополнительные данные

    public TableRowData(int rowIndex) {
        this.userId = rowIndex;
//        this.actionName = actionName;
    }

    public int getUserId() {
        return userId;
    }

//    public String getActionName() {
//        return actionName;
//    }
//
//    @Override
//    public String toString() {
//        return actionName; // Нужно для отображения в таблице
//    }
}