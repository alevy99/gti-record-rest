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

import java.util.List;

import static ie.gti.asdl.rey.gtirecord.core.util.AssignmentUtils.calcGradePercent;
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
    public StudentAssignmentStats getStudentAssignmentStats(Student student) {
        return getStudentAssignmentStats(student.getPerson().getId(), moduleDao.getByGroupId(student.getGroup().getId()));
    }

    @Override
    public StudentAssignmentStats getStudentAssignmentStats(Integer studentId, List<Module> modules) {
        StudentAssignmentStats stats = new StudentAssignmentStats();
        modules.forEach(module -> {
            getByStudentPersonIdAndModuleId(studentId, module.getId())
                    .forEach(sa -> {
                        Assignment assignment = sa.getAssignment();
                        Double weighting = calcWeightingTotalPercent(assignment, sa.getGrade());
                        stats.addGradeTotal(sa.getGrade() == null ? 0 : sa.getGrade());
                        stats.addMaxGradeTotal(assignment.getMaxGrade() == null ? 0 : assignment.getMaxGrade());
                        stats.addWeightingTotalPercent(weighting == null ? 0 : weighting);
                        stats.addMaxWeightingTotalPercent(assignment.getWeighting() == null ? 0 : assignment.getWeighting());
                    });
        });
        return stats;
    }
}
