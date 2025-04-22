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
import java.util.List;
import java.util.function.Function;

import static ie.gti.asdl.rey.gtirecord.desktop.ui.FrameManager.FrameType.*;
import static ie.gti.asdl.rey.gtirecord.desktop.util.SwingUIUtils.createSafeListSelectionListener;

/**
 *
 * @author Andrei
 */
public class GroupFrame extends AbstractTableDataFrame<Group> {

    private static int columnCnt = 0;

    private enum GROUP_COLUMNS {
        ID(columnCnt++), NAME(columnCnt++), CODE(columnCnt++),
        COURSE(columnCnt++);

        final int index;
        GROUP_COLUMNS(int index) {
            this.index = index;
        }
    }

    private enum GROUP_MODULE_COLUMNS {
        ID(0), NAME(1), CODE(2), TEACHER(3);
        final int index;
        GROUP_MODULE_COLUMNS(int index) {
            this.index = index;
        }
    }

//    private enum GROUP_STUDENT_COLUMNS {
//        ID(0), FIRST_NAME(1), LAST_NAME(2);
//        final int index;
//        GROUP_STUDENT_COLUMNS(int index) {
//            this.index = index;
//        }
//    }

    private enum STUDENT_COLUMNS {
        ID(0), FIRST_NAME(1), LAST_NAME(2), GROUP(3);
        final int index;
        STUDENT_COLUMNS(int index) {
            this.index = index;
        }
    }

    private final JComboBox<Course> courseCombo = new JComboBox<>();

    private final ModuleService moduleService;

    private final TeacherService teacherService;

    private final GroupService groupService;

    private final GroupModuleService groupModuleService;

    private final CourseService courseService;

    private final StudentService studentService;

//    private final TeacherModuleService teacherModuleService;

    private Group selectedGroup;

    private List<Module> allModules = new ArrayList<>();
    private List<GroupModule> groupModules = new ArrayList<>();

    private List<Student> allStudents = new ArrayList<>();
    private List<Student> groupStudents = new ArrayList<>();

    private Integer highlightedRow;

    /**
     * Creates new form CourseFrame
     */
    public GroupFrame(FrameManager frameManager, ServiceManager serviceManager) {
        super(frameManager);
        groupService = serviceManager.getGroupService();
        courseService = serviceManager.getCourseService();
        moduleService = serviceManager.getModuleService();
        groupModuleService = serviceManager.getGroupModuleService();
        teacherService = serviceManager.getTeacherService();
        studentService = serviceManager.getStudentService();
//        teacherModuleService = serviceManager.getTeacherModuleService();
        initComponents();
        initFrame();
    }

    @Override
    protected void initFrame() {
        // Init table model first
        initTableModel();
        super.initFrame();

        tblGroup.setHighlightedRowSupplier(() -> highlightedRow);

        DataListCellRenderer listCellRendered = new DataListCellRenderer();
        PaddedDataCellRenderer dataCellRenderer = new PaddedDataCellRenderer(() -> highlightedRow);

        // Set Course Type custom JComboBox Renderer and Editor
        TableColumn courseColumn = getTable().getColumnModel().getColumn(GROUP_COLUMNS.COURSE.index);
        courseCombo.setRenderer(listCellRendered);
        courseColumn.setCellEditor(new DefaultCellEditor(courseCombo));
        courseColumn.setCellRenderer(dataCellRenderer);

        TableColumnModel columnModel = getTable().getColumnModel();
        columnModel.getColumn(GROUP_COLUMNS.ID.index)     .setMaxWidth(35);
        columnModel.getColumn(GROUP_COLUMNS.NAME.index)   .setMinWidth(100);
        columnModel.getColumn(GROUP_COLUMNS.CODE.index)   .setMinWidth(70);
        columnModel.getColumn(GROUP_COLUMNS.COURSE.index) .setMinWidth(120);

        // Init module table
        tblGroup.getSelectionModel().addListSelectionListener(createSafeListSelectionListener(event -> onGroupSelect()));

        initModuleTables();
        initStudentTables();
    }

    private void onGroupSelect() {
        reloadGroupStudentsAndModules();
        updateAdditionalTablesUI();
    }

    private void reloadGroupStudentsAndModules() {
        tblGroupModules.clear();
        tblGroupStudents.clear();

        // Show modules for the first course
        Arrays.stream(tblGroup.getSelectedRows()).findFirst().ifPresentOrElse(row -> {
            highlightedRow = row; // Set new highlighted row
            tblGroup.repaint(); // Repaint after we changed highlightedRow
            selectedGroup = getTableModel().getData(tblGroup.convertRowIndexToModel(row));
            groupModules = groupModuleService.getByGroupId(selectedGroup.getId());
            groupStudents = studentService.getByGroupId(selectedGroup.getId());
        }, () -> {
            if (groupModules != null) {
                groupModules.clear();
            }
            if (groupStudents != null) {
                groupStudents.clear();
            }
        });
    }

