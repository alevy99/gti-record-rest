/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ie.gti.asdl.rey.gtirecord.desktop.ui.frame;

import ie.gti.asdl.rey.gtirecord.core.ServiceManager;
import ie.gti.asdl.rey.gtirecord.core.service.ModuleService;
import ie.gti.asdl.rey.gtirecord.core.service.StudentAssignmentService;
import ie.gti.asdl.rey.gtirecord.core.service.UserService;
import ie.gti.asdl.rey.gtirecord.desktop.GtiRecordDesktopGuiApp;
import ie.gti.asdl.rey.gtirecord.desktop.ui.AbstractTableDataFrame;
import ie.gti.asdl.rey.gtirecord.desktop.ui.FrameManager;
import ie.gti.asdl.rey.gtirecord.desktop.ui.component.DataTableModel;
import ie.gti.asdl.rey.gtirecord.desktop.ui.component.PaddedJTable;
import ie.gti.asdl.rey.gtirecord.desktop.util.SpringGuiRunner;
import ie.gti.asdl.rey.gtirecord.desktop.util.SwingUIUtils;
import ie.gti.asdl.rey.gtirecord.model.annotation.DescriptionUtil;
import ie.gti.asdl.rey.gtirecord.model.annotation.InstanceFactory;
import ie.gti.asdl.rey.gtirecord.model.entity.*;
import ie.gti.asdl.rey.gtirecord.model.entity.Module;
import ie.gti.asdl.rey.gtirecord.model.entity.add.StudentAssignmentStats;
import lombok.Setter;
import org.springframework.context.ApplicationContext;

import javax.swing.*;

import java.util.Arrays;
import java.util.Optional;

import static ie.gti.asdl.rey.gtirecord.core.util.AssignmentUtils.calcGradePercent;
import static ie.gti.asdl.rey.gtirecord.core.util.AssignmentUtils.calcWeightingTotalPercent;
import static ie.gti.asdl.rey.gtirecord.desktop.ui.FrameManager.FrameType.*;
import static ie.gti.asdl.rey.gtirecord.desktop.util.SwingUIUtils.createSafeListSelectionListener;

/**
 *
 * @author Andrei
 */
public class StudentReportFrame extends AbstractTableDataFrame<Module> {

    private enum MODULE_COLUMNS {
        ID(0), NAME(1), CODE(2);

        final int index;
        MODULE_COLUMNS(int index) {
            this.index = index;
        }
    }

    private static int assignColCnt = 0;

    private enum ASSIGNMENT_COLUMNS {
        ASSIGNMENT(assignColCnt++), SUBMITTED(assignColCnt++), GRADED(assignColCnt++), GRADE(assignColCnt++),
        GRADE_PERCENTAGE(assignColCnt++), MAX_GRADE(assignColCnt++), WEIGHTING(assignColCnt++), MAX_WEIGHTING(assignColCnt++);

        final int index;
        ASSIGNMENT_COLUMNS(int index) {
            this.index = index;
        }
    }

    @Setter
    private Student student;

    private Module selectedModule;

    private final ModuleService moduleService;

    private static final JButton DUMMY_JBUTTON = new JButton();

    private final StudentAssignmentService studentAssignmentService;

    private Integer highlightedRow;

    /**
     * Creates a new form StudentReportFrame
     */
    public StudentReportFrame(FrameManager frameManager, ServiceManager serviceManager) {
        super(frameManager);
//        groupModuleService = serviceManager.getGroupModuleService();
//        assignmentService = serviceManager.getAssignmentService();
//        groupService = serviceManager.getGroupService();
        studentAssignmentService = serviceManager.getStudentAssignmentService();
        moduleService = serviceManager.getModuleService();
        initComponents();
        initFrame();
    }

    @Override
    protected void initFrame() {
        ModuleFrame.initTable(getTable(), false); // Init Table Model first
        super.initFrame();

        tblModule.setHighlightedRowSupplier(() -> highlightedRow);

        tblModule.getSelectionModel().addListSelectionListener(
                createSafeListSelectionListener(event -> onModuleSelect()));

        initStudentAssignmentTable();
    }

