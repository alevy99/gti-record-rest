/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ie.gti.asdl.rey.gtirecord.desktop.ui.frame;

import ie.gti.asdl.rey.gtirecord.core.ServiceManager;
import ie.gti.asdl.rey.gtirecord.core.service.*;
import ie.gti.asdl.rey.gtirecord.desktop.GtiRecordDesktopGuiApp;
import ie.gti.asdl.rey.gtirecord.desktop.ui.AbstractTableDataFrame;
import ie.gti.asdl.rey.gtirecord.desktop.ui.FrameManager;
import ie.gti.asdl.rey.gtirecord.desktop.ui.component.*;
import ie.gti.asdl.rey.gtirecord.desktop.util.SpringGuiRunner;
import ie.gti.asdl.rey.gtirecord.desktop.util.SwingUIUtils;
import ie.gti.asdl.rey.gtirecord.model.annotation.InstanceFactory;
import ie.gti.asdl.rey.gtirecord.model.entity.*;
import ie.gti.asdl.rey.gtirecord.model.entity.Module;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.util.*;
import java.util.function.Function;

import static ie.gti.asdl.rey.gtirecord.desktop.ui.FrameManager.FrameType.*;
import static ie.gti.asdl.rey.gtirecord.desktop.util.AssignmentUtils.calcGradePercent;
import static ie.gti.asdl.rey.gtirecord.desktop.util.SwingUIUtils.createSafeListSelectionListener;

/**
 *
 * @author Andrei
 */
public class AssignmentFrame extends AbstractTableDataFrame<Assignment> {

    private enum COLUMNS {
        ID(0), NAME(1), WEIGHTING(2), MAX_GRADE(3), GROUP(4), MODULE(5);

        final int index;
        COLUMNS(int index) {
            this.index = index;
        }
    }

    private enum STUDENT_RES_COLUMNS {
        STUDENT(0), IS_SUBMITTED(1), IS_GRADED(2), GRADE(3), GRADE_PERCENT(4), MAX_GRADE(5);

        final int index;
        STUDENT_RES_COLUMNS(int index) {
            this.index = index;
        }
    }

    private final GroupModuleService groupModuleService;

    private final GroupService groupService;

    private final AssignmentService assignmentService;

    private final StudentAssignmentService studentAssignmentService;

    private Assignment selectedAssignment;

    private Integer assignmentHighlightedRow;

    private Map<Group, List<Module>> groupModulesMap = new HashMap<>();

    private List<GroupModule> groupModuleList = new ArrayList<>();

    private List<Group> groups = new ArrayList<>();

    private List<StudentAssignment> studentAssignments = new ArrayList<>();

    /**
     * Creates new form CourseFrame
     */
    public AssignmentFrame(FrameManager frameManager, ServiceManager serviceManager) {
        super(frameManager);
        groupModuleService = serviceManager.getGroupModuleService();
        assignmentService = serviceManager.getAssignmentService();
        groupService = serviceManager.getGroupService();
        studentAssignmentService = serviceManager.getStudentAssignmentService();
        initComponents();
        initFrame();
    }

    @Override
    protected void initFrame() {
        // Init table model first
        initTableModel();
        super.initFrame();

        tblAssignment.setHighlightedRowSupplier(() -> assignmentHighlightedRow);

        PaddedDataCellRenderer dataCellRenderer = new PaddedDataCellRenderer(() -> assignmentHighlightedRow);

        TableColumnModel columnModel = getTable().getColumnModel();
        columnModel.getColumn(COLUMNS.ID.index)         .setMaxWidth(35);
        columnModel.getColumn(COLUMNS.NAME.index)       .setMinWidth(100);
        columnModel.getColumn(COLUMNS.WEIGHTING.index)  .setMinWidth(50);
        columnModel.getColumn(COLUMNS.GROUP.index)      .setMinWidth(80);
        columnModel.getColumn(COLUMNS.MODULE.index)     .setMinWidth(80);

        // Init module table
        tblAssignment.getSelectionModel().addListSelectionListener(createSafeListSelectionListener(event -> onAssignmentSelect()));

        initStudentResultTable();

        // Set GROUP custom JComboBox Renderer and Editor
        TableColumn groupColumn = getTable().getColumnModel().getColumn(COLUMNS.GROUP.index);
        groupColumn.setCellRenderer(dataCellRenderer);
        groupColumn.setCellEditor(getGroupDynamicComboBoxEditor(this::getGroupsList));

        // Set MODULE custom JComboBox Renderer and Editor
        TableColumn moduleColumn = getTable().getColumnModel().getColumn(COLUMNS.MODULE.index);
        moduleColumn.setCellRenderer(dataCellRenderer);
        moduleColumn.setCellEditor(getModuleDynamicComboBoxEditor(this::getModulesList));
    }

