package ie.gti.asdl.rey.gtirecord.desktop.ui;


import ie.gti.asdl.rey.gtirecord.desktop.ui.comp.DataTableModel;
import ie.gti.asdl.rey.gtirecord.desktop.ui.comp.PaddedJTable;
import ie.gti.asdl.rey.gtirecord.model.entity.Department;
import ie.gti.asdl.rey.gtirecord.model.entity.Module;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.util.*;
import java.util.stream.Collectors;

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
    }

    protected abstract PaddedJTable getTable();

    protected abstract JButton getAddBtn();
    protected abstract JButton getDeleteBtn();
    protected abstract JButton getUpdateBtn();
    protected abstract JButton getAddCancelBtn();
    protected abstract JButton getAddSaveBtn();
    protected abstract int getDataDescriptionColumn();

    protected int getDataIDColumn() {
        return 0;
    }

    protected abstract T createDataInstance();
    protected abstract void doReloadData();
    protected abstract Optional<Integer> doInsertData(T data);
    protected abstract void doUpdateData(T data);
    protected abstract void doDeleteData(int dataId);

    protected abstract void fillDataObjectFromTable(T data, Integer row);

    protected void onAddData() {
        DataTableModel<T> model = getTableModel();

        model.addRow(createDataInstance(), new Object[]{null, "", "", false, false, false});
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
        rowsInserting.forEach(row -> {
            T newData = createDataInstance();
            fillDataObjectFromTable(newData, row);
//            newDepartment.setName(getTable().getValueAt(row, 1).toString());

//            Optional<Integer> newDepId = departmentService.insert(newDepartment);
            Optional<Integer> newDepId = doInsertData(newData);
            newDepId.ifPresent(id -> {
                getTable().setValueAt(id, row, 0);
            });
        });
        stopInserting();
    }

    protected void onUpdateData() {
        if (!confirmBatchTableAction("Confirm update", "Are you sure want to update departments:")) return;

        if (! isInserting) {
            Arrays.stream(getTable().getSelectedRows()).forEach(row -> {
                T data = createDataInstance();
                fillDataObjectFromTable(data, row);
//                Department department = new Department();
//                department.setId((Integer) getTable().getValueAt(row, 0));
//                department.setName(getTable().getValueAt(row, 1).toString());
                doUpdateData(data);
//                departmentService.update(department);
            });
        }
    }

    protected void onDeleteData() {
        if (!confirmBatchTableAction("Confirm delete", "Are you sure want to delete users:")) return;

        Arrays.stream(getTable().getSelectedRows()).forEach(row -> {
            doDeleteData((Integer) getTable().getModel().getValueAt(row, getDataIDColumn()));
//            departmentService.delete();
            //            ((DefaultTableModel) tblDepartment.getModel()).removeRow(row);
            //            ids.add((Long) tblDepartment.getModel().getValueAt(row, 0));
        });
        //        userDao.deleteUsersById(ids);
        reloadTableData();
    }

    private boolean confirmBatchTableAction(String title, String message) {
        if (getTable().getSelectedRows().length == 0) {
            return false;
        }
        return JOptionPane.showConfirmDialog(this,
                message + "\n" +
                        Arrays.stream(getTable().getSelectedRows()).
                                mapToObj(row -> getTable().getModel().getValueAt(row, getDataDescriptionColumn()).toString()).
                                collect(Collectors.joining(", ")),
                title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
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
        DataTableModel<T> model = getTableModel();
        // Clear table
        model.setRowCount(0);

        doReloadData();
        updateUI(null);
//        List<T> departments = departmentService.getAll();
//
//        departments.forEach(department -> {
//            model.addRow(department, new Object[]{department.getId(), department.getName()});
//        });
    }

    private void updateUI(ListSelectionEvent listSelectionEvent) {
        getUpdateBtn().setEnabled(getTable().getSelectedRowCount() > 0);
        getDeleteBtn().setEnabled(getTable().getSelectedRowCount() > 0);
    }

    protected void addRow() {
        DataTableModel<T> model = getTableModel();

        model.addRow(createDataInstance(), new Object[]{null, "", "", false, false, false});
        int newRow = getTable().getRowCount() - 1;
        getTable().setRowSelectionInterval(newRow, newRow);
        startInserting();
    }





}