    private void initStudentAssignmentTable() {
        if (!(tblStudentAssignment.getModel() instanceof DataTableModel<?>)) {
            tblStudentAssignment.setModel(new DataTableModel<StudentAssignment>(
                    new Object [][] {

                    },
                    new String [] {
                            "Assignment", "Submitted", "Graded", "Grade", "Grade, %", "Max grade", "Weighting, %", "Max weighting, %"
                    }
            ) {
                Class[] types = new Class [] {
                        java.lang.Object.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Integer.class, java.lang.Double.class, java.lang.Integer.class, java.lang.Double.class, java.lang.Integer.class
                };

                public Class getColumnClass(int columnIndex) {
                    return types [columnIndex];
                }

                public boolean isCellEditable(int rowIndex, int columnIndex) {
                    return false;
                }
            });
        }

        SwingUIUtils.addTableFilter(tblStudentAssignment, tfAssignmentsFilter);

        var columnModel = tblStudentAssignment.getColumnModel();

        // MAX width
        columnModel.getColumn(ASSIGNMENT_COLUMNS.SUBMITTED.index)       .setMaxWidth(150);
        columnModel.getColumn(ASSIGNMENT_COLUMNS.GRADED.index)          .setMaxWidth(150);
        columnModel.getColumn(ASSIGNMENT_COLUMNS.GRADE.index)           .setMaxWidth(150);
        columnModel.getColumn(ASSIGNMENT_COLUMNS.GRADE_PERCENTAGE.index).setMaxWidth(150);
        columnModel.getColumn(ASSIGNMENT_COLUMNS.MAX_GRADE.index)       .setMaxWidth(150);
        columnModel.getColumn(ASSIGNMENT_COLUMNS.WEIGHTING.index)       .setMaxWidth(150);
        columnModel.getColumn(ASSIGNMENT_COLUMNS.MAX_WEIGHTING.index)   .setMaxWidth(150);

        // MIN width
        columnModel.getColumn(ASSIGNMENT_COLUMNS.ASSIGNMENT.index)      .setMinWidth(250);
        columnModel.getColumn(ASSIGNMENT_COLUMNS.SUBMITTED.index)       .setMinWidth(150);
        columnModel.getColumn(ASSIGNMENT_COLUMNS.GRADED.index)          .setMinWidth(150);
        columnModel.getColumn(ASSIGNMENT_COLUMNS.GRADE.index)           .setMinWidth(150);
        columnModel.getColumn(ASSIGNMENT_COLUMNS.GRADE_PERCENTAGE.index).setMinWidth(150);
        columnModel.getColumn(ASSIGNMENT_COLUMNS.MAX_GRADE.index)       .setMinWidth(150);
        columnModel.getColumn(ASSIGNMENT_COLUMNS.WEIGHTING.index)       .setMinWidth(150);
        columnModel.getColumn(ASSIGNMENT_COLUMNS.MAX_WEIGHTING.index)   .setMinWidth(150);

//        TableColumn asColumn = tblStudentAssignment.getColumnModel().getColumn(ASSIGNMENT_COLUMNS.ASSIGNMENT.index);
//        studentColumn.setCellRenderer(new PaddedDataCellRenderer(null));
    }

