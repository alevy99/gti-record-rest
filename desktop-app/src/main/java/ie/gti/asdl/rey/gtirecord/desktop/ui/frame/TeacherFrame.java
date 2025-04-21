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
import ie.gti.asdl.rey.gtirecord.model.annotation.DescriptionUtil;
import ie.gti.asdl.rey.gtirecord.model.annotation.InstanceFactory;
import ie.gti.asdl.rey.gtirecord.model.entity.*;
import ie.gti.asdl.rey.gtirecord.model.entity.Module;
import ie.gti.asdl.rey.gtirecord.model.util.Pair;
import org.springframework.context.ApplicationContext;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import java.util.*;
import java.util.List;

import static ie.gti.asdl.rey.gtirecord.desktop.ui.FrameManager.FrameType.*;
import static ie.gti.asdl.rey.gtirecord.desktop.util.SwingUIUtils.createSafeListSelectionListener;

/**
 *
 * @author Andrei
 */
public class TeacherFrame extends AbstractTableDataFrame<Pair<Teacher, User>> {

    private enum COLUMNS {
        PERSON_ID(0), FIRST_NAME(1), LAST_NAME(2),
        USERNAME(3), PASSWORD(4),
        POSITION(5), DEGREE(6), WORK_EXPERIENCE(7);
//        CONTRACT_START_DATE(4), CONTRACT_END_DATE(5);

        final int index;
        COLUMNS(int index) {
            this.index = index;
        }
    }

    private final ModuleService moduleService;

    private final TeacherService teacherService;

    private final UserService userService;

    private final TeacherModuleService teacherModuleService;

    private Pair<Teacher, User> selectedPair;

    private List<Module> allModules;
    private List<Module> teacherModules;

    private Integer highlightedRow;

    /**
     * Creates new form TeacherFrame
     */
    public TeacherFrame(FrameManager frameManager, ServiceManager serviceManager) {
        super(frameManager);
        teacherService = serviceManager.getTeacherService();
        moduleService = serviceManager.getModuleService();
        teacherModuleService = serviceManager.getTeacherModuleService();
        userService = serviceManager.getUserService();
        initComponents();
        initFrame();
    }

