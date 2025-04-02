/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ie.gti.asdl.rey.gtirecord.desktop.ui.frame;

import ie.gti.asdl.rey.gtirecord.core.ServiceManager;
import ie.gti.asdl.rey.gtirecord.core.service.UserService;
import ie.gti.asdl.rey.gtirecord.desktop.GtiRecordDesktopGuiApp;
import ie.gti.asdl.rey.gtirecord.desktop.ui.AbstractTableDataFrame;
import ie.gti.asdl.rey.gtirecord.desktop.ui.FrameManager;
import ie.gti.asdl.rey.gtirecord.desktop.ui.component.DataTableModel;
import ie.gti.asdl.rey.gtirecord.desktop.ui.component.PaddedJTable;
import ie.gti.asdl.rey.gtirecord.desktop.ui.component.PasswordCellEditor;
import ie.gti.asdl.rey.gtirecord.desktop.ui.component.PasswordCellRenderer;
import ie.gti.asdl.rey.gtirecord.desktop.util.SpringGuiRunner;
import ie.gti.asdl.rey.gtirecord.model.entity.Role;
import ie.gti.asdl.rey.gtirecord.model.entity.User;
import ie.gti.asdl.rey.gtirecord.model.util.UserUtils;
import org.springframework.context.ApplicationContext;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static ie.gti.asdl.rey.gtirecord.desktop.ui.FrameManager.FrameType.PERSON;
import static ie.gti.asdl.rey.gtirecord.desktop.ui.FrameManager.FrameType.USER;

/**
 * @author Andrei
 */
/// Do not apply SpringBootApp, if we use 'web' profile
//@Profile("!web")
public class UserFrame extends AbstractTableDataFrame<User> {

    enum USER_TBL_COL {
        ID(0), USERNAME(1), PASSWORD(2),
        IS_STUDENT(3), IS_TEACHER(4), IS_ADMIN(5);

          private final int index;
          USER_TBL_COL(int index) {
              this.index = index;
          }
    }

    private final UserService userService;

    private Integer highlightedRow;

    private User selectedUser;

    private static final String BTN_PERSONAL_DETAILS_CAPTION = "Personal details";

    /**
     * Creates new form PersonFrame
     */
    public UserFrame(FrameManager frameManager, ServiceManager serviceManager) {
        super(frameManager);
        userService = serviceManager.getUserService();
        initComponents();
        initForm();
    }

    @Override
    protected void initForm() {
        initModel();
        super.initForm();

        tblUsers.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        tblUsers.setHighlightedRowSupplier(() -> highlightedRow);

        TableColumnModel columnModel = tblUsers.getColumnModel();
        columnModel.getColumn(USER_TBL_COL.ID.index).setMaxWidth(35);
        columnModel.getColumn(USER_TBL_COL.USERNAME.index).setMinWidth(60);
        columnModel.getColumn(USER_TBL_COL.PASSWORD.index).setMinWidth(60);
        columnModel.getColumn(USER_TBL_COL.IS_STUDENT.index).setMaxWidth(70);
        columnModel.getColumn(USER_TBL_COL.IS_TEACHER.index).setMaxWidth(70);
        columnModel.getColumn(USER_TBL_COL.IS_ADMIN.index).setMaxWidth(70);

        tblUsers.getSelectionModel().addListSelectionListener(event -> onUserSelect());

        tblUsers.getColumnModel().getColumn(USER_TBL_COL.PASSWORD.index).setCellRenderer(new PasswordCellRenderer(() -> highlightedRow));
        tblUsers.getColumnModel().getColumn(USER_TBL_COL.PASSWORD.index).setCellEditor(new PasswordCellEditor());
    }

    private void onUserSelect() {
        // Show modules for the first course
        Arrays.stream(tblUsers.getSelectedRows()).findFirst().ifPresentOrElse(row -> {
            highlightedRow = row; // Set new highlighted row
            tblUsers.repaint(); // Repaint after we changed highlightedRow
            selectedUser = getTableModel().getData(tblUsers.convertRowIndexToModel(row));
            btnPersonInfo.setText(BTN_PERSONAL_DETAILS_CAPTION + ": " + selectedUser.getUsername());
        }, () -> {
            selectedUser = null;
            btnPersonInfo.setText(BTN_PERSONAL_DETAILS_CAPTION);
        });
    }

