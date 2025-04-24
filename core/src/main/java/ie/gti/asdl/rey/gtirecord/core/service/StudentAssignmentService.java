package ie.gti.asdl.rey.gtirecord.core.service;

import ie.gti.asdl.rey.gtirecord.model.entity.Module;
import ie.gti.asdl.rey.gtirecord.model.entity.Student;
import ie.gti.asdl.rey.gtirecord.model.entity.StudentAssignment;
import ie.gti.asdl.rey.gtirecord.model.entity.add.StudentAssignmentStats;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Andrei Levchenko
 */
@Service
public interface StudentAssignmentService {

    List<StudentAssignment> getByStudentPersonId(Integer studentPersonId);

    List<StudentAssignment> getByStudentPersonIdAndModuleId(Integer studentPersonId, Integer moduleId);

    List<StudentAssignment> getByAssignmentId(Integer assignmentId);

    void update(StudentAssignment studentAssignment);

    void deleteByAssignmentId(Integer assignmentId);

    StudentAssignmentStats getStudentAssignmentStats(Student student);

    StudentAssignmentStats getStudentAssignmentStats(Integer studentId, List<Module> modules);
}