    private @NotNull DynamicComboBoxEditor<Group> getGroupDynamicComboBoxEditor(Function<Integer, List<Group>> groupsProvider) {
        DynamicComboBoxEditor<Group> cbCellEditor = new DynamicComboBoxEditor<>(groupsProvider);
        cbCellEditor.setRowAwareActionListener((e, row) -> {
            System.out.println("Group combo action performed at row " + row);
            JComboBox<Group> cb = (JComboBox<Group>) e.getSource();
            Group group = (Group) cb.getSelectedItem();
            if (group == null) return;
            int modelRow = tblAssignment.convertRowIndexToModel(row);
            var assignment = getTableModel().getData(modelRow);
            GroupModule assignmentGroupModule = assignment.getGroupModule();
            if (! group.equals(assignmentGroupModule.getGroup())) {
                assignmentGroupModule.setGroup(group);
                // Reset module, since a group was changed
                Module module = InstanceFactory.create(Module.class);
                assignmentGroupModule.setModule(module);
                // Reset groupModule, because we don't know what groupModule will be linked to the assignment since the module is not chosen yet
                assignmentGroupModule.setId(null);
                // Update table MODULE column
                tblAssignment.setValueAt(module, modelRow, COLUMNS.MODULE.index);
            }
        });
        return cbCellEditor;
    }

    private @NotNull DynamicComboBoxEditor<Module> getModuleDynamicComboBoxEditor(Function<Integer, List<Module>> modulesProvider) {
        DynamicComboBoxEditor<Module> cbCellEditor = new DynamicComboBoxEditor<>(modulesProvider);
        cbCellEditor.setRowAwareActionListener((e, row) -> {
            System.out.println("Module combo action performed at row " + row);

            JComboBox<Module> cb = (JComboBox<Module>) e.getSource();
            Module module = (Module) cb.getSelectedItem();
            if (module == null) return;

            int modelRow = tblAssignment.convertRowIndexToModel(row);
            var assignment = getTableModel().getData(modelRow);
            GroupModule assignmentGroupModule = assignment.getGroupModule();
            // If the module was changed, update the groupModule for the assignment
            if (! module.equals(assignmentGroupModule.getModule())) {
                findGroupModule(assignmentGroupModule.getGroup(), module).ifPresent(assignment::setGroupModule);
            }
        });
        return cbCellEditor;
    }

    private List<Group> getGroupsList(Integer viewRow) {
        return groups;
    }

    private List<Module> getModulesList(Integer viewRow) {
        int modelRow = tblAssignment.convertRowIndexToModel(viewRow);
        Assignment assignment = getTableModel().getData(modelRow);
        return getModulesList(assignment.getGroupModule().getGroup());
    }

    private List<Module> getModulesList(Group group) {
        return groupModulesMap.getOrDefault(group, new ArrayList<>());
    }

    @Override
    protected void onSaveDataCompleted() {
        super.onSaveDataCompleted();
        reloadStudentResults();
    }

    private void onAssignmentSelect() {
        reloadStudentResults();
    }

    private void reloadStudentResults() {
        tblStudentResult.clear();

        // Show modules for the first course
        Arrays.stream(tblAssignment.getSelectedRows()).findFirst().ifPresentOrElse(row -> {
            assignmentHighlightedRow = row; // Set new highlighted row
            tblAssignment.repaint(); // Repaint after we changed highlightedRow
            selectedAssignment = getTableModel().getData(tblAssignment.convertRowIndexToModel(row));

            studentAssignments = studentAssignmentService.getByAssignmentId(selectedAssignment.getId());
        }, () -> {
            studentAssignments.clear();
        });

        updateStudentResultTableUI();
    }

