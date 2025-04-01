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
import ie.gti.asdl.rey.gtirecord.desktop.ui.component.DataTableModel;
import ie.gti.asdl.rey.gtirecord.desktop.ui.component.PaddedDataCellRenderer;
import ie.gti.asdl.rey.gtirecord.desktop.ui.component.PaddedJTable;
import ie.gti.asdl.rey.gtirecord.desktop.util.SpringGuiRunner;
import ie.gti.asdl.rey.gtirecord.desktop.util.SwingUIUtils;
import ie.gti.asdl.rey.gtirecord.model.entity.*;
import ie.gti.asdl.rey.gtirecord.model.entity.Module;
import org.springframework.context.ApplicationContext;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.util.*;
import java.util.function.Supplier;

import static ie.gti.asdl.rey.gtirecord.desktop.ui.FrameManager.FrameType.COURSE;
import static ie.gti.asdl.rey.gtirecord.desktop.ui.FrameManager.FrameType.MODULE;
import static ie.gti.asdl.rey.gtirecord.desktop.util.SwingUIUtils.createSafeListener;

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
        departmentColumn.setCellRenderer(new PaddedDataCellRenderer(() -> highlightedRow));

        // Set Course Type custom JComboBox Renderer and Editor
        TableColumn courseTypeColumn = getTable().getColumnModel().getColumn(COLUMNS.TYPE.index);
        courseTypeColumn.setCellEditor(new DefaultCellEditor(courseTypeCombo));
        courseTypeColumn.setCellRenderer(new PaddedDataCellRenderer(() -> highlightedRow));

        // Set QQI level custom JComboBox Renderer and Editor
        TableColumn qqiLevelColumn = getTable().getColumnModel().getColumn(COLUMNS.QQI_LEVEL.index);
        qqiLevelColumn.setCellEditor(new DefaultCellEditor(qqiLevelCombo));
        qqiLevelColumn.setCellRenderer(new PaddedDataCellRenderer(() -> highlightedRow));

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
            highlightedRow = row;
            selectedCourse = getTableModel().getData(tblCourse.convertRowIndexToModel(row));
            courseModules = moduleService.getByCourseId(selectedCourse.getId());
            tblCourse.repaint(); // Repaint after we changed highlightedRow
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

        SwingUIUtils.addTableFilter(tblCourseModules, tfModuleTableFilter);
        SwingUIUtils.addTableFilter(tblAllModules, tfModuleTableFilter);

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
    protected void onFormShown() {
        super.onFormShown();
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
        tblCourse = new PaddedJTable(() -> highlightedRow);
        jAddPanel = new javax.swing.JPanel();
        jAddBtn = new javax.swing.JButton();
        jAddCancelBtn = new javax.swing.JButton();
        jAddSaveBtn = new javax.swing.JButton();
        jUpdatePanel = new javax.swing.JPanel();
        jUpdateBtn = new javax.swing.JButton();
        jRevertBtn = new javax.swing.JButton();
        jDeleteBtn = new javax.swing.JButton();
        jTitle = new javax.swing.JLabel();
        pnlCourseTableFilter = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        tfTableFilter = new javax.swing.JTextField();
        pnlModulesTableFilter = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        tfModuleTableFilter = new javax.swing.JTextField();
        pnlCourseModules = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblCourseModules = new PaddedJTable();
        lblCourseModulesTitle = new javax.swing.JLabel();
        pnlAllModules = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblAllModules = new PaddedJTable();
        jLabel4 = new javax.swing.JLabel();
        pnlAddRemoveModules = new javax.swing.JPanel();
        btnRemoveModuleFromCourse = new javax.swing.JButton();
        btnAddModuleToCourse = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        btnOpenModules = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();

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

        jAddPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jAddBtn.setText("Add new course");
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

        jAddSaveBtn.setText("Save new courses");
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
                    .addComponent(jAddSaveBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jAddBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jAddCancelBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        jAddPanelLayout.setVerticalGroup(
            jAddPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jAddPanelLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jAddBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jAddSaveBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                .addComponent(jAddCancelBtn)
                .addGap(18, 18, 18))
        );

        jUpdatePanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jUpdateBtn.setText("Update selected course");
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

        jDeleteBtn.setText("Delete selected course");
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
                    .addComponent(jUpdateBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jRevertBtn)
                .addGap(17, 17, 17))
        );

        jTitle.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jTitle.setText("COURSES");

        pnlCourseTableFilter.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlCourseTableFilter.setPreferredSize(new java.awt.Dimension(197, 150));

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Course Filter");

        javax.swing.GroupLayout pnlCourseTableFilterLayout = new javax.swing.GroupLayout(pnlCourseTableFilter);
        pnlCourseTableFilter.setLayout(pnlCourseTableFilterLayout);
        pnlCourseTableFilterLayout.setHorizontalGroup(
            pnlCourseTableFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCourseTableFilterLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(pnlCourseTableFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfTableFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 296, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15))
        );
        pnlCourseTableFilterLayout.setVerticalGroup(
            pnlCourseTableFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCourseTableFilterLayout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tfTableFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlModulesTableFilter.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlModulesTableFilter.setPreferredSize(new java.awt.Dimension(197, 150));

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Module Filter");

        javax.swing.GroupLayout pnlModulesTableFilterLayout = new javax.swing.GroupLayout(pnlModulesTableFilter);
        pnlModulesTableFilter.setLayout(pnlModulesTableFilterLayout);
        pnlModulesTableFilterLayout.setHorizontalGroup(
            pnlModulesTableFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlModulesTableFilterLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(pnlModulesTableFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfModuleTableFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 296, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(13, Short.MAX_VALUE))
        );
        pnlModulesTableFilterLayout.setVerticalGroup(
            pnlModulesTableFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlModulesTableFilterLayout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tfModuleTableFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlCourseModules.setBorder(javax.swing.BorderFactory.createEtchedBorder());

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

        pnlAllModules.setBorder(javax.swing.BorderFactory.createEtchedBorder());

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

        javax.swing.GroupLayout pnlAddRemoveModulesLayout = new javax.swing.GroupLayout(pnlAddRemoveModules);
        pnlAddRemoveModules.setLayout(pnlAddRemoveModulesLayout);
        pnlAddRemoveModulesLayout.setHorizontalGroup(
            pnlAddRemoveModulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAddRemoveModulesLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(pnlAddRemoveModulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnRemoveModuleFromCourse)
                    .addComponent(btnAddModuleToCourse))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        pnlAddRemoveModulesLayout.setVerticalGroup(
            pnlAddRemoveModulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAddRemoveModulesLayout.createSequentialGroup()
                .addGap(96, 96, 96)
                .addComponent(btnRemoveModuleFromCourse)
                .addGap(38, 38, 38)
                .addComponent(btnAddModuleToCourse)
                .addContainerGap(115, Short.MAX_VALUE))
        );

        btnOpenModules.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnOpenModules.setText("<html>Manage<br/>Modules</html>");
        btnOpenModules.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenModulesActionPerformed(evt);
            }
        });

        btnClose.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnClose.setText("Close");
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(btnOpenModules, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 73, Short.MAX_VALUE)
                .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnOpenModules, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jAddPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jUpdatePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(pnlModulesTableFilter, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 328, Short.MAX_VALUE)
                            .addComponent(pnlCourseTableFilter, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 328, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(pnlCourseModules, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pnlAddRemoveModules, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pnlAllModules, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(22, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jTitle)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jAddPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jUpdatePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(pnlCourseTableFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlModulesTableFilter, javax.swing.GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlCourseModules, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlAllModules, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlAddRemoveModules, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 1050, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 7, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 774, Short.MAX_VALUE)
                .addContainerGap())
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
    private javax.swing.JButton btnAddModuleToCourse;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnOpenModules;
    private javax.swing.JButton btnRemoveModuleFromCourse;
    private javax.swing.JButton jAddBtn;
    private javax.swing.JButton jAddCancelBtn;
    private javax.swing.JPanel jAddPanel;
    private javax.swing.JButton jAddSaveBtn;
    private javax.swing.JButton jDeleteBtn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JButton jRevertBtn;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel jTitle;
    private javax.swing.JButton jUpdateBtn;
    private javax.swing.JPanel jUpdatePanel;
    private javax.swing.JLabel lblCourseModulesTitle;
    private javax.swing.JPanel pnlAddRemoveModules;
    private javax.swing.JPanel pnlAllModules;
    private javax.swing.JPanel pnlCourseModules;
    private javax.swing.JPanel pnlCourseTableFilter;
    private javax.swing.JPanel pnlModulesTableFilter;
    private PaddedJTable tblAllModules;
    private PaddedJTable tblCourse;
    private PaddedJTable tblCourseModules;
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