    @Override
    protected void initFrame() {
        // Init table model first
        initTableModel();
        super.initFrame();

        tblTeacher.setHighlightedRowSupplier(() -> highlightedRow);

        TableColumnModel columnModel = getTable().getColumnModel();
        columnModel.getColumn(COLUMNS.PERSON_ID.index)      .setMaxWidth(60);
        columnModel.getColumn(COLUMNS.FIRST_NAME.index)     .setMinWidth(80);
        columnModel.getColumn(COLUMNS.LAST_NAME.index)      .setMinWidth(80);
        columnModel.getColumn(COLUMNS.USERNAME.index)       .setMinWidth(80);
        columnModel.getColumn(COLUMNS.PASSWORD.index)       .setMinWidth(80);
        columnModel.getColumn(COLUMNS.POSITION.index)       .setMaxWidth(80);
        columnModel.getColumn(COLUMNS.DEGREE.index)         .setMinWidth(80);
        columnModel.getColumn(COLUMNS.WORK_EXPERIENCE.index).setMaxWidth(60);

        tblTeacher.getColumnModel().getColumn(COLUMNS.PASSWORD.index).setCellRenderer(new PasswordCellRenderer(() -> highlightedRow));
        tblTeacher.getColumnModel().getColumn(COLUMNS.PASSWORD.index).setCellEditor(new PasswordCellEditor());

        // Init module table
        tblTeacher.getSelectionModel().addListSelectionListener(createSafeListSelectionListener(event -> onTeacherSelect()));

        initModuleTable();
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

    private void onTeacherSelect() {
        reloadTeacherModules();
        updateModulesTableUI();
    }

    private void reloadTeacherModules() {
        tblTeacherModules.clear();

        // Show modules for the first teacher
        Arrays.stream(tblTeacher.getSelectedRows()).findFirst().ifPresentOrElse(row -> {
            highlightedRow = row; // Set new highlighted row
            tblTeacher.repaint(); // Repaint after we changed highlightedRow
            selectedPair = getTableModel().getData(tblTeacher.convertRowIndexToModel(row));
            teacherModules = moduleService.getByTeacherPersonId(selectedPair.getValue1().getPerson().getId());
        }, () -> {
            if (teacherModules != null) {
                teacherModules.clear();
            }
        });
    }

    private void reloadAllModules() {
        tblAllModules.clear();
        allModules = moduleService.getAll();
    }

    private void initTableModel() {
        if (! (getTable().getModel() instanceof DataTableModel)) {
            getTable().setModel(new DataTableModel<Pair<Teacher, User>>(
                    new Object [][] {

                    },
                    new String [] {
                            "Person ID", "First name", "Last name", "Username", "Password", "Position", "Degree", "Work Experience"
                    }
            ) {
                Class[] types = new Class [] {
                        java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class
                };
                boolean[] canEdit = new boolean [] {
                        false, true, true, true, true, true, true, true
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

    private void initModuleTable() {
        ModuleFrame.initTable(tblTeacherModules);
        ModuleFrame.initTable(tblAllModules);

        SwingUIUtils.addTableFilter(tblTeacherModules, tfModuleFilter);
        SwingUIUtils.addTableFilter(tblAllModules, tfModuleFilter);

        tblTeacherModules.getSelectionModel().addListSelectionListener(createSafeListSelectionListener(listener -> updateButtonsUI()));
        tblAllModules.getSelectionModel().addListSelectionListener(createSafeListSelectionListener(listener -> updateButtonsUI()));
    }

    private void updateButtonsUI() {
        btnAddModuleToTeacher.setEnabled(tblAllModules.getSelectedRowCount() > 0);
        btnRemoveModuleFromTeacher.setEnabled(tblTeacherModules.getSelectedRowCount() > 0);
    }

    private void updateModulesTableUI() {
        lblTeacherModulesTitle.setText((selectedPair == null) || (selectedPair.getValue1() == null) ? "Teacher Modules" :
                DescriptionUtil.getShortDescription(selectedPair.getValue1().getPerson()) + " Modules");

        tblTeacherModules.clear();
        tblAllModules.clear();

        if (teacherModules != null) {
            teacherModules.forEach(module -> {
                getTeacherModulesTableModel().addRow(module, new Object[] {module.getId(), module.getName(), module.getCode()});
            });
        }
        if (allModules != null) {
            List<Module> allExceptTeacherModules = new ArrayList<>(allModules);
            if (teacherModules != null) {
                allExceptTeacherModules.removeAll(teacherModules);
            }
            allExceptTeacherModules.forEach(module -> {
                getAllModulesTableModel().addRow(module, new Object[] {module.getId(), module.getName(), module.getCode()});
            });
        }
    }

    protected DataTableModel<Module> getTeacherModulesTableModel() {
        return (DataTableModel<Module>) tblTeacherModules.getModel();
    }

    protected DataTableModel<Module> getAllModulesTableModel() {
        return (DataTableModel<Module>) tblAllModules.getModel();
    }

    @Override
    protected void onFrameShown() {
        super.onFrameShown();
        reloadAllModules();
        reloadTeacherModules();
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
        tblTeacher = new PaddedJTable();
        jTitle = new javax.swing.JLabel();
        pnlTeacherModules = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblTeacherModules = new PaddedJTable();
        lblTeacherModulesTitle = new javax.swing.JLabel();
        pnlAddRemoveModules = new javax.swing.JPanel();
        btnRemoveModuleFromTeacher = new javax.swing.JButton();
        btnAddModuleToTeacher = new javax.swing.JButton();
        pnlAllModules = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblAllModules = new PaddedJTable();
        jLabel4 = new javax.swing.JLabel();
        btnOpenModules = new javax.swing.JButton();
        pnlControls = new javax.swing.JPanel();
        btnAdd = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnReload = new javax.swing.JButton();
        jUpdatePanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        tfTeacherFilter = new javax.swing.JTextField();
        jUpdatePanel2 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        tfModuleFilter = new javax.swing.JTextField();
        btnClose = new javax.swing.JButton();
        btnPersonInfo = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jPanel1.setPreferredSize(new java.awt.Dimension(1033, 755));

        tblTeacher.setAutoCreateRowSorter(true);
        tblTeacher.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        tblTeacher.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Person ID", "First name", "Last name", "Username", "Password", "Position", "Degree", "Work Experience"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true, true, true, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblTeacher);

        jTitle.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jTitle.setForeground(new java.awt.Color(0, 51, 204));
        jTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jTitle.setText("TEACHERS");

        pnlTeacherModules.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        tblTeacherModules.setAutoCreateRowSorter(true);
        tblTeacherModules.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Module name", "Module code"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tblTeacherModules);

        lblTeacherModulesTitle.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTeacherModulesTitle.setForeground(new java.awt.Color(0, 51, 204));
        lblTeacherModulesTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTeacherModulesTitle.setText("Teacher Modules");

        javax.swing.GroupLayout pnlTeacherModulesLayout = new javax.swing.GroupLayout(pnlTeacherModules);
        pnlTeacherModules.setLayout(pnlTeacherModulesLayout);
        pnlTeacherModulesLayout.setHorizontalGroup(
            pnlTeacherModulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTeacherModulesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlTeacherModulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTeacherModulesTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlTeacherModulesLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 439, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        pnlTeacherModulesLayout.setVerticalGroup(
            pnlTeacherModulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTeacherModulesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTeacherModulesTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnRemoveModuleFromTeacher.setFont(new java.awt.Font("Monoid", 1, 14)); // NOI18N
        btnRemoveModuleFromTeacher.setForeground(new java.awt.Color(0, 0, 204));
        btnRemoveModuleFromTeacher.setText(">>>");
        btnRemoveModuleFromTeacher.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveModuleFromTeacherActionPerformed(evt);
            }
        });

        btnAddModuleToTeacher.setFont(new java.awt.Font("Monoid", 1, 14)); // NOI18N
        btnAddModuleToTeacher.setForeground(new java.awt.Color(0, 0, 204));
        btnAddModuleToTeacher.setText("<<<");
        btnAddModuleToTeacher.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddModuleToTeacherActionPerformed(evt);
            }
        });

        pnlAllModules.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        tblAllModules.setAutoCreateRowSorter(true);
        tblAllModules.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Module name", "Module code"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(tblAllModules);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 51, 204));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("All Modules");

        javax.swing.GroupLayout pnlAllModulesLayout = new javax.swing.GroupLayout(pnlAllModules);
        pnlAllModules.setLayout(pnlAllModulesLayout);
        pnlAllModulesLayout.setHorizontalGroup(
            pnlAllModulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAllModulesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlAllModulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlAllModulesLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 439, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        pnlAllModulesLayout.setVerticalGroup(
            pnlAllModulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAllModulesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnOpenModules.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnOpenModules.setForeground(new java.awt.Color(0, 51, 204));
        btnOpenModules.setText("<html><center>Open<br/>Modules</center></html>");
        btnOpenModules.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenModulesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlAddRemoveModulesLayout = new javax.swing.GroupLayout(pnlAddRemoveModules);
        pnlAddRemoveModules.setLayout(pnlAddRemoveModulesLayout);
        pnlAddRemoveModulesLayout.setHorizontalGroup(
            pnlAddRemoveModulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAddRemoveModulesLayout.createSequentialGroup()
                .addGroup(pnlAddRemoveModulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlAddRemoveModulesLayout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(pnlAddRemoveModulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnRemoveModuleFromTeacher)
                            .addComponent(btnAddModuleToTeacher)))
                    .addGroup(pnlAddRemoveModulesLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnOpenModules)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlAllModules, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlAddRemoveModulesLayout.setVerticalGroup(
            pnlAddRemoveModulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAddRemoveModulesLayout.createSequentialGroup()
                .addGap(63, 63, 63)
                .addComponent(btnRemoveModuleFromTeacher)
                .addGap(23, 23, 23)
                .addComponent(btnAddModuleToTeacher)
                .addGap(27, 27, 27)
                .addComponent(btnOpenModules, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(pnlAllModules, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
        jLabel2.setText("Teacher filter");

        tfTeacherFilter.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        tfTeacherFilter.setForeground(new java.awt.Color(0, 51, 204));

        javax.swing.GroupLayout jUpdatePanelLayout = new javax.swing.GroupLayout(jUpdatePanel);
        jUpdatePanel.setLayout(jUpdatePanelLayout);
        jUpdatePanelLayout.setHorizontalGroup(
            jUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jUpdatePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(tfTeacherFilter)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jUpdatePanelLayout.setVerticalGroup(
            jUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jUpdatePanelLayout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tfTeacherFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jUpdatePanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 3, 15)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 51, 204));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel6.setText("Module filter");

        tfModuleFilter.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        tfModuleFilter.setForeground(new java.awt.Color(0, 51, 204));

        javax.swing.GroupLayout jUpdatePanel2Layout = new javax.swing.GroupLayout(jUpdatePanel2);
        jUpdatePanel2.setLayout(jUpdatePanel2Layout);
        jUpdatePanel2Layout.setHorizontalGroup(
            jUpdatePanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jUpdatePanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jUpdatePanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(tfModuleFilter)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jUpdatePanel2Layout.setVerticalGroup(
            jUpdatePanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jUpdatePanel2Layout.createSequentialGroup()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tfModuleFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(pnlTeacherModules, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlAddRemoveModules, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(15, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(pnlControls, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnPersonInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnClose, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addGap(24, 24, 24))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addComponent(jTitle)
                .addGap(12, 12, 12)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 338, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlControls, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(btnPersonInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(2, 2, 2)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlTeacherModules, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlAddRemoveModules, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
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
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 812, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddModuleToTeacherActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddModuleToTeacherActionPerformed
        if ((selectedPair == null)
                || (selectedPair.getValue1() == null)
                || (selectedPair.getValue1().getPerson() == null)
                || (selectedPair.getValue1().getPerson().getId() == null)
                || (teacherModules == null)
                || (tblAllModules.getSelectedRowCount() == 0)) {
            return;
        }

        Arrays.stream(tblAllModules.getSelectedRows()).forEach(row -> {
            int modelRow = tblAllModules.convertRowIndexToModel(row);
            Module module = getAllModulesTableModel().getData(modelRow);
            if ((module != null) && (module.getId() != null)) {
                teacherModuleService.insert(selectedPair.getValue1().getPerson().getId(), module.getId());
                getTeacherModulesTableModel().addRow(module, new Object[] {module.getId(), module.getName(), module.getCode()});
                teacherModules.add(module);
            }
        });
        updateModulesTableUI();
    }//GEN-LAST:event_btnAddModuleToTeacherActionPerformed

    private void btnRemoveModuleFromTeacherActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveModuleFromTeacherActionPerformed
        if ((selectedPair == null)
                || (selectedPair.getValue1() == null)
                || (selectedPair.getValue1().getPerson() == null)
                || (selectedPair.getValue1().getPerson().getId() == null)
                || (teacherModules == null)
                || (tblTeacherModules.getSelectedRowCount() == 0)) {
            return;
        }

        List<Integer> deletedModelRows = new ArrayList<>();

        Arrays.stream(tblTeacherModules.getSelectedRows()).forEach(row -> {
            int modelRow = tblTeacherModules.convertRowIndexToModel(row);
            Module module = getTeacherModulesTableModel().getData(modelRow);
            if ((module != null) && (module.getId() != null)) {
                teacherModuleService.delete(selectedPair.getValue1().getPerson().getId(), module.getId());
                // Collect deleted rows, since we can't change model here,
                // as RowSorter use it to map viewRows to Model rows properly
                deletedModelRows.add(modelRow);
                teacherModules.remove(module);
            }
        });
        // Sort in reverse order, so we will delete from the last to the first
        deletedModelRows.sort(Comparator.reverseOrder());
        deletedModelRows.forEach(modelRow -> getTeacherModulesTableModel().removeRow(modelRow));

        updateModulesTableUI();

    }//GEN-LAST:event_btnRemoveModuleFromTeacherActionPerformed

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

    private void btnOpenModulesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenModulesActionPerformed
        getFrameManager().showSub(MODULE);
    }//GEN-LAST:event_btnOpenModulesActionPerformed

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
                manager.showSub(TEACHER);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnAddModuleToTeacher;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnOpenModules;
    private javax.swing.JButton btnPersonInfo;
    private javax.swing.JButton btnReload;
    private javax.swing.JButton btnRemoveModuleFromTeacher;
    private javax.swing.JButton btnSave;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel jTitle;
    private javax.swing.JPanel jUpdatePanel;
    private javax.swing.JPanel jUpdatePanel2;
    private javax.swing.JLabel lblTeacherModulesTitle;
    private javax.swing.JPanel pnlAddRemoveModules;
    private javax.swing.JPanel pnlAllModules;
    private javax.swing.JPanel pnlControls;
    private javax.swing.JPanel pnlTeacherModules;
    private PaddedJTable tblAllModules;
    private PaddedJTable tblTeacher;
    private PaddedJTable tblTeacherModules;
    private javax.swing.JTextField tfModuleFilter;
    private javax.swing.JTextField tfTeacherFilter;
    // End of variables declaration//GEN-END:variables

    @Override
    protected PaddedJTable getTable() {
        return tblTeacher;
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
        return tfTeacherFilter;
    }

    @Override
    protected int getDataDescriptionColumn() {
        return COLUMNS.FIRST_NAME.index;
    }

    @Override
    protected Pair<Teacher, User> createDataInstance() {
//        var teacher = new Teacher();
//        teacher.setPerson(new Person());
//        User user = new User();
//        user.getRoles().add(Role.RoleType.TEACHER.asRole());
//        return new Pair<>(teacher, user);
        var teacher = InstanceFactory.create(Teacher.class);
        var user = InstanceFactory.create(User.class);
        user.getRoles().add(Role.RoleType.TEACHER.asRole());
        return new Pair<>(teacher, user);
    }

    @Override
    protected void doReloadData() {
        teacherService.getAll().forEach(teacher -> {
            Pair<Teacher, User> pair = new Pair<>(teacher, null);
            userService.getByPersonId(teacher.getPerson().getId()).ifPresentOrElse(user -> {
                user.setPersonId(teacher.getPerson().getId());
                pair.setValue2(user);
            }, () -> {
                User user = new User();
                user.setPersonId(teacher.getPerson().getId());
                user.getRoles().add(Role.RoleType.TEACHER.asRole());
                pair.setValue2(user);
            });

            getTableModel().addRow(pair, new Object[]{teacher.getPerson().getId(),
                    teacher.getPerson().getFirstName(), teacher.getPerson().getLastName(),
                    pair.getValue2().getUsername(), pair.getValue2().getPassword(),
                    teacher.getPosition(), teacher.getDegree(),
                    teacher.getWorkExperience()
            });
        });

        if (getTableModel().getDataList().contains(selectedPair)) {
            int viewRow = tblTeacher.convertRowIndexToView(getTableModel().getDataList().indexOf(selectedPair));
            tblTeacher.setRowSelectionInterval(viewRow, viewRow);
        } else {
            selectedPair = null;
        }
    }

    @Override
    protected Optional<Integer> doInsertData(Pair<Teacher, User> pair) {
        if (pair == null) return Optional.empty();
        return teacherService.saveWithUser(pair.getValue1(), pair.getValue2());
    }

    @Override
    protected void doUpdateData(Pair<Teacher, User> pair) {
        if (pair == null) return;
        teacherService.saveWithUser(pair.getValue1(), pair.getValue2());
    }

    @Override
    protected void doDeleteData(Integer dataId) {
        if (dataId != null) {
            teacherService.delete(dataId);
        }
    }

    @Override
    protected boolean isDataValid(Pair<Teacher, User> pair) {
        return (pair != null);
//                && (teacher.getName() != null) && ! teacher.getName().isBlank()
//                && teacher.getDepartment() != null && teacher.getCourseType() != null && teacher.getQqiLevel() != null;
    }

    @Override
    protected void fillDataObjectFromTable(Pair<Teacher, User> pair, Integer row) {
        Teacher teacher = pair.getValue1();
        if (getTable().getValueAt(row, COLUMNS.PERSON_ID.index) instanceof Integer id) {
            teacher.getPerson().setId(id);
        }
        teacher.getPerson().setFirstName(getTableStringValueAt(row, COLUMNS.FIRST_NAME.index));
        teacher.getPerson().setLastName(getTableStringValueAt(row, COLUMNS.LAST_NAME.index));
        teacher.setPosition(getTableStringValueAt(row, COLUMNS.POSITION.index));
        teacher.setDegree(getTableStringValueAt(row, COLUMNS.DEGREE.index));
        teacher.setWorkExperience((Integer) getTable().getValueAt(row, COLUMNS.WORK_EXPERIENCE.index));

        User user = pair.getValue2();
        user.setUsername(getTableStringValueAt(row, COLUMNS.USERNAME.index));
        user.setPassword(getTableStringValueAt(row, COLUMNS.PASSWORD.index));
    }

    @Override
    protected void addEmptyRowToModel() {
        getTableModel().addRow(createDataInstance(), new Object[]{null, "", "", "", "", "", "", null});
    }

}
