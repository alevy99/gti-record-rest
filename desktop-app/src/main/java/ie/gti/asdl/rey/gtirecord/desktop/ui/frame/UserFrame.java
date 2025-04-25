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
import ie.gti.asdl.rey.gtirecord.model.annotation.InstanceFactory;
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
/// Do not apply SpringBootApp if we use 'web' profile
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

    private static final String BTN_PERSONAL_DETAILS_TEXT = "Personal details";

    /**
     * Creates new form PersonFrame
     */
    public UserFrame(FrameManager frameManager, ServiceManager serviceManager) {
        super(frameManager);
        userService = serviceManager.getUserService();
        initComponents();
        this.initFrame();
    }

    @Override
    protected void initFrame() {
        initModel();
        super.initFrame();

        tblUsers.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        tblUsers.setHighlightedRowSupplier(() -> highlightedRow);

        TableColumnModel columnModel = tblUsers.getColumnModel();
        columnModel.getColumn(USER_TBL_COL.ID.index).setMaxWidth(45);
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
            // Set button text
            if (selectedUser.getUsername() == null || selectedUser.getUsername().isEmpty()) {
                btnPersonInfo.setText(BTN_PERSONAL_DETAILS_TEXT);
            } else {
                btnPersonInfo.setText(BTN_PERSONAL_DETAILS_TEXT + ": " + selectedUser.getUsername());
            }
        }, () -> {
            selectedUser = null;
            btnPersonInfo.setText(BTN_PERSONAL_DETAILS_TEXT);
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
                    // Always allow users to edit their own password, but not others
                    boolean sameUser = getData(rowIndex).equals(getFrameManager().getActiveUser());
                    return ( getIsEditable() || (sameUser && columnIndex == USER_TBL_COL.PASSWORD.index)) && canEdit[columnIndex];
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
        btnPersonInfo.setEnabled((selectedUser != null) && (selectedUser.getId() != null));
    }

    @Override
    protected void reloadTableData() {
        super.reloadTableData();

        lblLoggedInUsername.setText(getFrameManager().getActiveUser().getUsername());
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
        tblUsers = new PaddedJTable();
        btnClose = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        pnlControls = new javax.swing.JPanel();
        btnAdd = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnReload = new javax.swing.JButton();
        jUpdatePanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        tfTableFilter = new javax.swing.JTextField();
        btnPersonInfo = new javax.swing.JButton();
        pnlLoggedInAs = new javax.swing.JPanel();
        lblLoggedInUsername = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

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
        setResizable(false);

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
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class
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
        tblUsers.setFillsViewportHeight(true);
        tblUsers.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tblUsers.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tblUsers.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tblUsers);

        btnClose.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnClose.setForeground(new java.awt.Color(0, 51, 204));
        btnClose.setText("Close");
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 51, 204));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("USERS");

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

        javax.swing.GroupLayout pnlControlsLayout = new javax.swing.GroupLayout(pnlControls);
        pnlControls.setLayout(pnlControlsLayout);
        pnlControlsLayout.setHorizontalGroup(
            pnlControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlControlsLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(pnlControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnlControlsLayout.createSequentialGroup()
                        .addComponent(btnReload, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1))
                    .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        pnlControlsLayout.setVerticalGroup(
            pnlControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlControlsLayout.createSequentialGroup()
                .addContainerGap(25, Short.MAX_VALUE)
                .addGroup(pnlControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnReload, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18))
        );

        jUpdatePanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 3, 15)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 51, 204));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel2.setText("Filter:");

        tfTableFilter.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        tfTableFilter.setForeground(new java.awt.Color(0, 51, 204));

        javax.swing.GroupLayout jUpdatePanelLayout = new javax.swing.GroupLayout(jUpdatePanel);
        jUpdatePanel.setLayout(jUpdatePanelLayout);
        jUpdatePanelLayout.setHorizontalGroup(
            jUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jUpdatePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(tfTableFilter, javax.swing.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        jUpdatePanelLayout.setVerticalGroup(
            jUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jUpdatePanelLayout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tfTableFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        btnPersonInfo.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnPersonInfo.setForeground(new java.awt.Color(0, 51, 204));
        btnPersonInfo.setText("Personal details");
        btnPersonInfo.setName(""); // NOI18N
        btnPersonInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPersonInfoActionPerformed(evt);
            }
        });

        lblLoggedInUsername.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblLoggedInUsername.setForeground(new java.awt.Color(255, 0, 0));
        lblLoggedInUsername.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblLoggedInUsername.setText("logged in as ");

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/profile.png"))); // NOI18N

        javax.swing.GroupLayout pnlLoggedInAsLayout = new javax.swing.GroupLayout(pnlLoggedInAs);
        pnlLoggedInAs.setLayout(pnlLoggedInAsLayout);
        pnlLoggedInAsLayout.setHorizontalGroup(
            pnlLoggedInAsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLoggedInAsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblLoggedInUsername, javax.swing.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlLoggedInAsLayout.setVerticalGroup(
            pnlLoggedInAsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 53, Short.MAX_VALUE)
            .addGroup(pnlLoggedInAsLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(lblLoggedInUsername)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane1)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(pnlControls, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jUpdatePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(btnPersonInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 363, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlLoggedInAs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel1))
                    .addComponent(pnlLoggedInAs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 485, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jUpdatePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(pnlControls, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnPersonInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(11, 11, 11)))
                .addGap(14, 14, 14))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        getFrameManager().showParent();
    }//GEN-LAST:event_btnCloseActionPerformed

    private void btnPersonInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPersonInfoActionPerformed
        onShowPersonInfo();
    }//GEN-LAST:event_btnPersonInfoActionPerformed

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

    private void onShowPersonInfo() {
        if (selectedUser == null) {
            btnPersonInfo.setEnabled(false);
            return;
        }
        PersonFrame personFrame = getFrameManager().getFrame(PERSON);
        personFrame.setUser(selectedUser);
        getFrameManager().showSub(PERSON);
    }

    private void fillUserRoles(int row, User user) {
        user.getRoles().clear();
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
        java.awt.EventQueue.invokeLater(() -> {
            ApplicationContext context = SpringGuiRunner.run(GtiRecordDesktopGuiApp.class, args);
            FrameManager manager = context.getBean(FrameManager.class);
            manager.setActiveUser(context.getBean(UserService.class).getByUsername("johnm").orElse(null));
            manager.showSub(USER);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnPersonInfo;
    private javax.swing.JButton btnReload;
    private javax.swing.JButton btnSave;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPasswordField jPasswordField1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel jUpdatePanel;
    private javax.swing.JLabel lblLoggedInUsername;
    private javax.swing.JPanel pnlControls;
    private javax.swing.JPanel pnlLoggedInAs;
    private PaddedJTable tblUsers;
    private javax.swing.JTextField tfTableFilter;
    // End of variables declaration//GEN-END:variables

    @Override
    protected PaddedJTable getTable() {
        return tblUsers;
    }

    @Override
    protected JButton getAddBtn() {
        return btnAdd;
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
        return tfTableFilter;
    }

    @Override
    protected int getDataDescriptionColumn() {
        return 1;
    }

    @Override
    protected boolean getIsEditable() {
        return getFrameManager().isLoggedInAsAdmin();
    }

    @Override
    protected User createDataInstance() {
        return InstanceFactory.create(User.class);
    }

    @Override
    protected void doReloadData() {
        List<User> users = userService.getAll();
        users.forEach(user -> getTableModel().addRow(user, new Object[]{user.getId(), user.getUsername(), user.getPassword(),
                UserUtils.isStudent(user), UserUtils.isTeacher(user), UserUtils.isAdmin(user)}));
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
    protected void doDeleteData(User user) {
        if (user != null) {
            userService.delete(user.getId());
        }
    }

    @Override
    protected boolean isDataValid(User data) {
        return true; //(data != null) && (data.getName() != null) && ! data.getName().isBlank();
    }

    @Override
    protected void fillDataObjectFromTable(User user, Integer row) {
        user.setUsername(getTableStringValueAt(row, USER_TBL_COL.USERNAME.index));
        user.setPassword(getTableStringValueAt(row, USER_TBL_COL.PASSWORD.index));
        fillUserRoles(row, user);
    }

    @Override
    protected void addEmptyRowToModel() {
        getTableModel().addRow(createDataInstance(), new Object[]{null, "", "", false, false, false});
    }

}
