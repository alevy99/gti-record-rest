/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ie.gti.asdl.rey.gtirecord.desktop.ui.frame;

import ie.gti.asdl.rey.gtirecord.core.ServiceManager;
import ie.gti.asdl.rey.gtirecord.core.service.CourseModuleService;
import ie.gti.asdl.rey.gtirecord.core.service.CourseService;
import ie.gti.asdl.rey.gtirecord.core.service.DepartmentService;
import ie.gti.asdl.rey.gtirecord.core.service.ModuleService;
import ie.gti.asdl.rey.gtirecord.desktop.GtiRecordDesktopGuiApp;
import ie.gti.asdl.rey.gtirecord.desktop.ui.AbstractTableDataFrame;
import ie.gti.asdl.rey.gtirecord.desktop.ui.FrameManager;
import ie.gti.asdl.rey.gtirecord.desktop.ui.component.*;
import ie.gti.asdl.rey.gtirecord.desktop.util.SpringGuiRunner;
import ie.gti.asdl.rey.gtirecord.desktop.util.SwingUIUtils;
import ie.gti.asdl.rey.gtirecord.model.entity.*;
import ie.gti.asdl.rey.gtirecord.model.entity.Module;
import org.springframework.context.ApplicationContext;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.util.*;
import java.util.List;

import static ie.gti.asdl.rey.gtirecord.desktop.ui.FrameManager.FrameType.COURSE;
import static ie.gti.asdl.rey.gtirecord.desktop.ui.FrameManager.FrameType.MODULE;
import static ie.gti.asdl.rey.gtirecord.desktop.util.SwingUIUtils.createSafeListener;

/**
 *
 * @author Andrei
 */
public class CourseFrame extends AbstractTableDataFrame<Course> {

    private enum COLUMNS {
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

    List<Module> allModules;
    List<Module> courseModules;

    private Integer highlightedRow;

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
        initFrame();
    }

    @Override
    protected void initFrame() {
        // Init table model first
        initTableModel();
        super.initFrame();

        tblCourse.setHighlightedRowSupplier(() -> highlightedRow);

        DataListCellRendered listCellRendered = new DataListCellRendered();
        PaddedDataCellRenderer dataCellRenderer = new PaddedDataCellRenderer(() -> highlightedRow);

        // Set Department custom JComboBox Renderer and Editor
        TableColumn departmentColumn = getTable().getColumnModel().getColumn(COLUMNS.DEPARTMENT.index);
        departmentCombo.setRenderer(listCellRendered);
        departmentColumn.setCellEditor(new DefaultCellEditor(departmentCombo));
        departmentColumn.setCellRenderer(dataCellRenderer);

        // Set Course Type custom JComboBox Renderer and Editor
        TableColumn courseTypeColumn = getTable().getColumnModel().getColumn(COLUMNS.TYPE.index);
        courseTypeCombo.setRenderer(listCellRendered);
        courseTypeColumn.setCellEditor(new DefaultCellEditor(courseTypeCombo));
        courseTypeColumn.setCellRenderer(dataCellRenderer);

        // Set QQI level custom JComboBox Renderer and Editor
        TableColumn qqiLevelColumn = getTable().getColumnModel().getColumn(COLUMNS.QQI_LEVEL.index);
        qqiLevelCombo.setRenderer(listCellRendered);
        qqiLevelColumn.setCellEditor(new DefaultCellEditor(qqiLevelCombo));
        qqiLevelColumn.setCellRenderer(dataCellRenderer);

        TableColumnModel columnModel = getTable().getColumnModel();
        columnModel.getColumn(COLUMNS.ID.index)         .setMaxWidth(35);
        columnModel.getColumn(COLUMNS.NAME.index)       .setMinWidth(160);
        columnModel.getColumn(COLUMNS.CODE.index)       .setMaxWidth(70);
        columnModel.getColumn(COLUMNS.DEPARTMENT.index) .setMinWidth(120);
        columnModel.getColumn(COLUMNS.TYPE.index)       .setMaxWidth(80);
        columnModel.getColumn(COLUMNS.QQI_LEVEL.index)  .setMinWidth(80);

        // Init module table
        tblCourse.getSelectionModel().addListSelectionListener(createSafeListener(event -> onCourseSelect()));

        initModuleTable();
    }

