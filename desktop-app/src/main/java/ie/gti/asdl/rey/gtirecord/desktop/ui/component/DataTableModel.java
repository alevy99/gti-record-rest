package ie.gti.asdl.rey.gtirecord.desktop.ui.component;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @NotNull
    public T getData(@NotNull Integer row) {
        return dataList.get(row);
    }

    public Optional<Integer> getDataRow(T data) {
        if (dataList.contains(data)) {
            return Optional.of(dataList.indexOf(data));
        } else {
            return Optional.empty();
        }

    }

    void clear() {
        dataList.clear();
    }


//    public void setData(T data) {
//        this.data = data;
//    }
}