    private void onModuleSelect() {
        tblStudentAssignment.clear();

        Arrays.stream(tblModule.getSelectedRows()).findFirst().ifPresentOrElse(row -> {

            highlightedRow = row; // Set new highlighted row
            tblModule.repaint(); // Repaint after we changed highlightedRow

            int modelRow = tblModule.convertRowIndexToModel(row);
            selectedModule = getTableModel().getData(modelRow);

            lblStudentResultTitle.setText(getReportTitle());

            StudentAssignmentStats stats = new StudentAssignmentStats();

            studentAssignmentService.getByStudentPersonIdAndModuleId(student.getPerson().getId(), selectedModule.getId())
                    .forEach(sa -> {
                        Assignment assignment = sa.getAssignment();
                        Double gradePercent = calcGradePercent(assignment, sa.getGrade());
                        Double weighting = calcWeightingTotalPercent(assignment, sa.getGrade());
                        getStudentAssignmentTableModel().addRow(sa, new Object[]{
                                assignment.getName(), sa.getIsSubmitted(), sa.getIsGraded(),
                                sa.getGrade(), gradePercent, assignment.getMaxGrade(),
                                weighting, assignment.getWeighting() });

                        stats.addGradeTotal(sa.getGrade() == null ? 0 : sa.getGrade());
                        stats.addMaxGradeTotal(assignment.getMaxGrade() == null ? 0 : assignment.getMaxGrade());
                        stats.addWeightingTotalPercent(weighting == null ? 0 : weighting);
                        stats.addMaxWeightingTotalPercent(assignment.getWeighting() == null ? 0 : assignment.getWeighting());
                    });

//            double totalGradePercent = stats.getMaxGradeTotal() == 0 ? 0 : 100.0 * stats.getGradeTotal() / stats.getMaxGradeTotal();
            getStudentAssignmentTableModel().addRow(InstanceFactory.create(StudentAssignment.class), new Object[]{
                    "<html><b>STATISTICS</b></html>", false, false,
                    stats.getGradeTotal(), stats.getGradeTotalPercent(), stats.getMaxGradeTotal(),
                    stats.getWeightingTotalPercent(), stats.getMaxWeightingTotalPercent() });
        }, () -> {
            lblStudentResultTitle.setText("Students results for module");
        });
        updateButtonsUI();
    }

    @Override
    protected void updateUI() {
        super.updateUI();
        updateButtonsUI();
    }

    private void updateButtonsUI() {
        btnSavePdfReport.setEnabled(selectedModule != null);
        btnSaveWordReport.setEnabled(selectedModule != null);
    }

    @Override
    protected void reloadTableData() {
        super.reloadTableData();
        lblLoggedInUsername.setText(getFrameManager().getActiveUser().getUsername());
    }