    private void reloadAllStudentsAndModules() {
        tblAllModules.clear();
        allModules = moduleService.getAll();
        tblAllStudents.clear();
        allStudents = studentService.getAll();
    }

    private void initTableModel() {
        if (! (getTable().getModel() instanceof DataTableModel)) {
            getTable().setModel(new DataTableModel<Group>(
                    new Object [][] {

                    },
                    new String [] {
                            "ID", "Group name", "Group code", "Course"
                    }
            ) {
                Class[] types = new Class [] {
                        java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
                };
                boolean[] canEdit = new boolean [] {
                        false, true, true, true
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

    private void initStudentTables() {
        initStudentTable(tblGroupStudents);
        initStudentTable(tblAllStudents);

        SwingUIUtils.addTableFilter(tblGroupStudents, tfStudentFilter);
        SwingUIUtils.addTableFilter(tblAllStudents, tfStudentFilter);

        tblGroupStudents.getSelectionModel().addListSelectionListener(createSafeListSelectionListener(listener -> updateButtonsUI()));
        tblAllStudents.getSelectionModel().addListSelectionListener(createSafeListSelectionListener(listener -> updateButtonsUI()));
    }

    private void initStudentTable(PaddedJTable table) {
        if (! (table.getModel() instanceof DataTableModel)) {
            table.setModel(new StudentSimpleTableModel());
        }

        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(STUDENT_COLUMNS.ID.index).setMaxWidth(35);
        columnModel.getColumn(STUDENT_COLUMNS.FIRST_NAME.index).setMinWidth(70);
        columnModel.getColumn(STUDENT_COLUMNS.LAST_NAME.index).setMinWidth(70);
        columnModel.getColumn(STUDENT_COLUMNS.GROUP.index).setMinWidth(70);

        TableColumn groupColumn = table.getColumnModel().getColumn(STUDENT_COLUMNS.GROUP.index);
        groupColumn.setCellRenderer(new PaddedDataCellRenderer());
    }

    private void initModuleTables() {
        initGroupModuleTable();
        ModuleFrame.initTable(tblAllModules);

        SwingUIUtils.addTableFilter(tblGroupModules, tfModuleFilter);
        SwingUIUtils.addTableFilter(tblAllModules, tfModuleFilter);

        tblGroupModules.getSelectionModel().addListSelectionListener(createSafeListSelectionListener(listener -> updateButtonsUI()));
        tblAllModules.getSelectionModel().addListSelectionListener(createSafeListSelectionListener(listener -> updateButtonsUI()));

        // Function, which returns list of teachers for a module from the table
        // Todo: cache teachers for module locally
        Function<Integer, List<Teacher>> teachersProvider = (row) -> {
            List<Teacher> teachers = new ArrayList<>();
            Teacher emptyTeacher = InstanceFactory.create(Teacher.class);
            emptyTeacher.getPerson().setFirstName(" "); // Should be at least space, otherwise it won't be added as a comboBox item
            teachers.add(emptyTeacher);
            teachers.addAll(teacherService.getByModuleId((Integer) tblGroupModules.getValueAt(row, GROUP_MODULE_COLUMNS.ID.index)));
            return teachers;
        };

        // Init group module table
        TableColumn teacherColumn = tblGroupModules.getColumnModel().getColumn(GROUP_MODULE_COLUMNS.TEACHER.index);
        var cbCellEditor = getTeacherDynamicComboBoxEditor(teachersProvider);

        teacherColumn.setCellEditor(cbCellEditor);
        teacherColumn.setCellRenderer(new PaddedDataCellRenderer());
    }

    private @NotNull DynamicComboBoxEditor<Teacher> getTeacherDynamicComboBoxEditor(Function<Integer, List<Teacher>> teachersProvider) {
        DynamicComboBoxEditor<Teacher> cbCellEditor = new DynamicComboBoxEditor<>(teachersProvider);
        cbCellEditor.setRowAwareActionListener((e, row) -> {
            System.out.println("teacher row: " + row);
            JComboBox<Teacher> cb = (JComboBox<Teacher>) e.getSource();
            Teacher teacher = (Teacher) cb.getSelectedItem();
            if (teacher == null) return;
            int modelRow = tblGroupModules.convertRowIndexToModel(row);
            GroupModule groupModule = getGroupModulesTableModel().getData(modelRow);
            groupModuleService.update(groupModule.getGroup().getId(), groupModule.getModule().getId(), teacher.getPerson().getId());
        });
        return cbCellEditor;
    }

    void initGroupModuleTable() {
        if (! (tblGroupModules.getModel() instanceof DataTableModel)) {
            tblGroupModules.setModel(new DataTableModel<Module>(
                    new Object[][]{

                    },
                    new String[]{
                            "ID", "Module name", "Module code", "Teacher"
                    }
            ) {
                Class[] types = new Class[]{
                        java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
                };
                boolean[] canEdit = new boolean[]{
                        false, false, false, true
                };

                public Class getColumnClass(int columnIndex) {
                    return types[columnIndex];
                }

                public boolean isCellEditable(int rowIndex, int columnIndex) {
                    return canEdit[columnIndex];
                }
            });
        }
        TableColumnModel columnModel = tblGroupModules.getColumnModel();
        columnModel.getColumn(GROUP_MODULE_COLUMNS.ID.index).setMaxWidth(35);
        columnModel.getColumn(GROUP_MODULE_COLUMNS.NAME.index).setMinWidth(80);
        columnModel.getColumn(GROUP_MODULE_COLUMNS.CODE.index).setMinWidth(60);
        columnModel.getColumn(GROUP_MODULE_COLUMNS.TEACHER.index).setMinWidth(150);

        btnAddModuleToGroup.setEnabled(false);
        btnRemoveModuleFromGroup.setEnabled(false);
    }

    private void updateButtonsUI() {
//        btnAddModuleToGroup.setEnabled(tblAllModules.getSelectedRowCount() > 0);
//        btnRemoveModuleFromGroup.setEnabled(tblGroupModules.getSelectedRowCount() > 0);
        btnAddModuleToGroup.setEnabled(false);
        btnRemoveModuleFromGroup.setEnabled(false);

        btnAddStudentToGroup.setEnabled(tblAllStudents.getSelectedRowCount() > 0);
        btnRemoveStudentFromGroup.setEnabled(tblGroupStudents.getSelectedRowCount() > 0);
    }

    private void updateAdditionalTablesUI() {
        updateModuleTablesUI();
        updateStudentTablesUI();
    }

    private void updateModuleTablesUI() {
        lblGroupModulesTitle.setText((selectedGroup == null || selectedGroup.getName() == null)
                ? "Group Modules" : selectedGroup.getName() + " Modules");

        tblGroupModules.clear();
        tblAllModules.clear();

        if (allModules != null) {
            List<Module> allExceptGroupModules = new ArrayList<>(allModules);
            if (groupModules != null) {
                groupModules.forEach(groupModule -> {
                    allExceptGroupModules.remove(groupModule.getModule());

                    Module module = groupModule.getModule();
                    getGroupModulesTableModel().addRow(groupModule, new Object[] {module.getId(), module.getName(), module.getCode(), groupModule.getTeacher()});
                });
            }
            allExceptGroupModules.forEach(module -> {
                getAllModulesTableModel().addRow(module, new Object[] {module.getId(), module.getName(), module.getCode()});
            });
        }
    }

    private void updateStudentTablesUI() {
        lblGroupStudentsTitle.setText((selectedGroup == null || selectedGroup.getName() == null)
                ? "Group Students" : selectedGroup.getName() + " Students");

        tblGroupStudents.clear();
        tblAllStudents.clear();

        if (allStudents != null) {
            List<Student> allExceptGroupStudents = new ArrayList<>(allStudents);
            if (groupStudents != null) {
                groupStudents.forEach(student -> {
                    allExceptGroupStudents.remove(student);
                    getGroupStudentsTableModel().addRow(student, new Object[] {student.getPerson().getId(),
                            student.getPerson().getFirstName(), student.getPerson().getLastName(), student.getGroup()});
                });
            }
            allExceptGroupStudents.forEach(student -> {
                getAllStudentsTableModel().addRow(student, new Object[] {student.getPerson().getId(),
                        student.getPerson().getFirstName(), student.getPerson().getLastName(), student.getGroup()});
            });
        }
    }

    protected DataTableModel<GroupModule> getGroupModulesTableModel() {
        return (DataTableModel<GroupModule>) tblGroupModules.getModel();
    }

    protected DataTableModel<Module> getAllModulesTableModel() {
        return (DataTableModel<Module>) tblAllModules.getModel();
    }

    protected DataTableModel<Student> getGroupStudentsTableModel() {
        return (DataTableModel<Student>) tblGroupStudents.getModel();
    }

    protected DataTableModel<Student> getAllStudentsTableModel() {
        return (DataTableModel<Student>) tblAllStudents.getModel();
    }

    @Override
    protected void onFrameShown() {
        super.onFrameShown();
        reloadAllStudentsAndModules();
        reloadGroupStudentsAndModules();
        updateAdditionalTablesUI();
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
        tblGroup = new PaddedJTable();
        jTitle = new javax.swing.JLabel();
        pnlControls = new javax.swing.JPanel();
        btnAdd = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnReload = new javax.swing.JButton();
        pnlGroupFilter = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        tfCourseFilter = new javax.swing.JTextField();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        pnlStudents = new javax.swing.JPanel();
        pnlGroupStudents = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        tblGroupStudents = new PaddedJTable();
        lblGroupStudentsTitle = new javax.swing.JLabel();
        btnOpenAssignments = new javax.swing.JButton();
        pnlAddRemoveStudents = new javax.swing.JPanel();
        btnRemoveStudentFromGroup = new javax.swing.JButton();
        btnAddStudentToGroup = new javax.swing.JButton();
        pnlStudentFilter = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        tfStudentFilter = new javax.swing.JTextField();
        pnlAllStudents = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        tblAllStudents = new PaddedJTable();
        jLabel8 = new javax.swing.JLabel();
        btnOpenStudents = new javax.swing.JButton();
        pnlModules = new javax.swing.JPanel();
        pnlGroupModules = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblGroupModules = new PaddedJTable();
        lblGroupModulesTitle = new javax.swing.JLabel();
        btnOpenTeachers = new javax.swing.JButton();
        pnlAddRemoveModules = new javax.swing.JPanel();
        btnRemoveModuleFromGroup = new javax.swing.JButton();
        btnAddModuleToGroup = new javax.swing.JButton();
        pnlModuleFilter = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        tfModuleFilter = new javax.swing.JTextField();
        pnlAllModules = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblAllModules = new PaddedJTable();
        jLabel4 = new javax.swing.JLabel();
        btnOpenModules = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jPanel1.setPreferredSize(new java.awt.Dimension(1033, 755));

        tblGroup.setAutoCreateRowSorter(true);
        tblGroup.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        tblGroup.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Group name", "Group code", "Course"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblGroup);

        jTitle.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jTitle.setForeground(new java.awt.Color(0, 51, 204));
        jTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jTitle.setText("GROUPS");

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

        pnlGroupFilter.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 3, 15)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 51, 204));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel2.setText("Group filter:");

        tfCourseFilter.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        tfCourseFilter.setForeground(new java.awt.Color(0, 51, 204));

        javax.swing.GroupLayout pnlGroupFilterLayout = new javax.swing.GroupLayout(pnlGroupFilter);
        pnlGroupFilter.setLayout(pnlGroupFilterLayout);
        pnlGroupFilterLayout.setHorizontalGroup(
            pnlGroupFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGroupFilterLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tfCourseFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlGroupFilterLayout.setVerticalGroup(
            pnlGroupFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGroupFilterLayout.createSequentialGroup()
                .addComponent(jLabel2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(tfCourseFilter)
        );

        javax.swing.GroupLayout pnlControlsLayout = new javax.swing.GroupLayout(pnlControls);
        pnlControls.setLayout(pnlControlsLayout);
        pnlControlsLayout.setHorizontalGroup(
            pnlControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlControlsLayout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnReload, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlGroupFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(185, Short.MAX_VALUE))
        );
        pnlControlsLayout.setVerticalGroup(
            pnlControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlControlsLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlGroupFilter, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnReload, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        pnlGroupStudents.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        pnlGroupStudents.setPreferredSize(new java.awt.Dimension(637, 37));

        tblGroupStudents.setAutoCreateRowSorter(true);
        tblGroupStudents.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "First name", "Last name", "Group"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane6.setViewportView(tblGroupStudents);

        lblGroupStudentsTitle.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblGroupStudentsTitle.setForeground(new java.awt.Color(0, 51, 204));
        lblGroupStudentsTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblGroupStudentsTitle.setText("Group Students");

        btnOpenAssignments.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnOpenAssignments.setForeground(new java.awt.Color(0, 51, 204));
        btnOpenAssignments.setText("Assignments");
        btnOpenAssignments.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenAssignmentsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlGroupStudentsLayout = new javax.swing.GroupLayout(pnlGroupStudents);
        pnlGroupStudents.setLayout(pnlGroupStudentsLayout);
        pnlGroupStudentsLayout.setHorizontalGroup(
            pnlGroupStudentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlGroupStudentsLayout.createSequentialGroup()
                .addGroup(pnlGroupStudentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 633, Short.MAX_VALUE)
                    .addGroup(pnlGroupStudentsLayout.createSequentialGroup()
                        .addComponent(lblGroupStudentsTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnOpenAssignments, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        pnlGroupStudentsLayout.setVerticalGroup(
            pnlGroupStudentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGroupStudentsLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(pnlGroupStudentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblGroupStudentsTitle)
                    .addComponent(btnOpenAssignments))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        btnRemoveStudentFromGroup.setFont(new java.awt.Font("Monoid", 1, 14)); // NOI18N
        btnRemoveStudentFromGroup.setForeground(new java.awt.Color(0, 0, 204));
        btnRemoveStudentFromGroup.setText(">>>");
        btnRemoveStudentFromGroup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveStudentFromGroupActionPerformed(evt);
            }
        });

        btnAddStudentToGroup.setFont(new java.awt.Font("Monoid", 1, 14)); // NOI18N
        btnAddStudentToGroup.setForeground(new java.awt.Color(0, 0, 204));
        btnAddStudentToGroup.setText("<<<");
        btnAddStudentToGroup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddStudentToGroupActionPerformed(evt);
            }
        });

        pnlStudentFilter.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 51, 204));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel7.setText("Student filter:");

