/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ie.gti.asdl.rey.gtirecord.desktop.ui.frame;

import ie.gti.asdl.rey.gtirecord.core.service.CourseModuleService;
import ie.gti.asdl.rey.gtirecord.core.service.CourseService;
import ie.gti.asdl.rey.gtirecord.core.service.DepartmentService;
import ie.gti.asdl.rey.gtirecord.core.ServiceManager;
import ie.gti.asdl.rey.gtirecord.core.service.ModuleService;
import ie.gti.asdl.rey.gtirecord.desktop.GtiRecordDesktopGuiApp;
import ie.gti.asdl.rey.gtirecord.desktop.ui.AbstractTableDataFrame;
import ie.gti.asdl.rey.gtirecord.desktop.ui.FrameManager;
import ie.gti.asdl.rey.gtirecord.desktop.ui.comp.*;
import ie.gti.asdl.rey.gtirecord.desktop.util.SpringGuiRunner;
import ie.gti.asdl.rey.gtirecord.desktop.util.SwingUIUtils;
import ie.gti.asdl.rey.gtirecord.model.entity.*;
import ie.gti.asdl.rey.gtirecord.model.entity.Module;
import org.springframework.context.ApplicationContext;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static ie.gti.asdl.rey.gtirecord.desktop.ui.FrameManager.FrameType.COURSE;
import static ie.gti.asdl.rey.gtirecord.desktop.ui.FrameManager.FrameType.MODULE;
import static ie.gti.asdl.rey.gtirecord.desktop.util.SwingUIUtils.confirmBatchTableAction;

/**
 *
 * @author Andrei
 */
public class CourseFrame extends AbstractTableDataFrame<Course> {

    enum COLUMNS {
        ID(0), NAME(1), CODE(2),
        DEPARTMENT(3), TYPE(4), QQI_LEVEL(5);

        final int index;
        COLUMNS(int index) {
            this.index = index;
        }
    }

    private final JComboBox<Department> departmentCombo = new JComboBox<>();

    private final JComboBox<CourseType> courseTypeCombo = new JComboBox<>();

    private final JComboBox<QQILevel> qqiLevelCombo = new JComboBox<>();

    private final ModuleService moduleService;

    private final CourseService courseService;

    private final CourseModuleService courseModuleService;

    private final DepartmentService departmentService;

    private Course selectedCourse;

    /**
     * Creates new form CourseFrame
     */
    public CourseFrame(FrameManager frameManager, ServiceManager serviceManager) {
        super(frameManager);
        courseService = serviceManager.getCourseService();
        departmentService = serviceManager.getDepartmentService();
        moduleService = serviceManager.getModuleService();
        courseModuleService = serviceManager.getCourseModuleService();
        initComponents();
        initForm();
    }

    @Override
    protected void initForm() {
        // Init table model first
        initTableModel();
        super.initForm();

        // Set Department custom JComboBox Renderer and Editor
        TableColumn departmentColumn = getTable().getColumnModel().getColumn(COLUMNS.DEPARTMENT.index);
        departmentColumn.setCellEditor(new DefaultCellEditor(departmentCombo));
        departmentColumn.setCellRenderer(new PaddedDataCellRenderer());

        // Set Course Type custom JComboBox Renderer and Editor
        TableColumn courseTypeColumn = getTable().getColumnModel().getColumn(COLUMNS.TYPE.index);
        courseTypeColumn.setCellEditor(new DefaultCellEditor(courseTypeCombo));
        courseTypeColumn.setCellRenderer(new PaddedDataCellRenderer());

        // Set QQI level custom JComboBox Renderer and Editor
        TableColumn qqiLevelColumn = getTable().getColumnModel().getColumn(COLUMNS.QQI_LEVEL.index);
        qqiLevelColumn.setCellEditor(new DefaultCellEditor(qqiLevelCombo));
        qqiLevelColumn.setCellRenderer(new PaddedDataCellRenderer());

        TableColumnModel columnModel = getTable().getColumnModel();
        columnModel.getColumn(COLUMNS.ID.index)         .setMaxWidth(35);
        columnModel.getColumn(COLUMNS.NAME.index)       .setMinWidth(160);
        columnModel.getColumn(COLUMNS.CODE.index)       .setMaxWidth(70);
        columnModel.getColumn(COLUMNS.DEPARTMENT.index) .setMinWidth(120);
        columnModel.getColumn(COLUMNS.TYPE.index)       .setMaxWidth(80);
        columnModel.getColumn(COLUMNS.QQI_LEVEL.index)  .setMinWidth(80);

        // Init module table
        tblCourse.getSelectionModel().addListSelectionListener(this::onCourseSelect);

        initModuleTable();
    }