    private void initTableModel() {
        if (! (getTable().getModel() instanceof DataTableModel)) {
            getTable().setModel(new DataTableModel<Assignment>(
                    new Object [][] {

                    },
                    new String [] {
                            "ID", "Name", "Weighting, %", "Max grade", "Group", "Module"
                    }
            ) {
                Class[] types = new Class [] {
                        java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class
                };
                boolean[] canEdit = new boolean [] {
                        false, true, true, true, true, true
                };

                public Class getColumnClass(int columnIndex) {
                    return types [columnIndex];
                }

                public boolean isCellEditable(int rowIndex, int columnIndex) {
                    return canEdit [columnIndex];
                }
            });
        }
    }

    private void initStudentResultTable() {
        if (!(tblStudentResult.getModel() instanceof DataTableModel<?>)) {
            tblStudentResult.setModel(new DataTableModel<StudentAssignment>(
                    new Object [][] {

                    },
                    new String [] {
                            "Student", "Submitted", "Graded", "Grade", "Grade, %", "Max grade"
                    }
            ) {
                Class[] types = new Class [] {
                        java.lang.Object.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Integer.class, java.lang.Double.class, java.lang.Integer.class
                };
                boolean[] canEdit = new boolean [] {
                        false, true, true, true, false, false
                };

                public Class getColumnClass(int columnIndex) {
                    return types [columnIndex];
                }

                public boolean isCellEditable(int rowIndex, int columnIndex) {
                    return canEdit [columnIndex];
                }
            });
        }

        SwingUIUtils.addTableFilter(tblStudentResult, tfStudentResultFilter);

        tblStudentResult.getSelectionModel().addListSelectionListener(createSafeListSelectionListener(listener -> updateButtonsUI()));

        tblStudentResult.getColumnModel().getColumn(STUDENT_RES_COLUMNS.STUDENT.index).setMinWidth(250);
        tblStudentResult.getColumnModel().getColumn(STUDENT_RES_COLUMNS.IS_SUBMITTED.index).setMaxWidth(200);
        tblStudentResult.getColumnModel().getColumn(STUDENT_RES_COLUMNS.IS_GRADED.index).setMaxWidth(200);
        tblStudentResult.getColumnModel().getColumn(STUDENT_RES_COLUMNS.GRADE.index).setMaxWidth(200);

        TableColumn studentColumn = tblStudentResult.getColumnModel().getColumn(STUDENT_RES_COLUMNS.STUDENT.index);
        studentColumn.setCellRenderer(new PaddedDataCellRenderer(null));
    }

    private void updateButtonsUI() {
//        btnAddModuleToCourse.setEnabled(tblAllModules.getSelectedRowCount() > 0);
//        btnRemoveModuleFromCourse.setEnabled(tblStudentResult.getSelectedRowCount() > 0);
    }

//    private void updateSelectedAssignment() {
//        if (assignmentHighlightedRow != null) {
//            int modelRow = tblAssignment.convertRowIndexToModel(assignmentHighlightedRow);
//            selectedAssignment = getTableModel().getData(modelRow);
//        }
//    }


    private void updateStudentResultTableUI() {
        lblStudentResultTitle.setText((selectedAssignment == null || selectedAssignment.getName() == null)
                ? "Students results" : selectedAssignment.getName() + " results");

        tblStudentResult.clear();

        studentAssignments.forEach(sa -> {
            Double gradePercent = calcGradePercent(selectedAssignment, sa.getGrade());
            getStudentResultTableModel().addRow(sa, new Object[] {sa.getStudent(), sa.getIsSubmitted(), sa.getIsGraded(),
                    sa.getGrade(), gradePercent, selectedAssignment.getMaxGrade()});
        });
    }

    protected DataTableModel<StudentAssignment> getStudentResultTableModel() {
        return (DataTableModel<StudentAssignment>) tblStudentResult.getModel();
    }