    protected DataTableModel<StudentAssignment> getStudentAssignmentTableModel() {
        return (DataTableModel<StudentAssignment>) tblStudentAssignment.getModel();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        lblReportTitle = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblModule = new PaddedJTable();
        pnlStudentResults = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblStudentAssignment = new PaddedJTable();
        lblStudentResultTitle = new javax.swing.JLabel();
        btnSavePdfReport = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        btnSaveWordReport = new javax.swing.JButton();
        pnlControls = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        tfAssignmentsFilter = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        tfModuleFilter = new javax.swing.JTextField();
        btnOpenGroups = new javax.swing.JButton();
        btnOpenModules = new javax.swing.JButton();
        btnOpenStudents = new javax.swing.JButton();
        btnOpenAssignments = new javax.swing.JButton();
        pnlLoggedInAs4 = new javax.swing.JPanel();
        lblLoggedInUsername = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setPreferredSize(new java.awt.Dimension(1033, 755));

        lblReportTitle.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblReportTitle.setForeground(new java.awt.Color(0, 51, 204));
        lblReportTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblReportTitle.setText("REPORT");

        tblModule.setAutoCreateRowSorter(true);
        tblModule.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        tblModule.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Module name", "Code"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblModule);

        pnlStudentResults.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        tblStudentAssignment.setAutoCreateRowSorter(true);
        tblStudentAssignment.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Assignment", "Submitted", "Graded", "Grade", "Grade, %", "Max grade", "Weighting, %", "Max weighting, %"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Integer.class, java.lang.Double.class, java.lang.Integer.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tblStudentAssignment);

        lblStudentResultTitle.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblStudentResultTitle.setForeground(new java.awt.Color(0, 51, 204));
        lblStudentResultTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblStudentResultTitle.setText("Student's results for the \"OOP\" module");

        btnSavePdfReport.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnSavePdfReport.setForeground(new java.awt.Color(0, 51, 204));
        btnSavePdfReport.setText("Save PDF Report");
        btnSavePdfReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSavePdfReportActionPerformed(evt);
            }
        });

        btnClose.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnClose.setForeground(new java.awt.Color(0, 51, 204));
        btnClose.setText("Close");
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });

        btnSaveWordReport.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnSaveWordReport.setForeground(new java.awt.Color(0, 51, 204));
        btnSaveWordReport.setText("Save Word Report");
        btnSaveWordReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveWordReportActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlStudentResultsLayout = new javax.swing.GroupLayout(pnlStudentResults);
        pnlStudentResults.setLayout(pnlStudentResultsLayout);
        pnlStudentResultsLayout.setHorizontalGroup(
            pnlStudentResultsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlStudentResultsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlStudentResultsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addComponent(lblStudentResultTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlStudentResultsLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnSavePdfReport, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(55, 55, 55)
                        .addComponent(btnSaveWordReport, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(238, 238, 238)
                        .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        pnlStudentResultsLayout.setVerticalGroup(
            pnlStudentResultsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlStudentResultsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblStudentResultTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlStudentResultsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnClose, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                    .addComponent(btnSavePdfReport, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSaveWordReport, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pnlControls.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 3, 15)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 51, 204));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel6.setText("Assignment filter");

        tfAssignmentsFilter.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        tfAssignmentsFilter.setForeground(new java.awt.Color(0, 51, 204));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 3, 15)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 51, 204));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel2.setText("Module filter");

        tfModuleFilter.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        tfModuleFilter.setForeground(new java.awt.Color(0, 51, 204));

        javax.swing.GroupLayout pnlControlsLayout = new javax.swing.GroupLayout(pnlControls);
        pnlControls.setLayout(pnlControlsLayout);
        pnlControlsLayout.setHorizontalGroup(
            pnlControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlControlsLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tfModuleFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(74, 74, 74)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tfAssignmentsFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(90, 90, 90))
        );
        pnlControlsLayout.setVerticalGroup(
            pnlControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(tfAssignmentsFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel6)
                .addComponent(tfModuleFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel2))
        );

        btnOpenGroups.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnOpenGroups.setForeground(new java.awt.Color(0, 51, 204));
        btnOpenGroups.setText("Groups");
        btnOpenGroups.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenGroupsActionPerformed(evt);
            }
        });

        btnOpenModules.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnOpenModules.setForeground(new java.awt.Color(0, 51, 204));
        btnOpenModules.setText("Modules");
        btnOpenModules.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenModulesActionPerformed(evt);
            }
        });

        btnOpenStudents.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnOpenStudents.setForeground(new java.awt.Color(0, 51, 204));
        btnOpenStudents.setText("Students");
        btnOpenStudents.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenStudentsActionPerformed(evt);
            }
        });

        btnOpenAssignments.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnOpenAssignments.setForeground(new java.awt.Color(0, 51, 204));
        btnOpenAssignments.setText("Assignments");
        btnOpenAssignments.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenAssignmentsActionPerformed(evt);
            }
        });

        lblLoggedInUsername.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblLoggedInUsername.setForeground(new java.awt.Color(255, 0, 0));
        lblLoggedInUsername.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblLoggedInUsername.setText("logged in as ");

        jLabel18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/profile.png"))); // NOI18N

        javax.swing.GroupLayout pnlLoggedInAs4Layout = new javax.swing.GroupLayout(pnlLoggedInAs4);
        pnlLoggedInAs4.setLayout(pnlLoggedInAs4Layout);
        pnlLoggedInAs4Layout.setHorizontalGroup(
            pnlLoggedInAs4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLoggedInAs4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblLoggedInUsername, javax.swing.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlLoggedInAs4Layout.setVerticalGroup(
            pnlLoggedInAs4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 53, Short.MAX_VALUE)
            .addGroup(pnlLoggedInAs4Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(lblLoggedInUsername)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlStudentResults, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblReportTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(pnlLoggedInAs4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(pnlControls, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnOpenAssignments, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnOpenStudents, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnOpenModules, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnOpenGroups)))))
                .addGap(24, 24, 24))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(pnlLoggedInAs4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblReportTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnOpenGroups, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnOpenModules, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnOpenStudents, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnOpenAssignments, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(pnlControls, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlStudentResults, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 1324, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 774, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        getFrameManager().showParent();
    }//GEN-LAST:event_btnCloseActionPerformed

    private void btnOpenGroupsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenGroupsActionPerformed
        getFrameManager().showSub(GROUP);
    }//GEN-LAST:event_btnOpenGroupsActionPerformed

    private void btnOpenModulesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenModulesActionPerformed
        getFrameManager().showSub(MODULE);
    }//GEN-LAST:event_btnOpenModulesActionPerformed

    private void btnOpenStudentsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenStudentsActionPerformed
        getFrameManager().showSub(STUDENT);
    }//GEN-LAST:event_btnOpenStudentsActionPerformed

    private void btnSavePdfReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSavePdfReportActionPerformed
        if (student == null || selectedModule == null) {
            return;
        }
        exportTablePdfReport(tblStudentAssignment, getReportTitle());
    }//GEN-LAST:event_btnSavePdfReportActionPerformed

    private void btnOpenAssignmentsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenAssignmentsActionPerformed
        getFrameManager().showSub(ASSIGNMENT);
    }//GEN-LAST:event_btnOpenAssignmentsActionPerformed

    private void btnSaveWordReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveWordReportActionPerformed
        if (student == null || selectedModule == null) {
            return;
        }
        exportTableWordReport(tblStudentAssignment, getReportTitle());
    }//GEN-LAST:event_btnSaveWordReportActionPerformed

    private String getReportTitle() {
        if (student == null || selectedModule == null) {
            return "Student report for module";
        }
        return String.format("Student report for %s. Module '%s'",
                DescriptionUtil.getShortDescription(student),
                DescriptionUtil.getShortDescription(selectedModule));
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
                manager.setActiveUser(context.getBean(UserService.class).getByUsername("johnm").orElse(null));
                StudentReportFrame studentReportFrame = manager.getFrame(STUDENT_REPORT);
                Student student = InstanceFactory.create(Student.class);
                student.getPerson().setId(6);
                studentReportFrame.setStudent(student);
                manager.showSub(STUDENT_REPORT);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnOpenAssignments;
    private javax.swing.JButton btnOpenGroups;
    private javax.swing.JButton btnOpenModules;
    private javax.swing.JButton btnOpenStudents;
    private javax.swing.JButton btnSavePdfReport;
    private javax.swing.JButton btnSaveWordReport;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblLoggedInUsername;
    private javax.swing.JLabel lblReportTitle;
    private javax.swing.JLabel lblStudentResultTitle;
    private javax.swing.JPanel pnlControls;
    private javax.swing.JPanel pnlLoggedInAs4;
    private javax.swing.JPanel pnlStudentResults;
    private PaddedJTable tblModule;
    private PaddedJTable tblStudentAssignment;
    private javax.swing.JTextField tfAssignmentsFilter;
    private javax.swing.JTextField tfModuleFilter;
    // End of variables declaration//GEN-END:variables

    @Override
    protected PaddedJTable getTable() {
        return tblModule;
    }

    @Override
    protected JButton getAddBtn() {
        return DUMMY_JBUTTON;
    }

    @Override
    protected JButton getDeleteBtn() {
        return DUMMY_JBUTTON;
    }

    @Override
    protected JButton getSaveBtn() {
        return DUMMY_JBUTTON;
    }

    @Override
    protected JTextField getTableFilterField() {
        return tfModuleFilter;
    }

    @Override
    protected int getDataDescriptionColumn() {
        return MODULE_COLUMNS.NAME.index;
    }

    @Override
    protected boolean getIsEditable() {
        return false;
    }

    @Override
    protected Module createDataInstance() {
        return InstanceFactory.create(Module.class);
    }

    @Override
    protected void doReloadData() {
        lblReportTitle.setText(String.format("Student report for %s", DescriptionUtil.getShortDescription(student)));
        lblStudentResultTitle.setText("Students results for module");

        moduleService.getByGroupId(student.getGroup().getId()).forEach(module -> {
            getTableModel().addRow(module, new Object[]{module.getId(), module.getName(), module.getCode()});
        });
    }

    @Override
    protected Optional<Integer> doInsertData(Module data) {
        return Optional.empty();
    }

    @Override
    protected void doUpdateData(Module data) {
    }

    @Override
    protected void doDeleteData(Module data) {
    }

    @Override
    protected boolean isDataValid(Module data) {
        return (data != null); // && (data.getName() != null) && ! data.getName().isBlank()
//                && data.getDepartment() != null && data.getCourseType() != null && data.getQqiLevel() != null;
    }

    @Override
    protected void fillDataObjectFromTable(Module assignment, Integer row) {
    }

    @Override
    protected void addEmptyRowToModel() {
    }
}
