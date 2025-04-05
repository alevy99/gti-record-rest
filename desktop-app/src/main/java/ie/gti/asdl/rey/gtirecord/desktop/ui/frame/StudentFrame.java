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
import ie.gti.asdl.rey.gtirecord.model.entity.*;
import ie.gti.asdl.rey.gtirecord.model.util.Pair;
import org.springframework.context.ApplicationContext;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import java.util.*;

import static ie.gti.asdl.rey.gtirecord.desktop.ui.FrameManager.FrameType.*;
import static ie.gti.asdl.rey.gtirecord.desktop.util.SwingUIUtils.createSafeListener;

/**
 *
 * @author Andrei
 */
public class StudentFrame extends AbstractTableDataFrame<Pair<Student, User>> {

    private static int columnCnt = 0;

    private enum COLUMNS {
        PERSON_ID(columnCnt++), FIRST_NAME(columnCnt++), LAST_NAME(columnCnt++),
        USERNAME(columnCnt++), PASSWORD(columnCnt++), GROUP(columnCnt++),
        EDUCATION(columnCnt++), ON_ERASMUS(columnCnt++), EMERGENCY_CONTACTS(columnCnt++);

        final int index;
        COLUMNS(int index) {
            this.index = index;
        }
    }

//    private final ModuleService moduleService;

    private final StudentService studentService;

    private final UserService userService;

//    private final TeacherModuleService teacherModuleService;

    private Pair<Student, User> selectedPair;

//    private List<Module> allModules;
//    private List<Module> teacherModules;

    private Integer highlightedRow;

    /**
     * Creates new form TeacherFrame
     */
    public StudentFrame(FrameManager frameManager, ServiceManager serviceManager) {
        super(frameManager);
        studentService = serviceManager.getStudentService();
//        moduleService = serviceManager.getModuleService();
//        teacherModuleService = serviceManager.getTeacherModuleService();
        userService = serviceManager.getUserService();
        initComponents();
        initFrame();
    }

    @Override
    protected void initFrame() {
        // Init table model first
        initTableModel();
        super.initFrame();

        tblStudent.setHighlightedRowSupplier(() -> highlightedRow);

        TableColumnModel columnModel = getTable().getColumnModel();
        columnModel.getColumn(COLUMNS.PERSON_ID.index)          .setMaxWidth(60);
        columnModel.getColumn(COLUMNS.FIRST_NAME.index)         .setMinWidth(80);
        columnModel.getColumn(COLUMNS.LAST_NAME.index)          .setMinWidth(80);
        columnModel.getColumn(COLUMNS.USERNAME.index)           .setMinWidth(60);
        columnModel.getColumn(COLUMNS.PASSWORD.index)           .setMinWidth(60);
        columnModel.getColumn(COLUMNS.GROUP.index)              .setMinWidth(80);
        columnModel.getColumn(COLUMNS.EDUCATION.index)          .setMaxWidth(80);
        columnModel.getColumn(COLUMNS.ON_ERASMUS.index)         .setMinWidth(40);
        columnModel.getColumn(COLUMNS.EMERGENCY_CONTACTS.index) .setMaxWidth(60);

        // Init module table
        tblStudent.getSelectionModel().addListSelectionListener(createSafeListener(event -> onStudentSelect()));

//        initModuleTable();
    }

    @Override
    protected void updateUI() {
        super.updateUI();
        btnPersonInfo.setEnabled(
                (selectedPair != null)
                && (selectedPair.getValue1() != null)
                && (selectedPair.getValue1().getPerson() != null)
                && (selectedPair.getValue1().getPerson().getId() != null));
    }

    private void onStudentSelect() {
        reloadStudent();
        updateModulesTableUI();
    }

    private void reloadStudent() {
        // Show modules for the first teacher
        Arrays.stream(tblStudent.getSelectedRows()).findFirst().ifPresent(row -> {
            highlightedRow = row; // Set new highlighted row
            tblStudent.repaint(); // Repaint after we changed highlightedRow
            selectedPair = getTableModel().getData(tblStudent.convertRowIndexToModel(row));
        });
    }

//    private void reloadAllModules() {
//        tblAllModules.clear();
//        allModules = moduleService.getAll();
//    }

