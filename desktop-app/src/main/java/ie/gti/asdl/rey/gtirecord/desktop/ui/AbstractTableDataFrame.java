package ie.gti.asdl.rey.gtirecord.desktop.ui;


import ie.gti.asdl.rey.gtirecord.desktop.ui.component.DataTableModel;
import ie.gti.asdl.rey.gtirecord.desktop.ui.component.PaddedJTable;
import ie.gti.asdl.rey.gtirecord.desktop.util.SwingUIUtils;
import ie.gti.asdl.rey.gtirecord.model.annotation.DescriptionUtil;
import ie.gti.asdl.rey.gtirecord.model.annotation.KeyUtil;
import ie.gti.asdl.rey.gtirecord.model.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.IntConsumer;

import static ie.gti.asdl.rey.gtirecord.desktop.util.SwingUIUtils.confirmBatchTableAction;
import static ie.gti.asdl.rey.gtirecord.desktop.util.SwingUIUtils.createSafeListSelectionListener;

public abstract class AbstractTableDataFrame<T> extends AbstractFrame {

    private final Logger logger = LoggerFactory.getLogger(AbstractTableDataFrame.class);

    public AbstractTableDataFrame(FrameManager frameManager) {
        super(frameManager);
    }

    @Override
    protected void initFrame() {
        super.initFrame();
        getTable().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // Add selection listener
        getTable().getSelectionModel().addListSelectionListener(createSafeListSelectionListener(event -> updateUI()));

        SwingUIUtils.addTableFilter(getTable(), getTableFilterField(), getOnRowSelect());
    }

    protected abstract PaddedJTable getTable();

    protected abstract JButton getDeleteBtn();
    protected abstract JButton getSaveBtn();
    protected abstract JTextField getTableFilterField();
    protected abstract int getDataDescriptionColumn();

    protected int getDataIDColumn() {
        return 0;
    }

    protected IntConsumer getOnRowSelect() {
        return (val) -> {};
    }

    protected abstract T createDataInstance();
    protected abstract void doReloadData();
    protected abstract Optional<Integer> doInsertData(T data);
    protected abstract void doUpdateData(T data);
    protected abstract void doDeleteData(T data);
    protected abstract boolean isDataValid(T data);

    protected abstract void fillDataObjectFromTable(T data, Integer row);
    protected abstract void addEmptyRowToModel();

    protected void onAddLine() {
        addEmptyRowToModel();
        int newRow = getTable().getRowCount() - 1;
        getTable().setRowSelectionInterval(newRow, newRow);
        // Scroll to the new row
        Rectangle rect = getTable().getCellRect(newRow, 0, true);
        getTable().scrollRectToVisible(rect);
    }

    protected void onSaveData() {
        if (!confirmBatchTableAction(this, getTable(), getDataDescriptionColumn(), "Confirm save", "Are you sure want to save data:")) return;
        List<String> errors = new ArrayList<>();

        Arrays.stream(getTable().getSelectedRows()).forEach(row -> {
            T data = getTableModel().getData(getTable().convertRowIndexToModel(row));

            fillDataObjectFromTable(data, row);

            if (! isDataValid(data)) {
                errors.add(DescriptionUtil.getShortDescription(data));
                return;
            }

            boolean setKeyNeeded = (data instanceof Pair<?,?>);
            Object keyData;
            if (setKeyNeeded) {
                keyData = ((Pair<?,?>) data).getValue1(); // Assume we have keys only in the first entity
            } else {
                keyData = data;
            }

            try {
                if (KeyUtil.hasKey(keyData)) {
                    doUpdateData(data);
                } else {
                    doInsertData(data).ifPresentOrElse((newId -> {
                        if (setKeyNeeded) {
                            KeyUtil.setKey(keyData, newId);
                        }
                        getTable().setValueAt(newId, row, 0);
                    }), () -> errors.add(DescriptionUtil.getShortDescription(data)));
                }
            } catch (Exception e) {
                errors.add(DescriptionUtil.getShortDescription(data));
                logger.error("Failed to save data: {}", data, e);
            }
        });

        if (!errors.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Save failed for:\n" + String.join("\n", errors), "Not valid data", JOptionPane.ERROR_MESSAGE);
        }
        onSaveDataCompleted();
        updateUI();
    }

    protected void onSaveDataCompleted() {
        // Do nothing by default
        // Override if needed
    }

    protected void onDeleteData() {
        List<Integer> modelRows = new ArrayList<>();
        List<T> dataList = new ArrayList<>();

        Arrays.stream(getTable().getSelectedRows())
                .map(row -> getTable().convertRowIndexToModel(row)).forEach(modelRow -> {
            T data = getTableModel().getData(modelRow);

            boolean setKeyNeeded = (data instanceof Pair<?,?>);
            Object keyData;
            if (data instanceof Pair<?,?> pair) {
                keyData = pair.getValue1(); // Assume we have keys only in the first entity
            } else {
                keyData = data;
            }

            // data has key -> call delete service
            if (KeyUtil.hasKey(keyData)) {
                dataList.add(data);
                modelRows.add(modelRow);
            } else {
                // No key - just delete the corresponding row
                modelRows.add(modelRow);
            }
        });

        if (!dataList.isEmpty() && !confirmBatchTableAction(this, getTable(), getDataDescriptionColumn(),
                "Confirm delete", "Are you sure want to delete data:")) {
            return;
        }

//        dataList.stream()
//                .map(data ->  {
//                    Object keyData = data;
//                    // data could be Pair with a key as value1
//                    if (data instanceof Pair<?,?> pair) {
//                        keyData = pair.getValue1(); // Assume we have keys only in the first entity
//                    }
//                    return KeyUtil.getKey(keyData);
//                })
//                .forEach(this::doDeleteData);

        dataList
//                .map(data ->  {
////                    Object keyData = data;
//                    // data could be Pair with a key as value1
//                    if (data instanceof Pair<?,?> pair) {
//                        data = pair.getValue1(); // Assume we have keys only in the first entity
//                    }
//                    return data;
//                })
                .forEach(this::doDeleteData);

        // Delete from the model in reverse order
        modelRows.sort(Comparator.reverseOrder());
        modelRows.forEach(modelRow -> getTableModel().removeRow(modelRow));

        updateUI();
    }

    protected DataTableModel<T> getTableModel() {
        return (DataTableModel<T>) getTable().getModel();
    }

    @Override
    protected void onFrameShown() {
        super.onFrameShown();
        reloadTableData();
        updateUI();
    }

    protected void reloadTableData() {
        getTable().clear();
        doReloadData();
        updateUI();
    }

    protected void updateUI() {
        getSaveBtn().setEnabled(getTable().getSelectedRowCount() > 0);
        getDeleteBtn().setEnabled(getTable().getSelectedRowCount() > 0);
    }

    protected String getTableStringValueAt(Integer row, int column) {
        if (getTable().getValueAt(row, column) == null) return "";
        return getTable().getValueAt(row, column).toString();
    }

}
