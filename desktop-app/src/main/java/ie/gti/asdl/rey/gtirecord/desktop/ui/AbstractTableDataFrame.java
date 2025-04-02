package ie.gti.asdl.rey.gtirecord.desktop.ui;


import ie.gti.asdl.rey.gtirecord.desktop.ui.component.DataTableModel;
import ie.gti.asdl.rey.gtirecord.desktop.ui.component.PaddedJTable;
import ie.gti.asdl.rey.gtirecord.desktop.util.SwingUIUtils;
import ie.gti.asdl.rey.gtirecord.model.annotation.KeyUtil;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

import static ie.gti.asdl.rey.gtirecord.desktop.util.SwingUIUtils.confirmBatchTableAction;
import static ie.gti.asdl.rey.gtirecord.desktop.util.SwingUIUtils.createSafeListener;

public abstract class AbstractTableDataFrame<T> extends AbstractFrame {

    public AbstractTableDataFrame(FrameManager frameManager) {
        super(frameManager);
    }

    @Override
    protected void initForm() {
        super.initForm();
        getTable().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // Add selection listener
        getTable().getSelectionModel().addListSelectionListener(createSafeListener(event -> updateUI()));

        SwingUIUtils.addTableFilter(getTable(), getTableFilterField());
    }

    protected abstract PaddedJTable getTable();

    protected abstract JButton getDeleteBtn();
    protected abstract JButton getAddSaveBtn();
    protected abstract JTextField getTableFilterField();
    protected abstract int getDataDescriptionColumn();

    protected int getDataIDColumn() {
        return 0;
    }

    protected abstract T createDataInstance();
    protected abstract void doReloadData();
    protected abstract Optional<Integer> doInsertData(T data);
    protected abstract void doUpdateData(T data);
    protected abstract void doDeleteData(Integer dataId);
    protected abstract boolean isDataValid(T data);

    protected abstract void fillDataObjectFromTable(T data, Integer row);
    protected abstract void addEmptyRowToModel();

    protected void onAddData() {
        addEmptyRowToModel();
        int newRow = getTable().getRowCount() - 1;
        getTable().setRowSelectionInterval(newRow, newRow);
        // Scroll to the new row
        Rectangle rect = getTable().getCellRect(newRow, 0, true);
        getTable().scrollRectToVisible(rect);
    }

    protected void onAddSaveData() {
        if (!confirmBatchTableAction(this, getTable(), getDataDescriptionColumn(), "Confirm save", "Are you sure want to save data:")) return;
        List<String> errors = new ArrayList<>();

        Arrays.stream(getTable().getSelectedRows()).forEach(row -> {
            T data = getTableModel().getData(getTable().convertRowIndexToModel(row));

            fillDataObjectFromTable(data, row);

            if (! isDataValid(data)) {
                errors.add(data.toString());
                return;
            }

            if (KeyUtil.hasKey(data)) {
                doUpdateData(data);
            } else {
                doInsertData(data).ifPresentOrElse((newId -> {
                    KeyUtil.setKey(data, newId);
                    getTable().setValueAt(newId, row, 0);
                }), () -> errors.add(data.toString()));
            }
        });

        if (!errors.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Insert failed for:\n" + String.join("\n", errors), "Not valid data", JOptionPane.ERROR_MESSAGE);
        }
        updateUI();
    }

    protected void onDeleteData() {
        if (!confirmBatchTableAction(this, getTable(), getDataDescriptionColumn(),
                "Confirm delete", "Are you sure want to delete data:")) {
            return;
        }

        List<Integer> modelRows = new ArrayList<>();

        Arrays.stream(getTable().getSelectedRows()).forEach(row -> {
            T data = getTableModel().getData(getTable().convertRowIndexToModel(row));

            // data has key -> call delete service
            if (KeyUtil.hasKey(data)) {
                doDeleteData(KeyUtil.getKey(data));
                modelRows.add(getTable().convertRowIndexToModel(row));
            } else {
                // No key - just delete the corresponding row
                modelRows.add(getTable().convertRowIndexToModel(row));
            }
        });
        // Delete from the model in reverse order
        modelRows.sort(Comparator.reverseOrder());
        modelRows.forEach(modelRow -> getTableModel().removeRow(modelRow));

        updateUI();
//        reloadTableData();
    }

    protected DataTableModel<T> getTableModel() {
        return (DataTableModel<T>) getTable().getModel();
    }

    @Override
    protected void onFormShown() {
        super.onFormShown();
        reloadTableData();
        updateUI();
    }

    protected void reloadTableData() {
        getTable().clear();
        doReloadData();
        updateUI();
    }

    protected void updateUI() {
        getAddSaveBtn().setEnabled(getTable().getSelectedRowCount() > 0);
        getDeleteBtn().setEnabled(getTable().getSelectedRowCount() > 0);
    }

}