    @Override
    protected void onFrameShown() {
        super.onFrameShown();
        reloadStudentResults();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblAssignment = new PaddedJTable();
        lblTitle = new javax.swing.JLabel();
        pnlStudentResults = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblStudentResult = new PaddedJTable();
        lblStudentResultTitle = new javax.swing.JLabel();
        btnSaveStudentResults = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        pnlControls = new javax.swing.JPanel();
        btnAdd = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnReload = new javax.swing.JButton();
        jUpdatePanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        tfAssignmentFilter = new javax.swing.JTextField();
        jUpdatePanel2 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        tfStudentResultFilter = new javax.swing.JTextField();
        btnOpenGroups = new javax.swing.JButton();
        btnOpenModules = new javax.swing.JButton();
        btnOpenStudents = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jPanel1.setPreferredSize(new java.awt.Dimension(1033, 755));

        tblAssignment.setAutoCreateRowSorter(true);
        tblAssignment.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        tblAssignment.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Name", "Weighting, %", "Max grade", "Group", "Module"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblAssignment);

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(0, 51, 204));
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitle.setText("ASSIGNMENTS");

        pnlStudentResults.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        tblStudentResult.setAutoCreateRowSorter(true);
        tblStudentResult.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Student", "Submitted", "Graded", "Grade", "Grade, %", "Max grade"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Integer.class, java.lang.Double.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true, true, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tblStudentResult);

        lblStudentResultTitle.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblStudentResultTitle.setForeground(new java.awt.Color(0, 51, 204));
        lblStudentResultTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblStudentResultTitle.setText("Students results");

