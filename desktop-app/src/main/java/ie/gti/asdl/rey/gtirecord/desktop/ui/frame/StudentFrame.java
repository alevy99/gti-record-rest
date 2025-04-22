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
import ie.gti.asdl.rey.gtirecord.model.annotation.DescriptionUtil;
import ie.gti.asdl.rey.gtirecord.model.annotation.InstanceFactory;
import ie.gti.asdl.rey.gtirecord.model.entity.*;
import ie.gti.asdl.rey.gtirecord.model.util.Pair;
import org.springframework.context.ApplicationContext;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import java.util.*;
import java.util.stream.Collectors;

import static ie.gti.asdl.rey.gtirecord.desktop.ui.FrameManager.FrameType.*;
import static ie.gti.asdl.rey.gtirecord.desktop.util.SwingUIUtils.createSafeListSelectionListener;

/**
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

    private final StudentService studentService;

    private final UserService userService;

    private final DepartmentService departmentService;

    private final CourseService courseService;

    private final GroupService groupService;


    private Pair<Student, User> selectedPair;

    private Map<Department, List<Course>> coursesByDepartment = new HashMap<>();
    private Map<Course, List<Group>> groupsByCourse = new HashMap<>();
    private Map<Group, Department> departmentsByGroup = new HashMap<>();

    private Department selectedDepartment;

    private Integer highlightedRow;

    /**
     * Creates new form TeacherFrame
     */
    public StudentFrame(FrameManager frameManager, ServiceManager serviceManager) {
        super(frameManager);
        studentService = serviceManager.getStudentService();
        departmentService = serviceManager.getDepartmentService();
        groupService = serviceManager.getGroupService();
        courseService = serviceManager.getCourseService();
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
        columnModel.getColumn(COLUMNS.PERSON_ID.index).setMaxWidth(60);
        columnModel.getColumn(COLUMNS.FIRST_NAME.index).setMinWidth(80);
        columnModel.getColumn(COLUMNS.LAST_NAME.index).setMinWidth(80);
        columnModel.getColumn(COLUMNS.USERNAME.index).setMinWidth(60);
        columnModel.getColumn(COLUMNS.PASSWORD.index).setMinWidth(60);
        columnModel.getColumn(COLUMNS.GROUP.index).setMinWidth(80);
        columnModel.getColumn(COLUMNS.EDUCATION.index).setMaxWidth(80);
        columnModel.getColumn(COLUMNS.ON_ERASMUS.index).setMinWidth(40);
        columnModel.getColumn(COLUMNS.EMERGENCY_CONTACTS.index).setMaxWidth(60);

        tblStudent.getColumnModel().getColumn(COLUMNS.PASSWORD.index).setCellRenderer(new PasswordCellRenderer(() -> highlightedRow));
        tblStudent.getColumnModel().getColumn(COLUMNS.PASSWORD.index).setCellEditor(new PasswordCellEditor());

        // Init module table
        tblStudent.getSelectionModel().addListSelectionListener(createSafeListSelectionListener(event -> onSelectStudent()));

        initGroupFilterTables();
    }

    private void initGroupFilterTables() {
        initGroupFilterTableModels();

        tblDepartment.getSelectionModel().addListSelectionListener(tblDepartment.getListSelectionListener().createSafeListener(listener -> onSelectDepartment()));
        tblCourse.getSelectionModel().addListSelectionListener(tblCourse.getListSelectionListener().createSafeListener(listener -> onSelectCourse()));
        tblGroup.getSelectionModel().addListSelectionListener(tblGroup.getListSelectionListener().createSafeListener(listener -> onSelectGroup()));
    }

    private void onSelectStudent() {
        reloadStudent();
        updateGroupFilterTableUI();
    }

    private void onSelectDepartment() {
        switch (tblDepartment.getSelectedRowCount()) {
            case 0 -> {
                tblCourse.clear();
                tblGroup.clear();
            }
            case 1 -> {
                selectedDepartment = getDeparmentTableModel().getData(tblDepartment.convertRowIndexToModel(tblDepartment.getSelectedRow()));

                tblCourse.clear();
                tblGroup.clear(); // Clear groups as well
                Optional.ofNullable(coursesByDepartment.get(selectedDepartment))
                        .stream()
                        .flatMap(List::stream)
                        .forEach(course -> {
                            getCourseTableModel().addRow(course, new Object[]{
                                    course.getId(), course.getName(), course.getCode(),
                                    course.getCourseType(), course.getQqiLevel()
                            });
                            if (course.equals(selectedPair.getValue1().getGroup().getCourse())) {
                                int lastViewRow = tblCourse.getRowCount() - 1;
                                tblCourse.setRowSelectionInterval(lastViewRow, lastViewRow);
                            }
                        });
            }
            default ->
                    throw new IllegalStateException("Too many departments selected: " + tblDepartment.getSelectedRowCount());
        }
    }

    private void onSelectCourse() {
        switch (tblCourse.getSelectedRowCount()) {
            case 0 -> {
                tblGroup.clear();
            }
            case 1 -> {
                Course selectedCourse = getCourseTableModel().getData(tblCourse.convertRowIndexToModel(tblCourse.getSelectedRow()));
                tblGroup.clear();

                Optional.ofNullable(groupsByCourse.get(selectedCourse))
                        .stream()
                        .flatMap(List::stream)
                        .forEach(group -> {
                            getGroupTableModel().addRow(group, new Object[]{
                                    group.getId(), group.getName(), group.getCode()
                            });
                            // Select group, if it is the one we have selected
                            if (group.equals(selectedPair.getValue1().getGroup())) {
                                int lastViewRow = tblGroup.getRowCount() - 1;
                                tblGroup.setRowSelectionInterval(lastViewRow, lastViewRow);
                            }
                        });
            }
        }
    }

    private void onSelectGroup() {
        if (selectedPair == null) return;
        Group selectedGroup;
        switch (tblGroup.getSelectedRowCount()) {
            case 0 -> {
                selectedGroup = new Group();
            }
            case 1 -> {
                selectedGroup = getGroupTableModel().getData(tblGroup.convertRowIndexToModel(tblGroup.getSelectedRow()));
            }
            default -> {
                return;
            }
        }
        selectedPair.getValue1().setGroup(selectedGroup);
        getTableModel().setValueAt(DescriptionUtil.getShortDescription(selectedGroup),
                tblStudent.convertRowIndexToModel(tblStudent.getSelectedRow()), COLUMNS.GROUP.index);
    }

    private boolean isSelectedSavedData() {
        return (selectedPair != null)
                && (selectedPair.getValue1() != null)
                && (selectedPair.getValue1().getPerson() != null)
                && (selectedPair.getValue1().getPerson().getId() != null);
    }

    @Override
    protected void updateUI() {
        super.updateUI();
        var selectedSavedData = isSelectedSavedData();
        btnPersonInfo.setEnabled(selectedSavedData);

        var selected = tblStudent.getSelectedRowCount() > 0;
        tblDepartment.setEnabled(selected);
        tblCourse.setEnabled(selected);
        tblGroup.setEnabled(selected);
    }

    private void clearGroupFilterSelection() {
        tblDepartment.clearSelection();
        tblCourse.clearSelection();
        tblGroup.clearSelection();
    }

    private void reloadStudent() {
        // Show modules for the first teacher
        Arrays.stream(tblStudent.getSelectedRows()).findFirst().ifPresent(row -> {
            highlightedRow = row; // Set new highlighted row
            tblStudent.repaint(); // Repaint after we changed highlightedRow

            selectedPair = getTableModel().getData(tblStudent.convertRowIndexToModel(row));

            Student student = selectedPair.getValue1();
            selectedDepartment = departmentsByGroup.getOrDefault(student.getGroup(), new Department());
            getDeparmentTableModel().getDataRow(selectedDepartment).ifPresentOrElse(modelRow -> {
                int viewRow = tblDepartment.convertRowIndexToView(modelRow);
                tblDepartment.setRowSelectionInterval(viewRow, viewRow);
                onSelectDepartment();
                getCourseTableModel().getDataRow(student.getGroup().getCourse()).ifPresentOrElse(courseModelRow -> {
                    int viewCourseRow = tblCourse.convertRowIndexToView(courseModelRow);
                    tblCourse.setRowSelectionInterval(viewCourseRow, viewCourseRow);
                    onSelectCourse();
                    getGroupTableModel().getDataRow(student.getGroup()).ifPresentOrElse(groupModelRow -> {
                        int viewGroupRow = tblGroup.convertRowIndexToView(groupModelRow);
                        tblGroup.setRowSelectionInterval(viewGroupRow, viewGroupRow);
                        onSelectGroup();
                    }, this::clearGroupFilterSelection);
                }, this::clearGroupFilterSelection);
            }, this::clearGroupFilterSelection);
        });
    }

    private void initTableModel() {
        if (!(getTable().getModel() instanceof DataTableModel)) {
            getTable().setModel(new DataTableModel<Pair<Student, User>>(
                    new Object[][]{

                    },
                    new String[]{
                            "Person ID", "First name", "Last name", "Username", "Password", "Group", "Education", "On Erasmus", "Contacts"
                    }
            ) {
                Class[] types = new Class[]{
                        java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Boolean.class, java.lang.String.class
                };
                boolean[] canEdit = new boolean[]{
                        false, true, true, true, true, false, true, true, true
                };

                public Class getColumnClass(int columnIndex) {
                    return types[columnIndex];
                }

                public boolean isCellEditable(int rowIndex, int columnIndex) {
                    return canEdit[columnIndex];
                }
            });
        }
    }

    protected DataTableModel<Department> getDeparmentTableModel() {
        return (DataTableModel<Department>) tblDepartment.getModel();
    }

    protected DataTableModel<Course> getCourseTableModel() {
        return (DataTableModel<Course>) tblCourse.getModel();
    }

    protected DataTableModel<Group> getGroupTableModel() {
        return (DataTableModel<Group>) tblGroup.getModel();
    }

    private void initGroupFilterTableModels() {
        initDepartmentTableModel();
        initCourseTableModel();
        initGroupTableModel();
    }

    private void initDepartmentTableModel() {
        if (!(tblDepartment.getModel() instanceof DataTableModel)) {
            tblDepartment.setModel(new DepartmentTableModel(false));
        }

        TableColumnModel columnModel = tblDepartment.getColumnModel();
        columnModel.getColumn(0).setMaxWidth(30);
        columnModel.getColumn(1).setMinWidth(160);
    }

    private void initCourseTableModel() {
        if (!(tblCourse.getModel() instanceof DataTableModel)) {
            tblCourse.setModel(new DataTableModel<Course>(
                    new Object[][]{
                    },
                    new String[]{
                            "ID", "Name", "Code", "Type", "QQI"
                    }
            ) {
                Class[] types = new Class[]{
                        Integer.class, String.class, String.class, String.class, String.class
                };

                public Class getColumnClass(int columnIndex) {
                    return types[columnIndex];
                }

                public boolean isCellEditable(int rowIndex, int columnIndex) {
                    return false;
                }
            });
        }
        TableColumnModel columnModel = tblCourse.getColumnModel();
        columnModel.getColumn(0).setMaxWidth(30);
        columnModel.getColumn(1).setMinWidth(160);
        columnModel.getColumn(2).setMaxWidth(60);
        columnModel.getColumn(3).setMaxWidth(60);
        columnModel.getColumn(4).setMaxWidth(50);
    }

    private void initGroupTableModel() {
        if (!(tblGroup.getModel() instanceof DataTableModel)) {
            tblGroup.setModel(new GroupTableModel(false));
        }
        TableColumnModel columnModel = tblGroup.getColumnModel();
        columnModel.getColumn(0).setMaxWidth(30);
        columnModel.getColumn(1).setMinWidth(160);
        columnModel.getColumn(2).setMaxWidth(60);
    }

    private void updateGroupFilterTableUI() {
        if (tblStudent.getSelectedRowCount() == 0) {
            tblDepartment.clearSelection();
            tblCourse.clear();
            tblGroup.clear();
        }
    }

    @Override
    protected void onFrameShown() {
        super.onFrameShown();
        updateGroupFilterTableUI();
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
        pnlGroupFilter = new javax.swing.JPanel();
        pnlDepartments = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblDepartment = new PaddedJTable();
        btnOpenDepartments = new javax.swing.JButton();
        pnlCourses = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblCourse = new PaddedJTable();
        btnOpenCourses = new javax.swing.JButton();
        pnlGroups = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tblGroup = new PaddedJTable();
        btnOpenGroups = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jPanel1.setPreferredSize(new java.awt.Dimension(1033, 755));

        tblStudent.setAutoCreateRowSorter(true);
        tblStudent.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        tblStudent.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{

                },
                new String[]{
                        "Person ID", "First name", "Last name", "Username", "Password", "Group", "Education", "On Erasmus", "Contacts"
                }
        ) {
            Class[] types = new Class[]{
                    java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Boolean.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean[]{
                    false, true, true, true, true, false, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
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
        jLabel2.setText("Student filter:");

        tfStudentFilter.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        tfStudentFilter.setForeground(new java.awt.Color(0, 51, 204));

        javax.swing.GroupLayout jUpdatePanelLayout = new javax.swing.GroupLayout(jUpdatePanel);
        jUpdatePanel.setLayout(jUpdatePanelLayout);
        jUpdatePanelLayout.setHorizontalGroup(
                jUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jUpdatePanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(tfStudentFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(163, Short.MAX_VALUE))
        );
        jUpdatePanelLayout.setVerticalGroup(
                jUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jUpdatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel2)
                                .addComponent(tfStudentFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                                .addContainerGap(84, Short.MAX_VALUE))
        );
        pnlControlsLayout.setVerticalGroup(
                pnlControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlControlsLayout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(pnlControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlControlsLayout.createSequentialGroup()
                                                .addGroup(pnlControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlControlsLayout.createSequentialGroup()
                                                .addComponent(jUpdatePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(2, 2, 2)))
                                .addGroup(pnlControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnReload, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
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

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 51, 204));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("DEPARTMENTS");

        tblDepartment.setAutoCreateRowSorter(true);
        tblDepartment.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{

                },
                new String[]{
                        "ID", "Name"
                }
        ) {
            Class[] types = new Class[]{
                    java.lang.Integer.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean[]{
                    false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        tblDepartment.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblDepartment.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane2.setViewportView(tblDepartment);

        btnOpenDepartments.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnOpenDepartments.setForeground(new java.awt.Color(0, 51, 204));
        btnOpenDepartments.setText("Open");
        btnOpenDepartments.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenDepartmentsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlDepartmentsLayout = new javax.swing.GroupLayout(pnlDepartments);
        pnlDepartments.setLayout(pnlDepartmentsLayout);
        pnlDepartmentsLayout.setHorizontalGroup(
                pnlDepartmentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pnlDepartmentsLayout.createSequentialGroup()
                                .addGap(0, 0, 0)
                                .addGroup(pnlDepartmentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addGroup(pnlDepartmentsLayout.createSequentialGroup()
                                                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnOpenDepartments))
                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE))
        );
        pnlDepartmentsLayout.setVerticalGroup(
                pnlDepartmentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pnlDepartmentsLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(pnlDepartmentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel1)
                                        .addComponent(btnOpenDepartments))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 51, 204));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("COURSES");
        jLabel3.setMaximumSize(new java.awt.Dimension(45, 16));
        jLabel3.setMinimumSize(new java.awt.Dimension(45, 16));
        jLabel3.setPreferredSize(new java.awt.Dimension(45, 16));

        tblCourse.setAutoCreateRowSorter(true);
        tblCourse.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{

                },
                new String[]{
                        "ID", "Name", "Code", "Type", "QQI"
                }
        ) {
            Class[] types = new Class[]{
                    java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean[]{
                    false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        tblCourse.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblCourse.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane3.setViewportView(tblCourse);

        btnOpenCourses.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnOpenCourses.setForeground(new java.awt.Color(0, 51, 204));
        btnOpenCourses.setText("Open");
        btnOpenCourses.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenCoursesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlCoursesLayout = new javax.swing.GroupLayout(pnlCourses);
        pnlCourses.setLayout(pnlCoursesLayout);
        pnlCoursesLayout.setHorizontalGroup(
                pnlCoursesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pnlCoursesLayout.createSequentialGroup()
                                .addGap(0, 0, 0)
                                .addGroup(pnlCoursesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(pnlCoursesLayout.createSequentialGroup()
                                                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnOpenCourses))
                                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 455, Short.MAX_VALUE)))
        );
        pnlCoursesLayout.setVerticalGroup(
                pnlCoursesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pnlCoursesLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(pnlCoursesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnOpenCourses)
                                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 51, 204));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("GROUPS");

        tblGroup.setAutoCreateRowSorter(true);
        tblGroup.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{

                },
                new String[]{
                        "ID", "Name", "Code"
                }
        ) {
            Class[] types = new Class[]{
                    java.lang.Integer.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean[]{
                    false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        tblGroup.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblGroup.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane5.setViewportView(tblGroup);

        btnOpenGroups.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnOpenGroups.setForeground(new java.awt.Color(0, 51, 204));
        btnOpenGroups.setText("Open");
        btnOpenGroups.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenGroupsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlGroupsLayout = new javax.swing.GroupLayout(pnlGroups);
        pnlGroups.setLayout(pnlGroupsLayout);
        pnlGroupsLayout.setHorizontalGroup(
                pnlGroupsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pnlGroupsLayout.createSequentialGroup()
                                .addGap(0, 0, 0)
                                .addGroup(pnlGroupsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(pnlGroupsLayout.createSequentialGroup()
                                                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnOpenGroups))
                                        .addGroup(pnlGroupsLayout.createSequentialGroup()
                                                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(0, 0, Short.MAX_VALUE)))
                                .addGap(0, 0, 0))
        );
        pnlGroupsLayout.setVerticalGroup(
                pnlGroupsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pnlGroupsLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(pnlGroupsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel5)
                                        .addComponent(btnOpenGroups))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlGroupFilterLayout = new javax.swing.GroupLayout(pnlGroupFilter);
        pnlGroupFilter.setLayout(pnlGroupFilterLayout);
        pnlGroupFilterLayout.setHorizontalGroup(
                pnlGroupFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pnlGroupFilterLayout.createSequentialGroup()
                                .addComponent(pnlDepartments, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pnlCourses, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pnlGroups, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );
        pnlGroupFilterLayout.setVerticalGroup(
                pnlGroupFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pnlGroupFilterLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(pnlGroupFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(pnlCourses, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(pnlDepartments, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(pnlGroups, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap(17, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(pnlGroupFilter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                                .addComponent(pnlControls, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(btnPersonInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 203, Short.MAX_VALUE)
                                                        .addComponent(btnClose, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                                .addGap(9, 9, 9))
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(9, 9, 9)
                                .addComponent(jTitle)
                                .addGap(12, 12, 12)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(pnlControls, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                                .addGap(6, 6, 6)
                                                .addComponent(btnPersonInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(pnlGroupFilter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 1088, Short.MAX_VALUE)
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 770, Short.MAX_VALUE)
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

    private void btnOpenDepartmentsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenDepartmentsActionPerformed
        getFrameManager().showSub(DEPARTMENT);
    }//GEN-LAST:event_btnOpenDepartmentsActionPerformed

    private void btnOpenCoursesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenCoursesActionPerformed
        getFrameManager().showSub(COURSE);
    }//GEN-LAST:event_btnOpenCoursesActionPerformed

    private void btnOpenGroupsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenGroupsActionPerformed
        getFrameManager().showSub(GROUP);
    }//GEN-LAST:event_btnOpenGroupsActionPerformed

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
    private javax.swing.JButton btnOpenCourses;
    private javax.swing.JButton btnOpenDepartments;
    private javax.swing.JButton btnOpenGroups;
    private javax.swing.JButton btnPersonInfo;
    private javax.swing.JButton btnReload;
    private javax.swing.JButton btnSave;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JLabel jTitle;
    private javax.swing.JPanel jUpdatePanel;
    private javax.swing.JPanel pnlControls;
    private javax.swing.JPanel pnlCourses;
    private javax.swing.JPanel pnlDepartments;
    private javax.swing.JPanel pnlGroupFilter;
    private javax.swing.JPanel pnlGroups;
    private PaddedJTable tblCourse;
    private PaddedJTable tblDepartment;
    private PaddedJTable tblGroup;
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
        var student = InstanceFactory.create(Student.class);
        var user = InstanceFactory.create(User.class);
        user.getRoles().add(Role.RoleType.STUDENT.asRole());
        return new Pair<>(student, user);
    }

    @Override
    protected void doReloadData() {
        // Load students
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
                    DescriptionUtil.getShortDescription(student.getGroup()),
                    student.getEducation(), student.getOnErasmus() == null ? false : student.getOnErasmus(),
                    student.getEmergencyContacts()
            });
        });

        // Load departments
        reloadDepartments();

        // Update selected rows
        if (getTableModel().getDataList().contains(selectedPair)) {
            int viewRow = tblStudent.convertRowIndexToView(getTableModel().getDataList().indexOf(selectedPair));
            tblStudent.setRowSelectionInterval(viewRow, viewRow);
            onSelectStudent();
        } else {
            selectedPair = null;
        }
    }

    private void reloadDepartments() {
        tblDepartment.clear();

        departmentService.getAll().forEach(department -> {
            getDeparmentTableModel().addRow(department, new Object[]{department.getId(), department.getName()});
        });
        coursesByDepartment = courseService.getAllGroupedByDepartment();
        groupsByCourse = groupService.getAllGroupedByCourse();

        departmentsByGroup =
                coursesByDepartment.entrySet().stream()
                        .flatMap(entry -> {
                            Department department = entry.getKey();
                            List<Course> courses = entry.getValue();
                            return courses.stream()
                                    .flatMap(course -> groupsByCourse.getOrDefault(course, List.of()).stream() // List<Group>
                                            .map(group -> Map.entry(group, department))
                                    );
                        })
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

//        courseService.getAll().forEach(course -> {
//            getCourseTableModel().addRow(course, new Object[]{
//                    course.getId(), course.getName(), course.getCode(),
//                    course.getDepartment(), course.getCourseType(), course.getQqiLevel()
//            });
//        });
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
    protected void doDeleteData(Pair<Student, User> pair) {
        if (pair != null) {
            studentService.delete(pair.getValue1());
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
        getTableModel().addRow(createDataInstance(), new Object[]{null, "", "", "", null, "", "", false, ""});
    }

}
