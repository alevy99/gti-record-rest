/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ie.gti.asdl.rey.gtirecord.desktop.ui.frame;

import ie.gti.asdl.rey.gtirecord.desktop.GtiRecordDesktopGuiApp;
import ie.gti.asdl.rey.gtirecord.desktop.ui.comp.*;
import ie.gti.asdl.rey.gtirecord.model.entity.Role;
import ie.gti.asdl.rey.gtirecord.model.entity.User;
import ie.gti.asdl.rey.gtirecord.core.service.ServiceManager;
import ie.gti.asdl.rey.gtirecord.core.service.UserService;
import ie.gti.asdl.rey.gtirecord.desktop.ui.AbstractFrame;
import ie.gti.asdl.rey.gtirecord.desktop.ui.FrameManager;
import ie.gti.asdl.rey.gtirecord.desktop.util.SpringGuiRunner;
import ie.gti.asdl.rey.gtirecord.model.util.UserUtils;
import org.springframework.context.ApplicationContext;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import java.util.*;
import java.util.stream.Collectors;

import static ie.gti.asdl.rey.gtirecord.desktop.ui.FrameManager.FrameType.PERSON;
import static ie.gti.asdl.rey.gtirecord.desktop.ui.FrameManager.FrameType.USER;

/**
 * @author Andrei
 */
/// Do not apply SpringBootApp, if we use 'web' profile
//@Profile("!web")
public class UserFrame extends AbstractFrame {

    enum USER_TBL_COL {
        ID(0), USERNAME(1), PASSWORD(2),
        IS_STUDENT(3), IS_TEACHER(4), IS_ADMIN(5);

          private final int index;
          USER_TBL_COL(int index) {
              this.index = index;
          }
    }

//    private static final String PERSON_INFO_BTN_TITLE = "Person info";

    private final UserService userService;

    private boolean isInserting = false;

    private final Set<Integer> rowsInserting = new HashSet<>();

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
//        final int CELL_PAD = 5;

        super.initForm();

