package ie.gti.asdl.rey.gtirecord.desktop.ui.component;

import lombok.Getter;
import lombok.Setter;

import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DataTableModel<T> extends DefaultTableModel {

    @Getter
    private final List<T> dataList = new ArrayList<>();

    public DataTableModel(Object[][] data, Object[] columnNames) {
        super(data, columnNames);
    }

    public void addRow(T data, Object[] rowData) {
        super.addRow(rowData);
        dataList.add(data);
    }

    public void removeRow(int row) {
        super.removeRow(row);
        dataList.remove(row);
    }

    public T getData(int row) {
        return dataList.get(row);
    }

    void clear() {
        dataList.clear();
    }


//    public void setData(T data) {
//        this.data = data;
//    }
}
