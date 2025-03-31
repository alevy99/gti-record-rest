package ie.gti.asdl.rey.gtirecord.desktop.ui;


import ie.gti.asdl.rey.gtirecord.desktop.ui.comp.DataTableModel;
import ie.gti.asdl.rey.gtirecord.desktop.ui.comp.PaddedJTable;
import ie.gti.asdl.rey.gtirecord.desktop.util.SwingUIUtils;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.util.*;

import static ie.gti.asdl.rey.gtirecord.desktop.util.SwingUIUtils.confirmBatchTableAction;

public abstract class AbstractTableDataFrame<T> extends AbstractFrame {

    private final Set<Integer> rowsInserting = new HashSet<>();

    private boolean isInserting = false;

    public AbstractTableDataFrame(FrameManager frameManager) {
        super(frameManager);
    }

    @Override
    protected void initForm() {
        super.initForm();
        getTable().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // Add selection listener
        getTable().getSelectionModel().addListSelectionListener(this::updateUI);

        SwingUIUtils.addTableFilter(getTable(), getTableFilterField());
    }

    protected abstract PaddedJTable getTable();

    protected abstract JButton getAddBtn();
    protected abstract JButton getDeleteBtn();
    protected abstract JButton getUpdateBtn();
    protected abstract JButton getAddCancelBtn();
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
    protected abstract void doDeleteData(int dataId);
    protected abstract boolean isDataValid(T data);

    protected abstract void fillDataObjectFromTable(T data, Integer row);
    protected abstract void addEmptyRowToModel();

    protected void onAddData() {
        DataTableModel<T> model = getTableModel();
        addEmptyRowToModel();
        int newRow = getTable().getRowCount() - 1;
        getTable().setRowSelectionInterval(newRow, newRow);
        startInserting();
    }

    protected void onCancelAddData() {
        stopInserting();
        // Delete last row
        ((DefaultTableModel) getTable().getModel()).removeRow(getTable().getRowCount() - 1);
    }

    protected void onAddSaveData() {
        List<String> errors = new ArrayList<>();
        rowsInserting.forEach(row -> {
            T newData = createDataInstance();
            fillDataObjectFromTable(newData, row);
            if (! isDataValid(newData)) {
                errors.add(newData.toString());
                return;
            }
            Optional<Integer> newDepId = doInsertData(newData);
            newDepId.ifPresent(id -> {
                getTable().setValueAt(id, row, 0);
            });
        });
        if (!errors.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Insert failed for:\n" + String.join("\n", errors), "Not valid data", JOptionPane.ERROR_MESSAGE);
        }
        stopInserting();
    }

    protected void onUpdateData() {
        if (isInserting) {
            return;
        }
        if (!confirmBatchTableAction(this, getTable(), getDataDescriptionColumn(), "Confirm update", "Are you sure want to update data:")) return;

        List<String> errors = new ArrayList<>();
        Arrays.stream(getTable().getSelectedRows()).forEach(row -> {
            T data = createDataInstance();
            fillDataObjectFromTable(data, row);
            if (! isDataValid(data)) {
                errors.add(data.toString());
                return;
            }
            doUpdateData(data);
        });
        if (!errors.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Update failed for:\n" + String.join("\n", errors), "Not valid data", JOptionPane.ERROR_MESSAGE);
        }
    }

    protected void onDeleteData() {
        if (!confirmBatchTableAction(this, getTable(), getDataDescriptionColumn(),
                "Confirm delete", "Are you sure want to delete data:")) {
            return;
        }

        Arrays.stream(getTable().getSelectedRows()).forEach(row -> {
            doDeleteData((Integer) getTable().getModel().getValueAt(row, getDataIDColumn()));
        });
        reloadTableData();
    }

    private void startInserting() {
        rowsInserting.add(getTable().getRowCount() - 1);
        isInserting = true;
        // disable all the other buttons
//        getAddBtn().setEnabled(false);
        getAddCancelBtn().setEnabled(true);
        getAddSaveBtn().setEnabled(true);

        getUpdateBtn().setEnabled(false);
        getDeleteBtn().setEnabled(false);
//        setTableSelection(false);
    }

    private void stopInserting() {
        isInserting = false;
        // enable all the buttons etc
//        getAddBtn().setEnabled(true);
        getAddCancelBtn().setEnabled(false);
        getAddSaveBtn().setEnabled(false);

        getUpdateBtn().setEnabled(true);
        getDeleteBtn().setEnabled(true);

        rowsInserting.clear();
//        setTableSelection(true);
    }

    protected DataTableModel<T> getTableModel() {
        return (DataTableModel<T>) getTable().getModel();
    }

    @Override
    protected void onFormShown() {
        super.onFormShown();
        reloadTableData();
        updateUI(null);
    }

    protected void reloadTableData() {
        stopInserting();
//        DataTableModel<T> model = getTableModel();
//        // Clear table
//        model.setRowCount(0);
        getTable().clear();

        doReloadData();
        updateUI(null);
//        List<T> departments = departmentService.getAll();
//
//        departments.forEach(department -> {
//            model.addRow(department, new Object[]{department.getId(), department.getName()});
//        });
    }

    protected void updateUI(ListSelectionEvent listSelectionEvent) {
        getUpdateBtn().setEnabled(getTable().getSelectedRowCount() > 0);
        getDeleteBtn().setEnabled(getTable().getSelectedRowCount() > 0);
    }

}