        if (! (jUserTable.getModel() instanceof DataTableModel)) {
            jUserTable.setModel(new DataTableModel<User>(
                    new Object[][]{
                            {null, null, null, null, null, null}
                    },
                    new String[]{
                            "ID", "Username", "Password", "Student", "Teacher", "Admin"
                    }
            ) {
                Class[] types = new Class[]{
                        java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class
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

//        jUserTable.setCellSelectionEnabled(false);
        jUserTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        TableColumnModel columnModel = jUserTable.getColumnModel();
        columnModel.getColumn(USER_TBL_COL.ID.index).setMaxWidth(35);
        columnModel.getColumn(USER_TBL_COL.USERNAME.index).setMinWidth(60);
        columnModel.getColumn(USER_TBL_COL.PASSWORD.index).setMinWidth(60);
        columnModel.getColumn(USER_TBL_COL.IS_STUDENT.index).setMaxWidth(70);
        columnModel.getColumn(USER_TBL_COL.IS_TEACHER.index).setMaxWidth(70);
        columnModel.getColumn(USER_TBL_COL.IS_ADMIN.index).setMaxWidth(70);
//        columnModel.getColumn(USER_TBL_COL.PERSON_INFO.index).setPreferredWidth(50);

        // Add selection listener
        jUserTable.getSelectionModel().addListSelectionListener(this::updateUI);

//        // Add padding to cells
//        PaddedCellRenderer paddedCellRenderer = new PaddedCellRenderer(CELL_PAD);
//
//        // Apply to all text columns
//        for (int i = 0; i < IS_STUDENT_COLUMN; i++) {
//            jUserTable.getColumnModel().getColumn(i).setCellRenderer(paddedCellRenderer);
//        }
//
//        // Set cell editor with paddings
//        JTextField textField = new JTextField();
//        textField.setBorder(new EmptyBorder(0, CELL_PAD, 0, CELL_PAD)); // Apply padding inside the editor
//
//        DefaultCellEditor cellEditor = new DefaultCellEditor(textField);
//        jUserTable.setDefaultEditor(Object.class, cellEditor); // Apply to all cells
//
//        // Add sorter to the table
//        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(jUserTable.getModel());
//        jUserTable.setRowSorter(sorter);

        jUserTable.getColumnModel().getColumn(USER_TBL_COL.PASSWORD.index).setCellRenderer(new PasswordCellRenderer());
        jUserTable.getColumnModel().getColumn(USER_TBL_COL.PASSWORD.index).setCellEditor(new PasswordCellEditor());

//        jUserTable.getColumnModel().getColumn(USER_TBL_COL.PERSON_INFO.index).setCellRenderer(new ButtonCellRenderer());
//        jUserTable.getColumnModel().getColumn(USER_TBL_COL.PERSON_INFO.index).setCellEditor(new ButtonCellEditor<User>(new ActionPerformer<Integer>() {
//            @Override
//            public void actionPerformed(ActionEvent e, Integer row) {
//                System.out.println("BTN CLICK for USER: " + getTableModel().getData(row).getId());
////                System.out.println("Button clicked for UserID: " + data.getData().getId());
//            }
//        }));
    }

    private DataTableModel<User> getTableModel() {
        return (DataTableModel<User>) jUserTable.getModel();
    }

    private void updateUI(ListSelectionEvent listSelectionEvent) {
        jUpdateBtn.setEnabled(jUserTable.getSelectedRowCount() > 0);
        jDeleteBtn.setEnabled(jUserTable.getSelectedRowCount() > 0);
        jPersonInfoBtn.setEnabled(jUserTable.getSelectedRowCount() > 0);
    }

    @Override
    protected void onFormShown() {
        super.onFormShown();
        reloadTableData();
        updateUI(null);
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
        jUserTable = new PaddedJTable();
        jCloseBtn = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jButtonsPanel = new javax.swing.JPanel();
        jAddPanel = new javax.swing.JPanel();
        jAddBtn = new javax.swing.JButton();
        jAddCancelBtn = new javax.swing.JButton();
        jAddSaveBtn = new javax.swing.JButton();
        jUpdatePanel = new javax.swing.JPanel();
        jUpdateBtn = new javax.swing.JButton();
        jReloadBtn = new javax.swing.JButton();
        jDeleteBtn = new javax.swing.JButton();
        jPersonDetailsPanel = new javax.swing.JPanel();
        jPersonInfoBtn = new javax.swing.JButton();

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
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jUserTable.setAutoCreateRowSorter(true);
        jUserTable.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jUserTable.setModel(new javax.swing.table.DefaultTableModel(
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
        jUserTable.setFillsViewportHeight(true);
        jUserTable.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jUserTable.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jUserTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jUserTable);

        jCloseBtn.setText("Close");
        jCloseBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCloseBtnActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("USERS");

        jAddPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jAddBtn.setText("Add new user");
        jAddBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jAddBtnActionPerformed(evt);
            }
        });

        jAddCancelBtn.setText("Cancel");
        jAddCancelBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jAddCancelBtnActionPerformed(evt);
            }
        });

        jAddSaveBtn.setText("Save new user(s)");
        jAddSaveBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jAddSaveBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jAddPanelLayout = new javax.swing.GroupLayout(jAddPanel);
        jAddPanel.setLayout(jAddPanelLayout);
        jAddPanelLayout.setHorizontalGroup(
            jAddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jAddPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jAddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jAddSaveBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
                    .addComponent(jAddBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jAddCancelBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(17, Short.MAX_VALUE))
        );
        jAddPanelLayout.setVerticalGroup(
            jAddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jAddPanelLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jAddBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jAddSaveBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jAddCancelBtn)
                .addGap(15, 15, 15))
        );

        jUpdatePanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jUpdateBtn.setText("Update selected");
        jUpdateBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jUpdateBtnActionPerformed(evt);
            }
        });

        jReloadBtn.setText("Reload users");
        jReloadBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jReloadBtnActionPerformed(evt);
            }
        });

        jDeleteBtn.setText("Delete selected");
        jDeleteBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jDeleteBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jUpdatePanelLayout = new javax.swing.GroupLayout(jUpdatePanel);
        jUpdatePanel.setLayout(jUpdatePanelLayout);
        jUpdatePanelLayout.setHorizontalGroup(
            jUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jUpdatePanelLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jUpdateBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE)
                    .addComponent(jDeleteBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jReloadBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        jUpdatePanelLayout.setVerticalGroup(
            jUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jUpdatePanelLayout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addComponent(jUpdateBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jDeleteBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                .addComponent(jReloadBtn)
                .addGap(17, 17, 17))
        );

        jPersonDetailsPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPersonInfoBtn.setText("Personal details");
        jPersonInfoBtn.setName(""); // NOI18N
        jPersonInfoBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPersonInfoBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPersonDetailsPanelLayout = new javax.swing.GroupLayout(jPersonDetailsPanel);
        jPersonDetailsPanel.setLayout(jPersonDetailsPanelLayout);
        jPersonDetailsPanelLayout.setHorizontalGroup(
            jPersonDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPersonDetailsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPersonInfoBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE)
                .addGap(12, 12, 12))
        );
        jPersonDetailsPanelLayout.setVerticalGroup(
            jPersonDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPersonDetailsPanelLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jPersonInfoBtn)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jButtonsPanelLayout = new javax.swing.GroupLayout(jButtonsPanel);
        jButtonsPanel.setLayout(jButtonsPanelLayout);
        jButtonsPanelLayout.setHorizontalGroup(
            jButtonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jButtonsPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jAddPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jUpdatePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPersonDetailsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );
        jButtonsPanelLayout.setVerticalGroup(
            jButtonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jButtonsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jButtonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jAddPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jButtonsPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jUpdatePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPersonDetailsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(21, 21, 21))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jCloseBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButtonsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 848, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(45, Short.MAX_VALUE))
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
                .addGap(57, 57, 57)
                .addComponent(jCloseBtn)
                .addContainerGap(29, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
//        makeHidden();
    }//GEN-LAST:event_formWindowClosed

    private void setTableSelection(boolean isEnabled) {
        jUserTable.setRowSelectionAllowed(isEnabled);
        jUserTable.setColumnSelectionAllowed(isEnabled);
    }

    private void reloadTableData() {
        stopInserting();
        DataTableModel<User> model = getTableModel();
        // Clear table
        model.setRowCount(0);

        List<User> users = userService.getAll();

        users.forEach(user -> {
            model.addRow(user, new Object[]{user.getId(), user.getUsername(), user.getPassword(),
                    UserUtils.isStudent(user), UserUtils.isTeacher(user), UserUtils.isAdmin(user)});
        });
        updateUI(null);
//        setTableSelection(true);
    }

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        System.out.println("WINDOW OPENED");
    }//GEN-LAST:event_formWindowOpened

    private void jReloadBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRevertBtnActionPerformed
        reloadTableData();
    }//GEN-LAST:event_jRevertBtnActionPerformed

    private void jCloseBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCloseBtnActionPerformed
        getFrameManager().showParent();
    }//GEN-LAST:event_jCloseBtnActionPerformed


    private void jDeleteBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDeleteBtnActionPerformed
        if (!confirmBatchTableAction("Confirm delete", "Are you sure want to delete users:")) return;

        Arrays.stream(jUserTable.getSelectedRows()).forEach(row -> {
            userService.delete((Integer) jUserTable.getModel().getValueAt(row, 0));
//            ((DefaultTableModel) jUserTable.getModel()).removeRow(row);
//            ids.add((Long) jUserTable.getModel().getValueAt(row, 0));
        });