    private void initTableModel() {
        if (! (getTable().getModel() instanceof DataTableModel)) {
            getTable().setModel(new DataTableModel<Pair<Student, User>>(
                    new Object [][] {

                    },
                    new String [] {
                            "Person ID", "First name", "Last name", "Username", "Password", "Group", "Education", "On Erasmus", "Contacts"
                    }
            ) {
                Class[] types = new Class [] {
                        java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Boolean.class, java.lang.String.class
                };
                boolean[] canEdit = new boolean [] {
                        false, true, true, true, true, false, true, true, true
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

//    private void initModuleTable() {
//        SwingUIUtils.addTableFilter(tblTeacherModules, tfModuleFilter);
//        SwingUIUtils.addTableFilter(tblAllModules, tfModuleFilter);
//
//        tblTeacherModules.getSelectionModel().addListSelectionListener(createSafeListener(listener -> updateButtonsUI()));
//        tblAllModules.getSelectionModel().addListSelectionListener(createSafeListener(listener -> updateButtonsUI()));
//    }
//
//    private void updateButtonsUI() {
//        btnAddModuleToTeacher.setEnabled(tblAllModules.getSelectedRowCount() > 0);
//        btnRemoveModuleFromTeacher.setEnabled(tblTeacherModules.getSelectedRowCount() > 0);
//    }

    private void updateModulesTableUI() {
//        lblTeacherModulesTitle.setText((selectedPair == null) || (selectedPair.getValue1() == null) ? "Teacher Modules" :
//                DescriptionUtil.getShortDescription(selectedPair.getValue1().getPerson()) + " Modules");
//
//        tblTeacherModules.clear();
//        tblAllModules.clear();
//
//        if (teacherModules != null) {
//            teacherModules.forEach(module -> {
//                getTeacherModulesTableModel().addRow(module, new Object[] {module.getId(), module.getName(), module.getCode()});
//            });
//        }
//        if (allModules != null) {
//            List<Module> allExceptTeacherModules = new ArrayList<>(allModules);
//            if (teacherModules != null) {
//                allExceptTeacherModules.removeAll(teacherModules);
//            }
//            allExceptTeacherModules.forEach(module -> {
//                getAllModulesTableModel().addRow(module, new Object[] {module.getId(), module.getName(), module.getCode()});
//            });
//        }
    }

//    protected DataTableModel<Module> getTeacherModulesTableModel() {
//        return (DataTableModel<Module>) tblTeacherModules.getModel();
//    }
//
//    protected DataTableModel<Module> getAllModulesTableModel() {
//        return (DataTableModel<Module>) tblAllModules.getModel();
//    }

    @Override
    protected void onFrameShown() {
        super.onFrameShown();
//        reloadAllModules();
//        reloadTeacherModules();
        updateModulesTableUI();
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
        tblStudent = new PaddedJTable();
        jTitle = new javax.swing.JLabel();
        pnlControls = new javax.swing.JPanel();
        btnAdd = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnReload = new javax.swing.JButton();
        jUpdatePanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        tfStudentFilter = new javax.swing.JTextField();
        btnClose = new javax.swing.JButton();
        btnPersonInfo = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jPanel1.setPreferredSize(new java.awt.Dimension(1033, 755));

        tblStudent.setAutoCreateRowSorter(true);
        tblStudent.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        tblStudent.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {

                },
                new String [] {
                        "Person ID", "First name", "Last name", "Username", "Password", "Group", "Education", "On Erasmus", "Contacts"
                }
        ) {
            Class[] types = new Class [] {
                    java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Boolean.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                    false, true, true, true, true, false, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblStudent);

        jTitle.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jTitle.setForeground(new java.awt.Color(0, 51, 204));
        jTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jTitle.setText("STUDENTS");

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
        jLabel2.setText("Student filter");

        tfStudentFilter.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        tfStudentFilter.setForeground(new java.awt.Color(0, 51, 204));

        javax.swing.GroupLayout jUpdatePanelLayout = new javax.swing.GroupLayout(jUpdatePanel);
        jUpdatePanel.setLayout(jUpdatePanelLayout);
        jUpdatePanelLayout.setHorizontalGroup(
            jUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jUpdatePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(tfStudentFilter)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE))
                .addContainerGap(9, Short.MAX_VALUE))
        );
        jUpdatePanelLayout.setVerticalGroup(
            jUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jUpdatePanelLayout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tfStudentFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                .addContainerGap(391, Short.MAX_VALUE))
        );
        pnlControlsLayout.setVerticalGroup(
            pnlControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlControlsLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
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

        btnClose.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnClose.setForeground(new java.awt.Color(0, 51, 204));
        btnClose.setText("Close");
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });

        btnPersonInfo.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnPersonInfo.setForeground(new java.awt.Color(0, 51, 204));
        btnPersonInfo.setText("Personal details");
        btnPersonInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPersonInfoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 207, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(pnlControls, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnPersonInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
                            .addComponent(btnClose, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(24, 24, 24))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addComponent(jTitle)
                .addGap(12, 12, 12)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(pnlControls, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(btnPersonInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 1045, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 824, Short.MAX_VALUE)
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

    private void btnPersonInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPersonInfoActionPerformed
        onShowPersonInfo();
    }//GEN-LAST:event_btnPersonInfoActionPerformed

    private void onShowPersonInfo() {
        if ((selectedPair == null) || (selectedPair.getValue1() == null)) {
            btnPersonInfo.setEnabled(false);
            return;
        }
        PersonFrame personFrame = getFrameManager().getFrame(PERSON);
        personFrame.setPerson(selectedPair.getValue1().getPerson());
        getFrameManager().showSub(PERSON);
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
                manager.showSub(STUDENT);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnPersonInfo;
    private javax.swing.JButton btnReload;
    private javax.swing.JButton btnSave;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel jTitle;
    private javax.swing.JPanel jUpdatePanel;
    private javax.swing.JPanel pnlControls;
    private PaddedJTable tblStudent;
    private javax.swing.JTextField tfStudentFilter;
    // End of variables declaration//GEN-END:variables

    @Override
    protected PaddedJTable getTable() {
        return tblStudent;
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
        return tfStudentFilter;
    }

    @Override
    protected int getDataDescriptionColumn() {
        return COLUMNS.FIRST_NAME.index;
    }

    @Override
    protected Pair<Student, User> createDataInstance() {
        var student = new Student();
        student.setPerson(new Person());
        User user = new User();
        user.getRoles().add(Role.RoleType.STUDENT.asRole());
        return new Pair<>(student, user);
    }

    @Override
    protected void doReloadData() {
        studentService.getAll().forEach(student -> {
            Pair<Student, User> pair = new Pair<>(student, null);
            userService.getByPersonId(student.getPerson().getId()).ifPresentOrElse(user -> {
                user.setPersonId(student.getPerson().getId());
                pair.setValue2(user);
            }, () -> {
                User user = new User();
                user.setPersonId(student.getPerson().getId());
                user.getRoles().add(Role.RoleType.STUDENT.asRole());
                pair.setValue2(user);
            });

            getTableModel().addRow(pair, new Object[]{student.getPerson().getId(),
                    student.getPerson().getFirstName(), student.getPerson().getLastName(),
                    pair.getValue2().getUsername(), pair.getValue2().getPassword(),
                    "",
                    student.getEducation(), student.getOnErasmus() == null ? false : student.getOnErasmus(),
                    student.getEmergencyContacts()
            });
        });

        if (getTableModel().getDataList().contains(selectedPair)) {
            int viewRow = tblStudent.convertRowIndexToView(getTableModel().getDataList().indexOf(selectedPair));
            tblStudent.setRowSelectionInterval(viewRow, viewRow);
        } else {
            selectedPair = null;
        }
    }

    @Override
    protected Optional<Integer> doInsertData(Pair<Student, User> pair) {
        if (pair == null) return Optional.empty();
        return studentService.saveWithUser(pair.getValue1(), pair.getValue2());
    }

    @Override
    protected void doUpdateData(Pair<Student, User> pair) {
        if (pair == null) return;
        studentService.saveWithUser(pair.getValue1(), pair.getValue2());
    }

    @Override
    protected void doDeleteData(Integer dataId) {
        if (dataId != null) {
            studentService.delete(dataId);
        }
    }

    @Override
    protected boolean isDataValid(Pair<Student, User> pair) {
        return (pair != null);
//                && (teacher.getName() != null) && ! teacher.getName().isBlank()
//                && teacher.getDepartment() != null && teacher.getCourseType() != null && teacher.getQqiLevel() != null;
    }

    @Override
    protected void fillDataObjectFromTable(Pair<Student, User> pair, Integer row) {
        Student student = pair.getValue1();
        if (getTable().getValueAt(row, COLUMNS.PERSON_ID.index) instanceof Integer id) {
            student.getPerson().setId(id);
        }
        student.getPerson().setFirstName(getTableStringValueAt(row, COLUMNS.FIRST_NAME.index));
        student.getPerson().setLastName(getTableStringValueAt(row, COLUMNS.LAST_NAME.index));
        student.setEducation(getTableStringValueAt(row, COLUMNS.EDUCATION.index));
        student.setOnErasmus((Boolean) getTable().getValueAt(row, COLUMNS.ON_ERASMUS.index));
        student.setEmergencyContacts(getTableStringValueAt(row, COLUMNS.EMERGENCY_CONTACTS.index));

        User user = pair.getValue2();
        user.setUsername(getTableStringValueAt(row, COLUMNS.USERNAME.index));
        user.setPassword(getTableStringValueAt(row, COLUMNS.PASSWORD.index));
    }

    @Override
    protected void addEmptyRowToModel() {
        getTableModel().addRow(createDataInstance(), new Object[]{null, "", "", "", "", "", "", false, ""});
    }

}
