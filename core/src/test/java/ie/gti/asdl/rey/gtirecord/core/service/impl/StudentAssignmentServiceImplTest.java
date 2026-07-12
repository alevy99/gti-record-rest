package ie.gti.asdl.rey.gtirecord.core.service.impl;

import ie.gti.asdl.rey.gtirecord.core.dao.ModuleDao;
import ie.gti.asdl.rey.gtirecord.core.dao.StudentAssignmentDao;
import ie.gti.asdl.rey.gtirecord.model.entity.*;
import ie.gti.asdl.rey.gtirecord.model.entity.Module;
import ie.gti.asdl.rey.gtirecord.model.entity.add.StudentAssignmentStats;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Unit tests for StudentAssignmentServiceImpl.
 * All collaborators are mocked with Mockito; no Spring context is loaded.
 */
@ExtendWith(MockitoExtension.class)
class StudentAssignmentServiceImplTest {

    @Mock
    private StudentAssignmentDao studentAssignmentDao;
    @Mock
    private ModuleDao moduleDao;

    @InjectMocks
    private StudentAssignmentServiceImpl studentAssignmentService;

    private Module newModule(int id) {
        return new Module(id, "Java", "JV1");
    }

    private Assignment newAssignment(Integer weighting, Integer maxGrade) {
        return new Assignment(1, null, "CA1", weighting, maxGrade, null, null);
    }

    private Student newStudent(int personId, int groupId) {
        Person person = new Person(personId, "John", "Doe", null, null, null, null, null, null);
        Group group = new Group(groupId, "SD1", "SD1", null);
        return new Student(person, group, null, null, null);
    }

    // Get by student person ID delegates to DAO.
    @Test
    void getByStudentPersonId_delegatesToDao() {
        List<StudentAssignment> result = List.of(new StudentAssignment(null, newAssignment(20, 100), null, null, 80, null));
        when(studentAssignmentDao.getByStudentPersonId(100)).thenReturn(result);

        assertEquals(result, studentAssignmentService.getByStudentPersonId(100));
    }

    // Get by student person ID and module ID delegates to DAO.
    @Test
    void getByStudentPersonIdAndModuleId_delegatesToDao() {
        List<StudentAssignment> result = List.of(new StudentAssignment(null, newAssignment(20, 100), null, null, 80, null));
        when(studentAssignmentDao.getByStudentPersonIdAndModuleId(100, 10)).thenReturn(result);

        assertEquals(result, studentAssignmentService.getByStudentPersonIdAndModuleId(100, 10));
    }

    // Get by assignment ID delegates to DAO.
    @Test
    void getByAssignmentId_delegatesToDao() {
        List<StudentAssignment> result = List.of(new StudentAssignment(null, newAssignment(20, 100), null, null, 80, null));
        when(studentAssignmentDao.getByAssignmentId(1)).thenReturn(result);

        assertEquals(result, studentAssignmentService.getByAssignmentId(1));
    }

    // Update delegates to DAO.
    @Test
    void update_delegatesToDao() {
        StudentAssignment sa = new StudentAssignment(null, newAssignment(20, 100), null, null, 80, null);

        studentAssignmentService.update(sa);

        org.mockito.Mockito.verify(studentAssignmentDao).update(sa);
    }

    // Delete by assignment ID delegates to DAO.
    @Test
    void deleteByAssignmentId_delegatesToDao() {
        studentAssignmentService.deleteByAssignmentId(1);

        org.mockito.Mockito.verify(studentAssignmentDao).deleteByAssignmentId(1);
    }

    // Get student assignment stats total for student looks up modules of students group and aggregates stats.
    @Test
    void getStudentAssignmentStatsTotal_forStudent_looksUpModulesOfStudentsGroup_andAggregatesStats() {
        Student student = newStudent(100, 5);
        Module module = newModule(10);
        Assignment assignment = newAssignment(20, 100);
        StudentAssignment gradedAssignment = new StudentAssignment(student, assignment, true, true, 80, null);
        when(moduleDao.getByGroupId(5)).thenReturn(List.of(module));
        when(studentAssignmentDao.getByStudentPersonIdAndModuleId(100, 10)).thenReturn(List.of(gradedAssignment));

        StudentAssignmentStats stats = studentAssignmentService.getStudentAssignmentStatsTotal(student);

        assertEquals(80, stats.getGradeTotal());
        assertEquals(100, stats.getMaxGradeTotal());
        assertEquals(16.0, stats.getWeightingTotal());
        assertEquals(20, stats.getMaxWeightingTotal());
    }

    // Get student assignment stats total by ID and modules treats ungraded as zero.
    @Test
    void getStudentAssignmentStatsTotal_byIdAndModules_treatsUngraded_asZero() {
        Module module = newModule(10);
        Assignment assignment = newAssignment(20, 100);
        StudentAssignment ungraded = new StudentAssignment(null, assignment, false, false, null, null);
        when(studentAssignmentDao.getByStudentPersonIdAndModuleId(100, 10)).thenReturn(List.of(ungraded));

        StudentAssignmentStats stats = studentAssignmentService.getStudentAssignmentStatsTotal(100, List.of(module));

        assertEquals(0, stats.getGradeTotal());
        assertEquals(100, stats.getMaxGradeTotal());
        assertEquals(0.0, stats.getWeightingTotal());
        assertEquals(20, stats.getMaxWeightingTotal());
    }

    // Get student assignment stats by ID and modules returns stats per module.
    @Test
    void getStudentAssignmentStats_byIdAndModules_returnsStatsPerModule() {
        Module module = newModule(10);
        Assignment assignment = newAssignment(20, 100);
        StudentAssignment gradedAssignment = new StudentAssignment(null, assignment, true, true, 50, null);
        when(studentAssignmentDao.getByStudentPersonIdAndModuleId(100, 10)).thenReturn(List.of(gradedAssignment));

        Map<Module, StudentAssignmentStats> statsMap = studentAssignmentService.getStudentAssignmentStats(100, List.of(module));

        assertEquals(1, statsMap.size());
        assertEquals(50, statsMap.get(module).getGradeTotal());
    }

    // Get student assignment stats for list of students returns stats per student.
    @Test
    void getStudentAssignmentStats_forListOfStudents_returnsStatsPerStudent() {
        Student student1 = newStudent(100, 5);
        Student student2 = newStudent(101, 5);
        Module module = newModule(10);
        when(studentAssignmentDao.getByStudentPersonIdAndModuleId(100, 10)).thenReturn(List.of());
        when(studentAssignmentDao.getByStudentPersonIdAndModuleId(101, 10)).thenReturn(List.of());

        Map<Student, Map<Module, StudentAssignmentStats>> result =
                studentAssignmentService.getStudentAssignmentStats(List.of(student1, student2), List.of(module));

        assertEquals(2, result.size());
        assertEquals(0, result.get(student1).get(module).getGradeTotal());
        assertEquals(0, result.get(student2).get(module).getGradeTotal());
    }
}