//        userDao.deleteUsersById(ids);
        reloadTableData();
    }//GEN-LAST:event_jDeleteBtnActionPerformed

    private boolean confirmBatchTableAction(String title, String message) {
        if (jUserTable.getSelectedRows().length == 0) {
            return false;
        }
        return JOptionPane.showConfirmDialog(this,
                message + "\n" +
                        Arrays.stream(jUserTable.getSelectedRows()).
                                mapToObj(row -> jUserTable.getModel().getValueAt(row, USER_TBL_COL.USERNAME.index).toString()).
                                collect(Collectors.joining(", ")),
                title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
    }

    private void jAddBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jAddBtnActionPerformed
        DataTableModel<User> model = getTableModel();

        model.addRow(new User(), new Object[]{null, "", "", false, false, false});
        int newRow = jUserTable.getRowCount() - 1;
        jUserTable.setRowSelectionInterval(newRow, newRow);
        startInserting();
    }//GEN-LAST:event_jAddBtnActionPerformed

    private void jUpdateBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jUpdateBtnActionPerformed
        if (!confirmBatchTableAction("Confirm update", "Are you sure want to update users:")) return;

        if (! isInserting) {
            Arrays.stream(jUserTable.getSelectedRows()).forEach(row -> {
                User user = getTableModel().getData(row);
                user.setId((Integer) jUserTable.getValueAt(row, USER_TBL_COL.ID.index));
                user.setUsername(jUserTable.getValueAt(row, USER_TBL_COL.USERNAME.index).toString());
                user.setPassword(jUserTable.getValueAt(row, USER_TBL_COL.PASSWORD.index).toString());

                fillUserRoles(row, user);

                userService.updateUserWithRoles(user);
            });
        }
    }//GEN-LAST:event_jUpdateBtnActionPerformed

    private void jAddCancelBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jAddCancelBtnActionPerformed
        stopInserting();
        // Delete last row
        ((DefaultTableModel) jUserTable.getModel()).removeRow(jUserTable.getRowCount() - 1);
    }//GEN-LAST:event_jAddCancelBtnActionPerformed

    private void jAddSaveBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jAddSaveBtnActionPerformed
        List<String> errorUsers = new ArrayList<>();
        rowsInserting.forEach(row -> {
            User newUser = getTableModel().getData(row);
            newUser.setUsername(jUserTable.getValueAt(row, USER_TBL_COL.USERNAME.index).toString());
            newUser.setPassword(jUserTable.getValueAt(row, USER_TBL_COL.PASSWORD.index).toString());

            fillUserRoles(row, newUser);

            Optional<Integer> newId = userService.insert(newUser);
            if (newId.isEmpty()) {
                errorUsers.add(newUser.getUsername());
            } else {
                jUserTable.setValueAt(newId.get(), row, USER_TBL_COL.ID.index);
//                jUserTable.setValueAt(PERSON_INFO_BTN_TITLE, row, USER_TBL_COL.PERSON_INFO.index);
            }
        });
        if (!errorUsers.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Error while inserting users: " + String.join(", ", errorUsers), "Error", JOptionPane.ERROR_MESSAGE);
        }
        stopInserting();
    }//GEN-LAST:event_jAddSaveBtnActionPerformed

    private void jPersonInfoBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPersonInfoBtnActionPerformed
        int[] rows = jUserTable.getSelectedRows();
        if (rows.length == 0) {
            return;
        }
        User user = getTableModel().getData(rows[0]);
        PersonFrame personFrame = getFrameManager().getFrame(PERSON);