    private void onCourseSelect(ListSelectionEvent listSelectionEvent) {
        tblModule.clear();

        // Show modules for the first course
        Arrays.stream(getTable().getSelectedRows()).findFirst().ifPresent(row -> {
            selectedCourse = getTableModel().getData(row);
            List<Module> modules = moduleService.getByCourseId(selectedCourse.getId());
            modules.forEach(module -> {
                getModuleTableModel().addRow(module, new Object[] {module.getId(), module.getName(), module.getCode()});
            });
        });
    }

    private void reloadModules() {
        onCourseSelect(null);
    }

    private void initTableModel() {
        if (! (getTable().getModel() instanceof DataTableModel)) {
            getTable().setModel(new DataTableModel<Course>(
                    new Object[][]{
                            {null, null, null, null, null, null}
                    },
                    new String[]{
                            "ID", "Name", "Code", "Department", "Type", "QQI Level"
                    }
            ) {
                Class[] types = new Class[]{
                        java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
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

    private void initModuleTable() {
        ModuleFrame.initTable(tblModule);

        SwingUIUtils.addTableFilter(tblModule, tfModuleTableFilter);

        tblModule.getSelectionModel().addListSelectionListener(this::updateModuleUI);
    }

    private void updateModuleUI(ListSelectionEvent listSelectionEvent) {
        btnDeleteModule.setEnabled(tblModule.getSelectedRowCount() > 0);
    }

    protected DataTableModel<Module> getModuleTableModel() {
        return (DataTableModel<Module>) tblModule.getModel();
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
        tblCourse = new PaddedJTable();
        jAddPanel = new javax.swing.JPanel();
        jAddBtn = new javax.swing.JButton();
        jAddCancelBtn = new javax.swing.JButton();
        jAddSaveBtn = new javax.swing.JButton();
        jUpdatePanel = new javax.swing.JPanel();
        jUpdateBtn = new javax.swing.JButton();
        jRevertBtn = new javax.swing.JButton();
        jDeleteBtn = new javax.swing.JButton();
        jTitle = new javax.swing.JLabel();
        pnlTableFilter = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        tfTableFilter = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblModule = new PaddedJTable();
        jLabel2 = new javax.swing.JLabel();
        btnAddModule = new javax.swing.JButton();
        btnDeleteModule = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        pnlTableFilter1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        tfModuleTableFilter = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        tblCourse.setAutoCreateRowSorter(true);
        tblCourse.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        tblCourse.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Course name", "Course code", "Department", "Type", "QQI Level"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
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
        jScrollPane1.setViewportView(tblCourse);

        jAddPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jAddBtn.setText("Add new");
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

        jAddSaveBtn.setText("Save new");
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
                .addGap(15, 15, 15)
                .addGroup(jAddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jAddSaveBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                    .addComponent(jAddBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jAddCancelBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jAddPanelLayout.setVerticalGroup(
            jAddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jAddPanelLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jAddBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jAddSaveBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jAddCancelBtn)
                .addGap(18, 18, 18))
        );

        jUpdatePanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jUpdateBtn.setText("Update selected");
        jUpdateBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jUpdateBtnActionPerformed(evt);
            }
        });

        jRevertBtn.setText("Reload courses");
        jRevertBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRevertBtnActionPerformed(evt);
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
                .addGap(15, 15, 15)
                .addGroup(jUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jDeleteBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jUpdateBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)
                    .addComponent(jRevertBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(15, Short.MAX_VALUE))
        );
        jUpdatePanelLayout.setVerticalGroup(
            jUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jUpdatePanelLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jUpdateBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jDeleteBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                .addComponent(jRevertBtn)
                .addGap(17, 17, 17))
        );

        jTitle.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jTitle.setText("COURSES");

        pnlTableFilter.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlTableFilter.setPreferredSize(new java.awt.Dimension(197, 150));

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Course Filter");

        tfTableFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfTableFilterActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlTableFilterLayout = new javax.swing.GroupLayout(pnlTableFilter);
        pnlTableFilter.setLayout(pnlTableFilterLayout);
        pnlTableFilterLayout.setHorizontalGroup(
            pnlTableFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTableFilterLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(pnlTableFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfTableFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 296, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15))
        );
        pnlTableFilterLayout.setVerticalGroup(
            pnlTableFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTableFilterLayout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tfTableFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(17, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        tblModule.setAutoCreateRowSorter(true);
        tblModule.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
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
        jScrollPane2.setViewportView(tblModule);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Course Modules");

        btnAddModule.setText("Add module");
        btnAddModule.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddModuleActionPerformed(evt);
            }
        });

        btnDeleteModule.setText("Delete module");
        btnDeleteModule.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteModuleActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 439, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btnAddModule, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnDeleteModule, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAddModule, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDeleteModule, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(23, Short.MAX_VALUE))
        );

        btnClose.setText("Close");
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });

        pnlTableFilter1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlTableFilter1.setPreferredSize(new java.awt.Dimension(197, 150));

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Module Filter");

        tfModuleTableFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfModuleTableFilterActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlTableFilter1Layout = new javax.swing.GroupLayout(pnlTableFilter1);
        pnlTableFilter1.setLayout(pnlTableFilter1Layout);
        pnlTableFilter1Layout.setHorizontalGroup(
            pnlTableFilter1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTableFilter1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(pnlTableFilter1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfModuleTableFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 296, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlTableFilter1Layout.setVerticalGroup(
            pnlTableFilter1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTableFilter1Layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tfModuleTableFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(17, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 797, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jAddPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jUpdatePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(pnlTableFilter, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 338, Short.MAX_VALUE)
                            .addComponent(pnlTableFilter1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 338, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jTitle)
                .addGap(27, 27, 27)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jAddPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jUpdatePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(12, 12, 12)
                        .addComponent(pnlTableFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(pnlTableFilter1, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        getFrameManager().showParent();
    }//GEN-LAST:event_btnCloseActionPerformed

    private void jAddBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jAddBtnActionPerformed
        onAddData();
    }//GEN-LAST:event_jAddBtnActionPerformed

    private void jAddCancelBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jAddCancelBtnActionPerformed
        onCancelAddData();
    }//GEN-LAST:event_jAddCancelBtnActionPerformed

    private void jAddSaveBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jAddSaveBtnActionPerformed
        onAddSaveData();
    }//GEN-LAST:event_jAddSaveBtnActionPerformed

    private void jUpdateBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jUpdateBtnActionPerformed
        onUpdateData();
    }//GEN-LAST:event_jUpdateBtnActionPerformed

    private void jRevertBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRevertBtnActionPerformed
        reloadTableData();
    }//GEN-LAST:event_jRevertBtnActionPerformed

    private void jDeleteBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDeleteBtnActionPerformed
        onDeleteData();
    }//GEN-LAST:event_jDeleteBtnActionPerformed

    private void tfTableFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfTableFilterActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfTableFilterActionPerformed

    private void btnDeleteModuleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteModuleActionPerformed
        if (!confirmBatchTableAction(this, tblModule, ModuleFrame.COLUMNS.NAME.index,
                "Confirm delete", "Are you sure want to delete modules:")) {
            return;
        }

        Arrays.stream(tblModule.getSelectedRows()).forEach(row -> {
            courseModuleService.delete(
                    selectedCourse.getId(),
                    (Integer) tblModule.getValueAt(row, ModuleFrame.COLUMNS.ID.index));
        });

        reloadModules();
    }//GEN-LAST:event_btnDeleteModuleActionPerformed

    private void tfModuleTableFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfModuleTableFilterActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfModuleTableFilterActionPerformed

    private void btnAddModuleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddModuleActionPerformed
        if (selectedCourse == null) {
            return;
        }
        ModuleFrame moduleFrame = getFrameManager().getFrame(MODULE);

        moduleFrame.reset();

        ModuleFrame.ModuleFilter moduleFilter = moduleFrame.getModuleFilter();
        moduleFilter.setCourse(selectedCourse);

        Arrays.stream(tblModule.getSelectedRows()).forEach(row -> {
            moduleFilter.getExceptModules().add(getModuleTableModel().getData(row));
        });

        moduleFrame.setSelectionMode(true);

        getFrameManager().showSub(MODULE);