        btnSaveStudentResults.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnSaveStudentResults.setForeground(new java.awt.Color(0, 51, 204));
        btnSaveStudentResults.setText("Save student result(s)");
        btnSaveStudentResults.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveStudentResultsActionPerformed(evt);
            }
        });

        btnClose.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnClose.setForeground(new java.awt.Color(0, 51, 204));
        btnClose.setText("Close");
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlStudentResultsLayout = new javax.swing.GroupLayout(pnlStudentResults);
        pnlStudentResults.setLayout(pnlStudentResultsLayout);
        pnlStudentResultsLayout.setHorizontalGroup(
            pnlStudentResultsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlStudentResultsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlStudentResultsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1001, Short.MAX_VALUE)
                    .addComponent(lblStudentResultTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlStudentResultsLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnSaveStudentResults, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(258, 258, 258)
                        .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        pnlStudentResultsLayout.setVerticalGroup(
            pnlStudentResultsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlStudentResultsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblStudentResultTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlStudentResultsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSaveStudentResults, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        pnlControls.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        btnAdd.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnAdd.setForeground(new java.awt.Color(0, 51, 204));
        btnAdd.setText("Add");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        btnSave.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnSave.setForeground(new java.awt.Color(0, 51, 204));
        btnSave.setText("Save");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnDelete.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnDelete.setForeground(new java.awt.Color(0, 51, 204));
        btnDelete.setText("Delete");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnReload.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnReload.setForeground(new java.awt.Color(0, 51, 204));
        btnReload.setText("Reload");
        btnReload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReloadActionPerformed(evt);
            }
        });

        jUpdatePanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 3, 15)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 51, 204));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel2.setText("Assignment filter");

        tfAssignmentFilter.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        tfAssignmentFilter.setForeground(new java.awt.Color(0, 51, 204));

        javax.swing.GroupLayout jUpdatePanelLayout = new javax.swing.GroupLayout(jUpdatePanel);
        jUpdatePanel.setLayout(jUpdatePanelLayout);
        jUpdatePanelLayout.setHorizontalGroup(
            jUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jUpdatePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(tfAssignmentFilter)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jUpdatePanelLayout.setVerticalGroup(
            jUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jUpdatePanelLayout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tfAssignmentFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jUpdatePanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 3, 15)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 51, 204));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel6.setText("Student filter");

        tfStudentResultFilter.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        tfStudentResultFilter.setForeground(new java.awt.Color(0, 51, 204));

        javax.swing.GroupLayout jUpdatePanel2Layout = new javax.swing.GroupLayout(jUpdatePanel2);
        jUpdatePanel2.setLayout(jUpdatePanel2Layout);
        jUpdatePanel2Layout.setHorizontalGroup(
            jUpdatePanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jUpdatePanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jUpdatePanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(tfStudentResultFilter)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jUpdatePanel2Layout.setVerticalGroup(
            jUpdatePanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jUpdatePanel2Layout.createSequentialGroup()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tfStudentResultFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlControlsLayout = new javax.swing.GroupLayout(pnlControls);
        pnlControls.setLayout(pnlControlsLayout);
        pnlControlsLayout.setHorizontalGroup(
            pnlControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlControlsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnlControlsLayout.createSequentialGroup()
                        .addComponent(btnReload, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1))
                    .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jUpdatePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33)
                .addComponent(jUpdatePanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlControlsLayout.setVerticalGroup(
            pnlControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlControlsLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jUpdatePanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jUpdatePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlControlsLayout.createSequentialGroup()
                        .addGroup(pnlControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pnlControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnReload, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        btnOpenGroups.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnOpenGroups.setForeground(new java.awt.Color(0, 51, 204));
        btnOpenGroups.setText("Groups");
        btnOpenGroups.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenGroupsActionPerformed(evt);
            }
        });

        btnOpenModules.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnOpenModules.setForeground(new java.awt.Color(0, 51, 204));
        btnOpenModules.setText("Modules");
        btnOpenModules.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenModulesActionPerformed(evt);
            }
        });

        btnOpenStudents.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnOpenStudents.setForeground(new java.awt.Color(0, 51, 204));
        btnOpenStudents.setText("Students");
        btnOpenStudents.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenStudentsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(pnlStudentResults, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(pnlControls, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnOpenModules, javax.swing.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE)
                            .addComponent(btnOpenStudents, javax.swing.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnOpenGroups)))
                .addGap(24, 24, 24))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(lblTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlControls, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnOpenGroups, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnOpenModules, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnOpenStudents, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)))
                .addGap(2, 2, 2)
                .addComponent(pnlStudentResults, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(16, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 1057, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 774, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        onAddLine();
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        onSaveData();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        onDeleteData();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnReloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReloadActionPerformed
        reloadTableData();
    }//GEN-LAST:event_btnReloadActionPerformed

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        getFrameManager().showParent();
    }//GEN-LAST:event_btnCloseActionPerformed

    private void btnOpenGroupsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenGroupsActionPerformed
        getFrameManager().showSub(GROUP);
    }//GEN-LAST:event_btnOpenGroupsActionPerformed

    private void btnOpenModulesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenModulesActionPerformed
        getFrameManager().showSub(MODULE);
    }//GEN-LAST:event_btnOpenModulesActionPerformed

    private void btnOpenStudentsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenStudentsActionPerformed
        getFrameManager().showSub(FrameManager.FrameType.STUDENT);
    }//GEN-LAST:event_btnOpenStudentsActionPerformed

    private void btnSaveStudentResultsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveStudentResultsActionPerformed
        saveStudentResults();
    }//GEN-LAST:event_btnSaveStudentResultsActionPerformed

    private void saveStudentResults() {
        Arrays.stream(tblStudentResult.getSelectedRows()).findFirst().ifPresent(row -> {
            int modelRow = tblStudentResult.convertRowIndexToModel(row);
            var studentAssignment = getStudentResultTableModel().getData(modelRow);
            // Fill model data
            studentAssignment.setIsSubmitted((Boolean) tblStudentResult.getValueAt(row, STUDENT_RES_COLUMNS.IS_SUBMITTED.index));
            studentAssignment.setIsGraded((Boolean) tblStudentResult.getValueAt(row, STUDENT_RES_COLUMNS.IS_GRADED.index));
            studentAssignment.setGrade((Integer) tblStudentResult.getValueAt(row, STUDENT_RES_COLUMNS.GRADE.index));
            studentAssignmentService.update(studentAssignment);
            // Update table cell
            tblStudentResult.setValueAt(calcGradePercent(selectedAssignment, studentAssignment.getGrade()), row, STUDENT_RES_COLUMNS.GRADE_PERCENT.index);
        });
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ApplicationContext context = SpringGuiRunner.run(GtiRecordDesktopGuiApp.class, args);
                FrameManager manager = context.getBean(FrameManager.class);
                manager.showSub(ASSIGNMENT);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnOpenGroups;
    private javax.swing.JButton btnOpenModules;
    private javax.swing.JButton btnOpenStudents;
    private javax.swing.JButton btnReload;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSaveStudentResults;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel jUpdatePanel;
    private javax.swing.JPanel jUpdatePanel2;
    private javax.swing.JLabel lblStudentResultTitle;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel pnlControls;
    private javax.swing.JPanel pnlStudentResults;
    private PaddedJTable tblAssignment;
    private PaddedJTable tblStudentResult;
    private javax.swing.JTextField tfAssignmentFilter;
    private javax.swing.JTextField tfStudentResultFilter;
    // End of variables declaration//GEN-END:variables

    @Override
    protected PaddedJTable getTable() {
        return tblAssignment;
    }

    @Override
    protected JButton getDeleteBtn() {
        return btnDelete;
    }

    @Override
    protected JButton getSaveBtn() {
        return btnSave;
    }

    @Override
    protected JTextField getTableFilterField() {
        return tfAssignmentFilter;
    }

    @Override
    protected int getDataDescriptionColumn() {
        return COLUMNS.NAME.index;
    }

    @Override
    protected Assignment createDataInstance() {
        return InstanceFactory.create(Assignment.class);
    }

    private Optional<GroupModule> findGroupModule(Group group, Module module) {
        return groupModuleList.stream()
                .filter(groupModule -> groupModule.getGroup().equals(group) && groupModule.getModule().equals(module))
                .findFirst();
    }

    @Override
    protected void doReloadData() {
        groupModulesMap.clear();
        groupModulesMap = groupModuleService.getAllGroupedByGroup();

        groupModuleList.clear();
        groupModuleList = groupModuleService.getAll();

        groups = groupService.getAll();

        assignmentService.getAll().forEach(assignment -> {
            getTableModel().addRow(assignment, new Object[]{
                    assignment.getId(), assignment.getName(), assignment.getWeighting(), assignment.getMaxGrade(),
                    assignment.getGroupModule().getGroup(), assignment.getGroupModule().getModule()
            });
        });

        if (getTableModel().getDataList().contains(selectedAssignment)) {
            int viewRow = tblAssignment.convertRowIndexToView(getTableModel().getDataList().indexOf(selectedAssignment));
            tblAssignment.setRowSelectionInterval(viewRow, viewRow);
        } else {
            selectedAssignment = null;
        }
    }

    @Override
    protected Optional<Integer> doInsertData(Assignment data) {
        return assignmentService.insert(data);
    }

    @Override
    protected void doUpdateData(Assignment data) {
        assignmentService.update(data);
    }

    @Override
    protected void doDeleteData(Assignment data) {
        assignmentService.delete(data.getId());
    }

    @Override
    protected boolean isDataValid(Assignment data) {
        return (data != null); // && (data.getName() != null) && ! data.getName().isBlank()
//                && data.getDepartment() != null && data.getCourseType() != null && data.getQqiLevel() != null;
    }

    @Override
    protected void fillDataObjectFromTable(Assignment assignment, Integer row) {
        if (getTable().getValueAt(row, COLUMNS.ID.index) instanceof Integer id) {
            assignment.setId(id);
        }

        assignment.setName(getTableStringValueAt(row, COLUMNS.NAME.index));

        assignment.setWeighting((Integer) tblAssignment.getValueAt(row, COLUMNS.WEIGHTING.index));
        assignment.setMaxGrade((Integer) tblAssignment.getValueAt(row, COLUMNS.MAX_GRADE.index));

        Group group = (Group) getTable().getValueAt(row, COLUMNS.GROUP.index);
        Module module = (Module) getTable().getValueAt(row, COLUMNS.MODULE.index);

        if (assignment.getGroupModule().getId() == null) {
            findGroupModule(group, module).ifPresent(groupModule -> {
                assignment.getGroupModule().setId(groupModule.getId());
            });
        }
        assignment.getGroupModule().setGroup(group);
        assignment.getGroupModule().setModule(module);
    }

    @Override
    protected void addEmptyRowToModel() {
        getTableModel().addRow(createDataInstance(), new Object[]{null, "", null, null,
                InstanceFactory.create(Group.class), InstanceFactory.create(Module.class)});
    }

}