    private void onCourseSelect() {
        reloadCourseModules();
        updateModulesTableUI();
    }

    private void reloadCourseModules() {
        tblCourseModules.clear();

        // Show modules for the first course
        Arrays.stream(tblCourse.getSelectedRows()).findFirst().ifPresentOrElse(row -> {
            highlightedRow = row; // Set new highlighted row
            tblCourse.repaint(); // Repaint after we changed highlightedRow
            selectedCourse = getTableModel().getData(tblCourse.convertRowIndexToModel(row));
            courseModules = moduleService.getByCourseId(selectedCourse.getId());
        }, () -> {
            if (courseModules != null) {
                courseModules.clear();
            }
        });
    }

    private void reloadAllModules() {
        tblAllModules.clear();
        allModules = moduleService.getAll();
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
        ModuleFrame.initTable(tblCourseModules);
        ModuleFrame.initTable(tblAllModules);

        SwingUIUtils.addTableFilter(tblCourseModules, tfModuleFilter);
        SwingUIUtils.addTableFilter(tblAllModules, tfModuleFilter);

        tblCourseModules.getSelectionModel().addListSelectionListener(createSafeListener(listener -> updateButtonsUI()));
        tblAllModules.getSelectionModel().addListSelectionListener(createSafeListener(listener -> updateButtonsUI()));
    }

    private void updateButtonsUI() {
        btnAddModuleToCourse.setEnabled(tblAllModules.getSelectedRowCount() > 0);
        btnRemoveModuleFromCourse.setEnabled(tblCourseModules.getSelectedRowCount() > 0);
    }

    private void updateModulesTableUI() {
        lblCourseModulesTitle.setText((selectedCourse == null) ? "Course Modules" : selectedCourse.getName() + " Modules");

        tblCourseModules.clear();
        tblAllModules.clear();

        if (courseModules != null) {
            courseModules.forEach(module -> {
                getCourseModulesTableModel().addRow(module, new Object[] {module.getId(), module.getName(), module.getCode()});
            });
        }
        if (allModules != null) {
            List<Module> allExceptCourseModules = new ArrayList<>(allModules);
            if (courseModules != null) {
                allExceptCourseModules.removeAll(courseModules);
            }
            allExceptCourseModules.forEach(module -> {
                getAllModulesTableModel().addRow(module, new Object[] {module.getId(), module.getName(), module.getCode()});
            });
        }
    }

    protected DataTableModel<Module> getCourseModulesTableModel() {
        return (DataTableModel<Module>) tblCourseModules.getModel();
    }

    protected DataTableModel<Module> getAllModulesTableModel() {
        return (DataTableModel<Module>) tblAllModules.getModel();
    }