    private void initModel() {
        if (! (tblUsers.getModel() instanceof DataTableModel)) {
            tblUsers.setModel(new DataTableModel<User>(
                    new Object[][]{
                            {null, null, null, null, null, null}
                    },
                    new String[]{
                            "ID", "Username", "Password", "Student", "Teacher", "Admin"
                    }
            ) {
                Class[] types = new Class[]{
                        Integer.class, String.class, String.class, Boolean.class, Boolean.class, Boolean.class
                };
                boolean[] canEdit = new boolean[]{
                        false, true, true, true, true, true
                };
                public boolean isCellEditable(int rowIndex, int columnIndex) {
                    return canEdit[columnIndex];
                }
                public Class getColumnClass(int columnIndex) {
                    return types[columnIndex];
                }
            });
        }
    }

    @Override
    protected void updateUI() {
        super.updateUI();
        btnPersonInfo.setEnabled(tblUsers.getSelectedRowCount() > 0);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPasswordField1 = new javax.swing.JPasswordField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblUsers = new javax.swing.JTable();
        btnClose = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jButtonsPanel = new javax.swing.JPanel();
        pnlControls = new javax.swing.JPanel();
        btnAdd = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        jPersonDetailsPanel = new javax.swing.JPanel();
        jUpdatePanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        tfTableFilter = new javax.swing.JTextField();
        btnReload = new javax.swing.JButton();
        btnPersonInfo = new javax.swing.JButton();

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jPasswordField1.setText("jPasswordField1");

        setAlwaysOnTop(true);

        tblUsers.setAutoCreateRowSorter(true);
        tblUsers.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        tblUsers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Username", "Password", "Student", "Teacher", "Admin"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Long.class, java.lang.String.class, java.lang.String.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tblUsers.setFillsViewportHeight(true);
        tblUsers.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tblUsers.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tblUsers.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tblUsers);

        btnClose.setText("Close");
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("USERS");