//        moduleFrame.getModuleFilter().getExceptModules().addAll(tblModule.getSelectedRows());
    }//GEN-LAST:event_btnAddModuleActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ApplicationContext context = SpringGuiRunner.run(GtiRecordDesktopGuiApp.class, args);
                FrameManager manager = context.getBean(FrameManager.class);
                manager.showSub(COURSE);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddModule;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnDeleteModule;
    private javax.swing.JButton jAddBtn;
    private javax.swing.JButton jAddCancelBtn;
    private javax.swing.JPanel jAddPanel;
    private javax.swing.JButton jAddSaveBtn;
    private javax.swing.JButton jDeleteBtn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JButton jRevertBtn;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel jTitle;
    private javax.swing.JButton jUpdateBtn;
    private javax.swing.JPanel jUpdatePanel;
    private javax.swing.JPanel pnlTableFilter;
    private javax.swing.JPanel pnlTableFilter1;
    private PaddedJTable tblCourse;
    private PaddedJTable tblModule;
    private javax.swing.JTextField tfModuleTableFilter;
    private javax.swing.JTextField tfTableFilter;
    // End of variables declaration//GEN-END:variables

    @Override
    protected PaddedJTable getTable() {
        return tblCourse;
    }

    @Override
    protected JButton getAddBtn() {
        return jAddBtn;
    }

    @Override
    protected JButton getDeleteBtn() {
        return jDeleteBtn;
    }

    @Override
    protected JButton getUpdateBtn() {
        return jUpdateBtn;
    }

    @Override
    protected JButton getAddCancelBtn() {
        return jAddCancelBtn;
    }

    @Override
    protected JButton getAddSaveBtn() {
        return jAddSaveBtn;
    }

    @Override
    protected JTextField getTableFilterField() {
        return tfTableFilter;
    }

    @Override
    protected int getDataDescriptionColumn() {
        return COLUMNS.NAME.index;
    }

    @Override
    protected Course createDataInstance() {
        return new Course();
    }

    @Override
    protected void doReloadData() {
        departmentCombo.removeAllItems();
        departmentService.getAll().forEach(departmentCombo::addItem);

        qqiLevelCombo.removeAllItems();
        qqiLevelCombo.addItem(QQILevel.QQILevelType.QQI5.asQQILevel());
        qqiLevelCombo.addItem(QQILevel.QQILevelType.QQI6.asQQILevel());

        courseTypeCombo.removeAllItems();
        courseTypeCombo.addItem(CourseType.CourseTypeType.FULL_TIME.asCourseType());
        courseTypeCombo.addItem(CourseType.CourseTypeType.PART_TIME.asCourseType());
        courseTypeCombo.addItem(CourseType.CourseTypeType.ONLINE.asCourseType());
        courseTypeCombo.addItem(CourseType.CourseTypeType.EVENING.asCourseType());

//        courseService.getAllGroupedByDepartment().forEach((department, courses) -> {
////            String comboItem = department.getName();
//            courses.forEach(course -> {
//                departmentCombo.addItem(course);
//            });
//        });

        courseService.getAll().forEach(course -> {
            getTableModel().addRow(course, new Object[]{
                    course.getId(), course.getName(), course.getCode(),
                    course.getDepartment(), course.getCourseType(), course.getQqiLevel()
            });
        });
    }

    @Override
    protected Optional<Integer> doInsertData(Course data) {
        return courseService.insert(data);
    }

    @Override
    protected void doUpdateData(Course data) {
        courseService.update(data);
    }

    @Override
    protected void doDeleteData(int dataId) {
        courseService.delete(dataId);
    }

    @Override
    protected boolean isDataValid(Course data) {
        return (data != null) && (data.getName() != null) && ! data.getName().isBlank()
                && data.getDepartment() != null && data.getCourseType() != null && data.getQqiLevel() != null;
    }

    @Override
    protected void fillDataObjectFromTable(Course course, Integer row) {
        if (getTable().getValueAt(row, COLUMNS.ID.index) instanceof Integer id) {
            course.setId(id);
        }

        course.setName(getTable().getValueAt(row, COLUMNS.NAME.index).toString());
        course.setCode(getTable().getValueAt(row, COLUMNS.CODE.index).toString());

        Department department = (Department) getTable().getValueAt(row, COLUMNS.DEPARTMENT.index);
        if (department != null) {
            course.setDepartment(department);
        }

        CourseType courseType = (CourseType) getTable().getValueAt(row, COLUMNS.TYPE.index);
        if (courseType != null) {
            course.setCourseType(courseType);
        }

        QQILevel qqiLevel = (QQILevel) getTable().getValueAt(row, COLUMNS.QQI_LEVEL.index);
        if (qqiLevel != null) {
            course.setQqiLevel(qqiLevel);
        }
    }

    @Override
    protected void addEmptyRowToModel() {
        getTableModel().addRow(createDataInstance(), new Object[]{null, "", "", null, null, null});
    }

}