    @Override
    protected void onFrameShown() {
        super.onFrameShown();
        reloadAllModules();
        reloadCourseModules();
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
        tblCourse = new PaddedJTable();
        jTitle = new javax.swing.JLabel();
        pnlCourseModules = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblCourseModules = new PaddedJTable();
        lblCourseModulesTitle = new javax.swing.JLabel();
        pnlAddRemoveModules = new javax.swing.JPanel();
        btnRemoveModuleFromCourse = new javax.swing.JButton();
        btnAddModuleToCourse = new javax.swing.JButton();
        pnlAllModules = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblAllModules = new PaddedJTable();
        jLabel4 = new javax.swing.JLabel();
        pnlControls = new javax.swing.JPanel();
        btnAdd = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnReload = new javax.swing.JButton();
        jUpdatePanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        tfCourseFilter = new javax.swing.JTextField();
        jUpdatePanel2 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        tfModuleFilter = new javax.swing.JTextField();
        btnOpenModules = new javax.swing.JButton();
        btnClose1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jPanel1.setPreferredSize(new java.awt.Dimension(1033, 755));

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

        jTitle.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jTitle.setForeground(new java.awt.Color(0, 51, 204));
        jTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jTitle.setText("COURSES");

        pnlCourseModules.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        tblCourseModules.setAutoCreateRowSorter(true);
        tblCourseModules.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(tblCourseModules);

        lblCourseModulesTitle.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblCourseModulesTitle.setForeground(new java.awt.Color(0, 51, 204));
        lblCourseModulesTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCourseModulesTitle.setText("Course Modules");

        javax.swing.GroupLayout pnlCourseModulesLayout = new javax.swing.GroupLayout(pnlCourseModules);
        pnlCourseModules.setLayout(pnlCourseModulesLayout);
        pnlCourseModulesLayout.setHorizontalGroup(
            pnlCourseModulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCourseModulesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlCourseModulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblCourseModulesTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlCourseModulesLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 439, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        pnlCourseModulesLayout.setVerticalGroup(
            pnlCourseModulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCourseModulesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblCourseModulesTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnRemoveModuleFromCourse.setFont(new java.awt.Font("Monoid", 1, 14)); // NOI18N
        btnRemoveModuleFromCourse.setForeground(new java.awt.Color(0, 0, 204));
        btnRemoveModuleFromCourse.setText(">>>");
        btnRemoveModuleFromCourse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveModuleFromCourseActionPerformed(evt);
            }
        });