        tfStudentFilter.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        tfStudentFilter.setForeground(new java.awt.Color(0, 51, 204));

        javax.swing.GroupLayout pnlStudentFilterLayout = new javax.swing.GroupLayout(pnlStudentFilter);
        pnlStudentFilter.setLayout(pnlStudentFilterLayout);
        pnlStudentFilterLayout.setHorizontalGroup(
            pnlStudentFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlStudentFilterLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(pnlStudentFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tfStudentFilter))
                .addContainerGap())
        );
        pnlStudentFilterLayout.setVerticalGroup(
            pnlStudentFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlStudentFilterLayout.createSequentialGroup()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tfStudentFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(21, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlAddRemoveStudentsLayout = new javax.swing.GroupLayout(pnlAddRemoveStudents);
        pnlAddRemoveStudents.setLayout(pnlAddRemoveStudentsLayout);
        pnlAddRemoveStudentsLayout.setHorizontalGroup(
            pnlAddRemoveStudentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAddRemoveStudentsLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlAddRemoveStudentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlStudentFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlAddRemoveStudentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(btnRemoveStudentFromGroup, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnAddStudentToGroup, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE))))
        );
        pnlAddRemoveStudentsLayout.setVerticalGroup(
            pnlAddRemoveStudentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAddRemoveStudentsLayout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(pnlStudentFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnRemoveStudentFromGroup)
                .addGap(52, 52, 52)
                .addComponent(btnAddStudentToGroup)
                .addGap(74, 74, 74))
        );

        pnlAllStudents.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        tblAllStudents.setAutoCreateRowSorter(true);
        tblAllStudents.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "First name", "Last name", "Group"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane7.setViewportView(tblAllStudents);

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(0, 51, 204));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("All Students");

        btnOpenStudents.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnOpenStudents.setForeground(new java.awt.Color(0, 51, 204));
        btnOpenStudents.setText("Students");
        btnOpenStudents.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenStudentsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlAllStudentsLayout = new javax.swing.GroupLayout(pnlAllStudents);
        pnlAllStudents.setLayout(pnlAllStudentsLayout);
        pnlAllStudentsLayout.setHorizontalGroup(
            pnlAllStudentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAllStudentsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlAllStudentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlAllStudentsLayout.createSequentialGroup()
                        .addComponent(jScrollPane7)
                        .addContainerGap())
                    .addGroup(pnlAllStudentsLayout.createSequentialGroup()
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 468, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 21, Short.MAX_VALUE)
                        .addComponent(btnOpenStudents, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18))))
        );
        pnlAllStudentsLayout.setVerticalGroup(
            pnlAllStudentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAllStudentsLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(pnlAllStudentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(btnOpenStudents))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout pnlStudentsLayout = new javax.swing.GroupLayout(pnlStudents);
        pnlStudents.setLayout(pnlStudentsLayout);
        pnlStudentsLayout.setHorizontalGroup(
            pnlStudentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlStudentsLayout.createSequentialGroup()
                .addComponent(pnlGroupStudents, javax.swing.GroupLayout.PREFERRED_SIZE, 641, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlAddRemoveStudents, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlAllStudents, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29))
        );
        pnlStudentsLayout.setVerticalGroup(
            pnlStudentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlStudentsLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(pnlStudentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlGroupStudents, javax.swing.GroupLayout.DEFAULT_SIZE, 316, Short.MAX_VALUE)
                    .addComponent(pnlAddRemoveStudents, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlAllStudents, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Students", pnlStudents);

        pnlGroupModules.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        tblGroupModules.setAutoCreateRowSorter(true);
        tblGroupModules.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Module name", "Module code", "Teacher"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tblGroupModules);

        lblGroupModulesTitle.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblGroupModulesTitle.setForeground(new java.awt.Color(0, 51, 204));
        lblGroupModulesTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblGroupModulesTitle.setText("Group Modules");

        btnOpenTeachers.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnOpenTeachers.setForeground(new java.awt.Color(0, 51, 204));
        btnOpenTeachers.setText("Teachers");
        btnOpenTeachers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenTeachersActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlGroupModulesLayout = new javax.swing.GroupLayout(pnlGroupModules);
        pnlGroupModules.setLayout(pnlGroupModulesLayout);
        pnlGroupModulesLayout.setHorizontalGroup(
            pnlGroupModulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGroupModulesLayout.createSequentialGroup()
                .addGroup(pnlGroupModulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlGroupModulesLayout.createSequentialGroup()
                        .addComponent(lblGroupModulesTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnOpenTeachers, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 633, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlGroupModulesLayout.setVerticalGroup(
            pnlGroupModulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGroupModulesLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(pnlGroupModulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblGroupModulesTitle)
                    .addComponent(btnOpenTeachers))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        btnRemoveModuleFromGroup.setFont(new java.awt.Font("Monoid", 1, 14)); // NOI18N
        btnRemoveModuleFromGroup.setForeground(new java.awt.Color(0, 0, 204));
        btnRemoveModuleFromGroup.setText(">>>");
        btnRemoveModuleFromGroup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveModuleFromGroupActionPerformed(evt);
            }
        });

        btnAddModuleToGroup.setFont(new java.awt.Font("Monoid", 1, 14)); // NOI18N
        btnAddModuleToGroup.setForeground(new java.awt.Color(0, 0, 204));
        btnAddModuleToGroup.setText("<<<");
        btnAddModuleToGroup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddModuleToGroupActionPerformed(evt);
            }
        });

        pnlModuleFilter.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 51, 204));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel6.setText("Module filter:");

        tfModuleFilter.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        tfModuleFilter.setForeground(new java.awt.Color(0, 51, 204));

        javax.swing.GroupLayout pnlModuleFilterLayout = new javax.swing.GroupLayout(pnlModuleFilter);
        pnlModuleFilter.setLayout(pnlModuleFilterLayout);
        pnlModuleFilterLayout.setHorizontalGroup(
            pnlModuleFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlModuleFilterLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(pnlModuleFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tfModuleFilter))
                .addContainerGap())
        );
        pnlModuleFilterLayout.setVerticalGroup(
            pnlModuleFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlModuleFilterLayout.createSequentialGroup()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tfModuleFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(21, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlAddRemoveModulesLayout = new javax.swing.GroupLayout(pnlAddRemoveModules);
        pnlAddRemoveModules.setLayout(pnlAddRemoveModulesLayout);
        pnlAddRemoveModulesLayout.setHorizontalGroup(
            pnlAddRemoveModulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAddRemoveModulesLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlAddRemoveModulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlModuleFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlAddRemoveModulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(btnRemoveModuleFromGroup, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnAddModuleToGroup, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE))))
        );
        pnlAddRemoveModulesLayout.setVerticalGroup(
            pnlAddRemoveModulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAddRemoveModulesLayout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(pnlModuleFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnRemoveModuleFromGroup)
                .addGap(52, 52, 52)
                .addComponent(btnAddModuleToGroup)
                .addGap(74, 74, 74))
        );

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

        btnOpenModules.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnOpenModules.setForeground(new java.awt.Color(0, 51, 204));
        btnOpenModules.setText("Modules");
        btnOpenModules.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenModulesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlAllModulesLayout = new javax.swing.GroupLayout(pnlAllModules);
        pnlAllModules.setLayout(pnlAllModulesLayout);
        pnlAllModulesLayout.setHorizontalGroup(
            pnlAllModulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlAllModulesLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlAllModulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 581, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlAllModulesLayout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 464, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnOpenModules, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(13, 13, 13)))
                .addGap(18, 18, 18))
        );
        pnlAllModulesLayout.setVerticalGroup(
            pnlAllModulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAllModulesLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(pnlAllModulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(btnOpenModules))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout pnlModulesLayout = new javax.swing.GroupLayout(pnlModules);
        pnlModules.setLayout(pnlModulesLayout);
        pnlModulesLayout.setHorizontalGroup(
            pnlModulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlModulesLayout.createSequentialGroup()
                .addComponent(pnlGroupModules, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlAddRemoveModules, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlAllModules, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlModulesLayout.setVerticalGroup(
            pnlModulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlModulesLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(pnlModulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlGroupModules, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlAddRemoveModules, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlAllModules, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Modules", pnlModules);

        btnClose.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnClose.setForeground(new java.awt.Color(0, 51, 204));
        btnClose.setText("Close");
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(pnlControls, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 18, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1355, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1355, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(15, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jTitle)
                .addGap(12, 12, 12)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(pnlControls, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 351, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 1388, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 781, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
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

    private void btnOpenModulesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenModulesActionPerformed
        getFrameManager().showSub(MODULE);
    }//GEN-LAST:event_btnOpenModulesActionPerformed

    private void btnAddModuleToGroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddModuleToGroupActionPerformed
        addModuleToGroup();
    }//GEN-LAST:event_btnAddModuleToGroupActionPerformed

    private void btnRemoveModuleFromGroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveModuleFromGroupActionPerformed
        removeModuleFromGroup();
    }//GEN-LAST:event_btnRemoveModuleFromGroupActionPerformed

    private void addModuleToGroup() {
        if ((selectedGroup == null)
                || (selectedGroup.getId() == null)
                || (groupModules == null)
                || (tblAllModules.getSelectedRowCount() == 0)) {
            return;
        }

        Arrays.stream(tblAllModules.getSelectedRows()).forEach(row -> {
            int modelRow = tblAllModules.convertRowIndexToModel(row);
            Module module = getAllModulesTableModel().getData(modelRow);
            if ((module != null) && (module.getId() != null)) {
                // Insert without the teacher. Teacher could be set later on
                GroupModule groupModule = new GroupModule();
                groupModule.setGroup(selectedGroup);
                groupModule.setModule(module);
                Teacher teacher = new Teacher();
                teacher.setPerson(new Person());
                groupModule.setTeacher(teacher);
                groupModuleService.insert(groupModule);
                getGroupModulesTableModel().addRow(groupModule, new Object[] {module.getId(), module.getName(), module.getCode(), null});
                groupModules.add(groupModule);
            }
        });
        updateModuleTablesUI();
    }

    private void removeModuleFromGroup() {
        if ((selectedGroup == null)
                || (selectedGroup.getId() == null)
                || (groupModules == null)
                || (tblGroupModules.getSelectedRowCount() == 0)) {
            return;
        }

        List<Integer> deletedModelRows = new ArrayList<>();

        Arrays.stream(tblGroupModules.getSelectedRows()).forEach(row -> {
            int modelRow = tblGroupModules.convertRowIndexToModel(row);
            GroupModule groupModule = getGroupModulesTableModel().getData(modelRow);
            if ((groupModule != null) && (groupModule.getModule() != null) && (groupModule.getModule().getId() != null)) {
                groupModuleService.delete(groupModule.getId());
                // Collect deleted rows, since we can't change model here,
                // as RowSorter use it to map viewRows to Model rows properly
                deletedModelRows.add(modelRow);
                groupModules.remove(groupModule);
            }
        });
        // Sort in reverse order, so we will delete from the last to the first
        deletedModelRows.sort(Comparator.reverseOrder());
        deletedModelRows.forEach(modelRow -> getGroupModulesTableModel().removeRow(modelRow));

        updateModuleTablesUI();
    }

    private void addStudentToGroup() {
        if ((selectedGroup == null)
                || (selectedGroup.getId() == null)
                || (groupStudents == null)
                || (tblAllStudents.getSelectedRowCount() == 0)) {
            return;
        }

        Arrays.stream(tblAllStudents.getSelectedRows()).forEach(row -> {
            int modelRow = tblAllStudents.convertRowIndexToModel(row);
            Student student = getAllStudentsTableModel().getData(modelRow);
//            if ((student != null) && (student.getId() != null)) {
                // Insert without the teacher. Teacher could be set later on
//                GroupModule groupModule = new GroupModule();
//                groupModule.setGroup(selectedGroup);
//                groupModule.setModule(module);
//                Teacher teacher = new Teacher();
//                teacher.setPerson(new Person());
//                groupModule.setTeacher(teacher);
            student.setGroup(selectedGroup);
            studentService.updateStudentAndAssignments(student);
            getGroupStudentsTableModel().addRow(student, new Object[] {student.getPerson().getId(),
                    student.getPerson().getFirstName(), student.getPerson().getLastName(), student.getGroup()});
            groupStudents.add(student);
//            }
        });
        updateStudentTablesUI();
    }

    private void removeStudentFromGroup() {
        if ((selectedGroup == null)
                || (selectedGroup.getId() == null)
                || (groupStudents == null)
                || (tblGroupStudents.getSelectedRowCount() == 0)) {
            return;
        }

        List<Integer> deletedModelRows = new ArrayList<>();

        Arrays.stream(tblGroupStudents.getSelectedRows()).forEach(row -> {
            int modelRow = tblGroupStudents.convertRowIndexToModel(row);
            Student student = getGroupStudentsTableModel().getData(modelRow);
            // Reset group
            student.setGroup(InstanceFactory.create(Group.class));
            studentService.update(student);
            // Collect deleted rows, since we can't change the model here,
            // as RowSorter use it to map viewRows to Model rows properly
            deletedModelRows.add(modelRow);
            groupStudents.remove(student);
        });
        // Sort in reverse order, so we will delete it from the last to the first
        deletedModelRows.sort(Comparator.reverseOrder());
        deletedModelRows.forEach(modelRow -> getGroupStudentsTableModel().removeRow(modelRow));

        allStudents = studentService.getAll();

        updateStudentTablesUI();
    }

    private void btnOpenTeachersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenTeachersActionPerformed
        getFrameManager().showSub(TEACHER);
    }//GEN-LAST:event_btnOpenTeachersActionPerformed

    private void btnOpenAssignmentsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenAssignmentsActionPerformed
        getFrameManager().showSub(ASSIGNMENT);
    }//GEN-LAST:event_btnOpenAssignmentsActionPerformed

    private void btnRemoveStudentFromGroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveStudentFromGroupActionPerformed
        removeStudentFromGroup();
    }//GEN-LAST:event_btnRemoveStudentFromGroupActionPerformed

    private void btnAddStudentToGroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddStudentToGroupActionPerformed
        addStudentToGroup();
    }//GEN-LAST:event_btnAddStudentToGroupActionPerformed

    private void btnOpenStudentsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenStudentsActionPerformed
        getFrameManager().showSub(STUDENT);
    }//GEN-LAST:event_btnOpenStudentsActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ApplicationContext context = SpringGuiRunner.run(GtiRecordDesktopGuiApp.class, args);
                FrameManager manager = context.getBean(FrameManager.class);
                manager.showSub(FrameManager.FrameType.GROUP);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnAddModuleToGroup;
    private javax.swing.JButton btnAddStudentToGroup;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnOpenAssignments;
    private javax.swing.JButton btnOpenModules;
    private javax.swing.JButton btnOpenStudents;
    private javax.swing.JButton btnOpenTeachers;
    private javax.swing.JButton btnReload;
    private javax.swing.JButton btnRemoveModuleFromGroup;
    private javax.swing.JButton btnRemoveStudentFromGroup;
    private javax.swing.JButton btnSave;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel jTitle;
    private javax.swing.JLabel lblGroupModulesTitle;
    private javax.swing.JLabel lblGroupStudentsTitle;
    private javax.swing.JPanel pnlAddRemoveModules;
    private javax.swing.JPanel pnlAddRemoveStudents;
    private javax.swing.JPanel pnlAllModules;
    private javax.swing.JPanel pnlAllStudents;
    private javax.swing.JPanel pnlControls;
    private javax.swing.JPanel pnlGroupFilter;
    private javax.swing.JPanel pnlGroupModules;
    private javax.swing.JPanel pnlGroupStudents;
    private javax.swing.JPanel pnlModuleFilter;
    private javax.swing.JPanel pnlModules;
    private javax.swing.JPanel pnlStudentFilter;
    private javax.swing.JPanel pnlStudents;
    private PaddedJTable tblAllModules;
    private PaddedJTable tblAllStudents;
    private PaddedJTable tblGroup;
    private PaddedJTable tblGroupModules;
    private PaddedJTable tblGroupStudents;
    private javax.swing.JTextField tfCourseFilter;
    private javax.swing.JTextField tfModuleFilter;
    private javax.swing.JTextField tfStudentFilter;
    // End of variables declaration//GEN-END:variables

    @Override
    protected PaddedJTable getTable() {
        return tblGroup;
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
        return GROUP_COLUMNS.NAME.index;
    }

    @Override
    protected Group createDataInstance() {
        return InstanceFactory.create(Group.class);
    }

    @Override
    protected void doReloadData() {
        courseCombo.removeAllItems();
        courseService.getAll().forEach(courseCombo::addItem);

        groupService.getAll().forEach(group -> {
            getTableModel().addRow(group, new Object[]{
                    group.getId(), group.getName(), group.getCode(), group.getCourse()
            });
        });

        if (getTableModel().getDataList().contains(selectedGroup)) {
            int viewRow = tblGroup.convertRowIndexToView(getTableModel().getDataList().indexOf(selectedGroup));
            tblGroup.setRowSelectionInterval(viewRow, viewRow);
        } else {
            selectedGroup = null;
        }
    }

    @Override
    protected Optional<Integer> doInsertData(Group data) {
        var groupIdOpt = groupService.insert(data);
        onGroupSelect();
        return groupIdOpt;
    }

    @Override
    protected void doUpdateData(Group data) {
        groupService.update(data);
        onGroupSelect();
    }

    @Override
    protected void doDeleteData(Group group) {
        if (group != null) {
            groupService.delete(group.getId());
        }
    }

    @Override
    protected boolean isDataValid(Group data) {
        return (data != null); // && (data.getName() != null) && ! data.getName().isBlank()
//                && data.getDepartment() != null && data.getCourseType() != null && data.getQqiLevel() != null;
    }

    @Override
    protected void fillDataObjectFromTable(Group group, Integer row) {
        if (getTable().getValueAt(row, GROUP_COLUMNS.ID.index) instanceof Integer id) {
            group.setId(id);
        }

        group.setName(getTableStringValueAt(row, GROUP_COLUMNS.NAME.index));
        group.setCode(getTableStringValueAt(row, GROUP_COLUMNS.CODE.index));

        group.setCourse((Course) getTable().getValueAt(row, GROUP_COLUMNS.COURSE.index));
    }

    @Override
    protected void addEmptyRowToModel() {
        getTableModel().addRow(createDataInstance(), new Object[]{null, "", "", null});
    }

}
