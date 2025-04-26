package ie.gti.asdl.rey.gtirecord.core.service.impl;

import ie.gti.asdl.rey.gtirecord.core.dao.ModuleDao;
import ie.gti.asdl.rey.gtirecord.core.dao.StudentAssignmentDao;
import ie.gti.asdl.rey.gtirecord.core.service.StudentAssignmentService;
import ie.gti.asdl.rey.gtirecord.model.entity.Assignment;
import ie.gti.asdl.rey.gtirecord.model.entity.Module;
import ie.gti.asdl.rey.gtirecord.model.entity.Student;
import ie.gti.asdl.rey.gtirecord.model.entity.StudentAssignment;
import ie.gti.asdl.rey.gtirecord.model.entity.add.StudentAssignmentStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ie.gti.asdl.rey.gtirecord.core.util.AssignmentUtils.calcWeightingTotalPercent;

/**
 * @author Andrei Levchenko
 */
@Service
public class StudentAssignmentServiceImpl implements StudentAssignmentService {

    private final StudentAssignmentDao studentAssignmentDao;
    private final ModuleDao moduleDao;

    @Autowired
    public StudentAssignmentServiceImpl(StudentAssignmentDao studentAssignmentDao, ModuleDao moduleDao) {
        this.studentAssignmentDao = studentAssignmentDao;
        this.moduleDao = moduleDao;
    }

    @Override
    public List<StudentAssignment> getByStudentPersonId(Integer studentPersonId) {
        return studentAssignmentDao.getByStudentPersonId(studentPersonId);
    }

    @Override
    public List<StudentAssignment> getByStudentPersonIdAndModuleId(Integer studentPersonId, Integer moduleId) {
        return studentAssignmentDao.getByStudentPersonIdAndModuleId(studentPersonId, moduleId);
    }

    @Override
    public List<StudentAssignment> getByAssignmentId(Integer assignmentId) {
        return studentAssignmentDao.getByAssignmentId(assignmentId);
    }

    @Override
    public void update(StudentAssignment studentAssignment) {
        studentAssignmentDao.update(studentAssignment);
    }

    @Override
    public void deleteByAssignmentId(Integer assignmentId) {
        studentAssignmentDao.deleteByAssignmentId(assignmentId);
    }

    @Override
    public StudentAssignmentStats getStudentAssignmentStatsTotal(Student student) {
        return getStudentAssignmentStatsTotal(student.getPerson().getId(), moduleDao.getByGroupId(student.getGroup().getId()));
    }

    @Override
    public StudentAssignmentStats getStudentAssignmentStatsTotal(Integer studentId, List<Module> modules) {
        StudentAssignmentStats stats = new StudentAssignmentStats();
        modules.forEach(module -> {
            getByStudentPersonIdAndModuleId(studentId, module.getId())
                    .forEach(sa -> {
                        Assignment assignment = sa.getAssignment();
                        Double weighting = calcWeightingTotalPercent(assignment, sa.getGrade());
                        stats.addGradeTotal(sa.getGrade() == null ? 0 : sa.getGrade());
                        stats.addMaxGradeTotal(assignment.getMaxGrade() == null ? 0 : assignment.getMaxGrade());
                        stats.addWeightingTotal(weighting == null ? 0 : weighting);
                        stats.addMaxWeightingTotal(assignment.getWeighting() == null ? 0 : assignment.getWeighting());
                    });
        });
        return stats;
    }

    @Override
    public Map<Module, StudentAssignmentStats> getStudentAssignmentStats(Integer studentId, List<Module> modules) {
        Map<Module, StudentAssignmentStats> statsMap = new HashMap<>();
        modules.forEach(module -> {
            StudentAssignmentStats stats = new StudentAssignmentStats();
            getByStudentPersonIdAndModuleId(studentId, module.getId())
                    .forEach(sa -> {
                        Assignment assignment = sa.getAssignment();
                        Double weighting = calcWeightingTotalPercent(assignment, sa.getGrade());
                        stats.addGradeTotal(sa.getGrade() == null ? 0 : sa.getGrade());
                        stats.addMaxGradeTotal(assignment.getMaxGrade() == null ? 0 : assignment.getMaxGrade());
                        stats.addWeightingTotal(weighting == null ? 0 : weighting);
                        stats.addMaxWeightingTotal(assignment.getWeighting() == null ? 0 : assignment.getWeighting());
                    });
            statsMap.put(module, stats);
        });
        return statsMap;
    }

    @Override
    public Map<Student, Map<Module, StudentAssignmentStats>> getStudentAssignmentStats(List<Student> students, List<Module> modules) {
        Map<Student, Map<Module, StudentAssignmentStats>> studentModuleAssignmentMap = new HashMap<>();
        students.forEach(student -> {
            studentModuleAssignmentMap.put(student, getStudentAssignmentStats(student.getPerson().getId(), modules));
        });
        return studentModuleAssignmentMap;
    }
}
