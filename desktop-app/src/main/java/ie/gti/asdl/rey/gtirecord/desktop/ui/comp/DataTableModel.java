package ie.gti.asdl.rey.gtirecord.desktop.ui.comp;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

public class DataTableModel<T> extends DefaultTableModel {

    private final List<T> dataList = new ArrayList<>();

    public DataTableModel(Object[][] data, Object[] columnNames) {
        super(data, columnNames);
    }

    public void addRow(T data, Object[] rowData) {
        super.addRow(rowData);
        this.dataList.add(data);
    }

    public T getData(int index) {
        return dataList.get(index);
    }

//    public void setData(T data) {
//        this.data = data;
//    }
}