        btnAddModuleToCourse.setFont(new java.awt.Font("Monoid", 1, 14)); // NOI18N
        btnAddModuleToCourse.setForeground(new java.awt.Color(0, 0, 204));
        btnAddModuleToCourse.setText("<<<");
        btnAddModuleToCourse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddModuleToCourseActionPerformed(evt);
            }
        });

        pnlAllModules.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        tblAllModules.setAutoCreateRowSorter(true);
        tblAllModules.setModel(new javax.swing.table.DefaultTableModel(
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

        javax.swing.GroupLayout pnlAddRemoveModulesLayout = new javax.swing.GroupLayout(pnlAddRemoveModules);
        pnlAddRemoveModules.setLayout(pnlAddRemoveModulesLayout);
        pnlAddRemoveModulesLayout.setHorizontalGroup(
            pnlAddRemoveModulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAddRemoveModulesLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(pnlAddRemoveModulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnRemoveModuleFromCourse)
                    .addComponent(btnAddModuleToCourse))
                .addGap(18, 18, 18)
                .addComponent(pnlAllModules, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlAddRemoveModulesLayout.setVerticalGroup(
            pnlAddRemoveModulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAddRemoveModulesLayout.createSequentialGroup()
                .addGap(96, 96, 96)
                .addComponent(btnRemoveModuleFromCourse)
                .addGap(38, 38, 38)
                .addComponent(btnAddModuleToCourse)
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
        jLabel2.setText("Course filter");

        tfCourseFilter.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        tfCourseFilter.setForeground(new java.awt.Color(0, 51, 204));

        javax.swing.GroupLayout jUpdatePanelLayout = new javax.swing.GroupLayout(jUpdatePanel);
        jUpdatePanel.setLayout(jUpdatePanelLayout);
        jUpdatePanelLayout.setHorizontalGroup(
            jUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jUpdatePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(tfCourseFilter)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jUpdatePanelLayout.setVerticalGroup(
            jUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jUpdatePanelLayout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tfCourseFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
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

        btnOpenModules.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnOpenModules.setForeground(new java.awt.Color(0, 51, 204));
        btnOpenModules.setText("Manage Modules");
        btnOpenModules.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenModulesActionPerformed(evt);
            }
        });

        btnClose1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnClose1.setForeground(new java.awt.Color(0, 51, 204));
        btnClose1.setText("Close");
        btnClose1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClose1ActionPerformed(evt);
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
                        .addComponent(pnlCourseModules, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlAddRemoveModules, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(17, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(pnlControls, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(btnOpenModules, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnClose1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addGap(24, 24, 24))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jTitle)
                .addGap(12, 12, 12)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 338, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlControls, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(btnOpenModules, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnClose1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(2, 2, 2)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlCourseModules, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

    private void btnAddModuleToCourseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddModuleToCourseActionPerformed
        if ((selectedCourse == null)
                || (selectedCourse.getId() == null)
                || (courseModules == null)
                || (tblAllModules.getSelectedRowCount() == 0)) {
            return;
        }

        Arrays.stream(tblAllModules.getSelectedRows()).forEach(row -> {
            int modelRow = tblAllModules.convertRowIndexToModel(row);
            Module module = getAllModulesTableModel().getData(modelRow);
            if ((module != null) && (module.getId() != null)) {
                courseModuleService.insert(selectedCourse.getId(), module.getId());
                getCourseModulesTableModel().addRow(module, new Object[] {module.getId(), module.getName(), module.getCode()});
                courseModules.add(module);
            }
        });
        updateModulesTableUI();
    }//GEN-LAST:event_btnAddModuleToCourseActionPerformed

    private void btnRemoveModuleFromCourseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveModuleFromCourseActionPerformed
        if ((selectedCourse == null)
                || (selectedCourse.getId() == null)
                || (courseModules == null)
                || (tblCourseModules.getSelectedRowCount() == 0)) {
            return;
        }

        List<Integer> deletedModelRows = new ArrayList<>();

        Arrays.stream(tblCourseModules.getSelectedRows()).forEach(row -> {
            int modelRow = tblCourseModules.convertRowIndexToModel(row);
            Module module = getCourseModulesTableModel().getData(modelRow);
            if ((module != null) && (module.getId() != null)) {
                courseModuleService.delete(selectedCourse.getId(), module.getId());
                // Collect deleted rows, since we can't change model here,
                // as RowSorter use it to map viewRows to Model rows properly
                deletedModelRows.add(modelRow);
                courseModules.remove(module);
            }
        });
        // Sort in reverse order, so we will delete from the last to the first
        deletedModelRows.sort(Comparator.reverseOrder());
        deletedModelRows.forEach(modelRow -> getCourseModulesTableModel().removeRow(modelRow));

        updateModulesTableUI();

    }//GEN-LAST:event_btnRemoveModuleFromCourseActionPerformed

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

    private void btnClose1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClose1ActionPerformed
        getFrameManager().showParent();
    }//GEN-LAST:event_btnClose1ActionPerformed

    private void btnOpenModulesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenModulesActionPerformed
        getFrameManager().showSub(MODULE);
    }//GEN-LAST:event_btnOpenModulesActionPerformed

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
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnAddModuleToCourse;
    private javax.swing.JButton btnClose1;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnOpenModules;
    private javax.swing.JButton btnReload;
    private javax.swing.JButton btnRemoveModuleFromCourse;
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
    private javax.swing.JLabel lblCourseModulesTitle;
    private javax.swing.JPanel pnlAddRemoveModules;
    private javax.swing.JPanel pnlAllModules;
    private javax.swing.JPanel pnlControls;
    private javax.swing.JPanel pnlCourseModules;
    private PaddedJTable tblAllModules;
    private PaddedJTable tblCourse;
    private PaddedJTable tblCourseModules;
    private javax.swing.JTextField tfCourseFilter;
    private javax.swing.JTextField tfModuleFilter;
    // End of variables declaration//GEN-END:variables

    @Override
    protected PaddedJTable getTable() {
        return tblCourse;
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
        return tfCourseFilter;
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

        courseService.getAll().forEach(course -> {
            getTableModel().addRow(course, new Object[]{
                    course.getId(), course.getName(), course.getCode(),
                    course.getDepartment(), course.getCourseType(), course.getQqiLevel()
            });
        });

        if (getTableModel().getDataList().contains(selectedCourse)) {
            int viewRow = tblCourse.convertRowIndexToView(getTableModel().getDataList().indexOf(selectedCourse));
            tblCourse.setRowSelectionInterval(viewRow, viewRow);
        } else {
            selectedCourse = null;
        }
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
    protected void doDeleteData(Integer dataId) {
        if (dataId != null) {
            courseService.delete(dataId);
        }
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