        pnlControls.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnAdd.setText("Add");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        btnSave.setText("Save");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnDelete.setText("Delete");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlControlsLayout = new javax.swing.GroupLayout(pnlControls);
        pnlControls.setLayout(pnlControlsLayout);
        pnlControlsLayout.setHorizontalGroup(
            pnlControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlControlsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnDelete, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(12, Short.MAX_VALUE))
        );
        pnlControlsLayout.setVerticalGroup(
            pnlControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlControlsLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jPersonDetailsPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        jUpdatePanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("User filter:");

        btnReload.setText("Reload");
        btnReload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReloadActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jUpdatePanelLayout = new javax.swing.GroupLayout(jUpdatePanel);
        jUpdatePanel.setLayout(jUpdatePanelLayout);
        jUpdatePanelLayout.setHorizontalGroup(
            jUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jUpdatePanelLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jUpdatePanelLayout.createSequentialGroup()
                        .addComponent(tfTableFilter)
                        .addGap(15, 15, 15))
                    .addGroup(jUpdatePanelLayout.createSequentialGroup()
                        .addGroup(jUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnReload, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jUpdatePanelLayout.createSequentialGroup()
                                .addGap(39, 39, 39)
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(18, Short.MAX_VALUE))))
        );
        jUpdatePanelLayout.setVerticalGroup(
            jUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jUpdatePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tfTableFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnReload, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23))
        );

        javax.swing.GroupLayout jPersonDetailsPanelLayout = new javax.swing.GroupLayout(jPersonDetailsPanel);
        jPersonDetailsPanel.setLayout(jPersonDetailsPanelLayout);
        jPersonDetailsPanelLayout.setHorizontalGroup(
            jPersonDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPersonDetailsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jUpdatePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(90, Short.MAX_VALUE))
        );
        jPersonDetailsPanelLayout.setVerticalGroup(
            jPersonDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jUpdatePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jButtonsPanelLayout = new javax.swing.GroupLayout(jButtonsPanel);
        jButtonsPanel.setLayout(jButtonsPanelLayout);
        jButtonsPanelLayout.setHorizontalGroup(
            jButtonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jButtonsPanelLayout.createSequentialGroup()
                .addContainerGap(7, Short.MAX_VALUE)
                .addComponent(pnlControls, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(328, 328, 328)
                .addComponent(jPersonDetailsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jButtonsPanelLayout.setVerticalGroup(
            jButtonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jButtonsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jButtonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(pnlControls, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPersonDetailsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(21, 21, 21))
        );

        btnPersonInfo.setText("Personal details");
        btnPersonInfo.setName(""); // NOI18N
        btnPersonInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPersonInfoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnPersonInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButtonsPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel1)
                .addGap(34, 34, 34)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 322, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnClose)
                    .addComponent(btnPersonInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        getFrameManager().showParent();
    }//GEN-LAST:event_btnCloseActionPerformed


    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        onDeleteData();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        onAddData();
    }//GEN-LAST:event_btnAddActionPerformed

    private Optional<Integer> doInsert(User user) {
        return userService.insert(user);
    }

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        onAddSaveData();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnPersonInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPersonInfoActionPerformed
        onShowPersonInfo();
    }//GEN-LAST:event_btnPersonInfoActionPerformed

    private void btnReloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReloadActionPerformed
        reloadTableData();
    }//GEN-LAST:event_btnReloadActionPerformed

    private void onShowPersonInfo() {
        if (selectedUser == null) {
            btnPersonInfo.setEnabled(false);
            return;
        }
        PersonFrame personFrame = getFrameManager().getFrame(PERSON);

        userService.insertPersonToUser(selectedUser);

        personFrame.setPersonId(selectedUser.getPersonId());
        personFrame.setUsername(selectedUser.getUsername());
        getFrameManager().showSub(PERSON);
    }

    private void fillUserRoles(int row, User user) {
        if ((Boolean) tblUsers.getValueAt(row, USER_TBL_COL.IS_STUDENT.index)) {
            user.getRoles().add(Role.RoleType.STUDENT.asRole());
        }
        if ((Boolean) tblUsers.getValueAt(row, USER_TBL_COL.IS_TEACHER.index)) {
            user.getRoles().add(Role.RoleType.TEACHER.asRole());
        }
        if ((Boolean) tblUsers.getValueAt(row, USER_TBL_COL.IS_ADMIN.index)) {
            user.getRoles().add(Role.RoleType.ADMIN.asRole());
        }
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
                manager.showSub(USER);
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
    private javax.swing.JPanel jButtonsPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPasswordField jPasswordField1;
    private javax.swing.JPanel jPersonDetailsPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel jUpdatePanel;
    private javax.swing.JPanel pnlControls;
    private javax.swing.JTable tblUsers;
    private javax.swing.JTextField tfTableFilter;
    // End of variables declaration//GEN-END:variables

    @Override
    protected PaddedJTable getTable() {
        return tblUsers;
    }

    @Override
    protected JButton getDeleteBtn() {
        return btnDelete;
    }

    @Override
    protected JButton getAddSaveBtn() {
        return btnSave;
    }

    @Override
    protected JTextField getTableFilterField() {
        return tfTableFilter;
    }

    @Override
    protected int getDataDescriptionColumn() {
        return 1;
    }

    @Override
    protected User createDataInstance() {
        return new User();
    }

    @Override
    protected void doReloadData() {
        List<User> users = userService.getAll();

        users.forEach(user -> {
            getTableModel().addRow(user, new Object[]{user.getId(), user.getUsername(), user.getPassword(),
                    UserUtils.isStudent(user), UserUtils.isTeacher(user), UserUtils.isAdmin(user)});
        });
    }

    @Override
    protected Optional<Integer> doInsertData(User user) {
        return userService.insert(user);
    }

    @Override
    protected void doUpdateData(User user) {
        userService.updateUserWithRoles(user);
    }

    @Override
    protected void doDeleteData(Integer dataId) {
        if (dataId != null) {
            userService.delete(dataId);
        }
    }

    @Override
    protected boolean isDataValid(User data) {
        return true; //(data != null) && (data.getName() != null) && ! data.getName().isBlank();
    }

    @Override
    protected void fillDataObjectFromTable(User user, Integer row) {
        user.setUsername(tblUsers.getValueAt(row, USER_TBL_COL.USERNAME.index).toString());
        user.setPassword(tblUsers.getValueAt(row, USER_TBL_COL.PASSWORD.index).toString());
        fillUserRoles(row, user);
    }

    @Override
    protected void addEmptyRowToModel() {
        getTableModel().addRow(new User(), new Object[]{null, "", "", false, false, false});
    }

}