//        Integer personId = null;
//        if (user.getPerson() != null) {
//            personId = user.getPerson().getId();
//        }
        userService.insertPersonToUser(user);
//        if (user.getPersonId() == null) {
//            Person person = new Person();
//            Optional<Integer> newPersonIdOpt = personService.insert(person);
//            newPersonIdOpt.ifPresent(personId -> {
//                user.setPersonId(personId);
//                userService.updateUser(user);
//            });
//        }
        personFrame.setPersonId(user.getPersonId());
        getFrameManager().showSub(PERSON);
    }//GEN-LAST:event_jPersonInfoBtnActionPerformed

    private void fillUserRoles(int row, User user) {
        if ((Boolean) jUserTable.getValueAt(row, USER_TBL_COL.IS_STUDENT.index)) {
            user.getRoles().add(Role.RoleType.STUDENT.asRole());
        }
        if ((Boolean) jUserTable.getValueAt(row, USER_TBL_COL.IS_TEACHER.index)) {
            user.getRoles().add(Role.RoleType.TEACHER.asRole());
        }
        if ((Boolean) jUserTable.getValueAt(row, USER_TBL_COL.IS_ADMIN.index)) {
            user.getRoles().add(Role.RoleType.ADMIN.asRole());
        }
    }


    private void startInserting() {
        rowsInserting.add(jUserTable.getRowCount() - 1);
        isInserting = true;
        // disable all the other buttons
//        jAddBtn.setEnabled(false);
        jAddCancelBtn.setEnabled(true);
        jAddSaveBtn.setEnabled(true);

        jUpdateBtn.setEnabled(false);
        jDeleteBtn.setEnabled(false);
//        setTableSelection(false);
    }

    private void stopInserting() {
        isInserting = false;
        // enable all the buttons etc
//        jAddBtn.setEnabled(true);
        jAddCancelBtn.setEnabled(false);
        jAddSaveBtn.setEnabled(false);

        jUpdateBtn.setEnabled(true);
        jDeleteBtn.setEnabled(true);

        rowsInserting.clear();
//        setTableSelection(true);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
//                UserFrame frame = new UserFrame();
//                AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
//                        ApplicationConfig.class
//                );
//                ApplicationContext context = SpringApplication.run(GtiRecordDesktopGuiApp.class, args);
//                ApplicationContext context = SpringApplication.run(UserFrame.class, args);
                ApplicationContext context = SpringGuiRunner.run(GtiRecordDesktopGuiApp.class, true, args);
                FrameManager manager = context.getBean(FrameManager.class);
                manager.showSub(USER);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jAddBtn;
    private javax.swing.JButton jAddCancelBtn;
    private javax.swing.JPanel jAddPanel;
    private javax.swing.JButton jAddSaveBtn;
    private javax.swing.JPanel jButtonsPanel;
    private javax.swing.JButton jCloseBtn;
    private javax.swing.JButton jDeleteBtn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPasswordField jPasswordField1;
    private javax.swing.JPanel jPersonDetailsPanel;
    private javax.swing.JButton jPersonInfoBtn;
    private javax.swing.JButton jReloadBtn;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton jUpdateBtn;
    private javax.swing.JPanel jUpdatePanel;
    private javax.swing.JTable jUserTable;
    // End of variables declaration//GEN-END:variables

}
