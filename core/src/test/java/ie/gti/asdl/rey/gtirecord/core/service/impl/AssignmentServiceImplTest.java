package ie.gti.asdl.rey.gtirecord.core.service.impl;

import ie.gti.asdl.rey.gtirecord.core.dao.AssignmentDao;
import ie.gti.asdl.rey.gtirecord.core.dao.StudentAssignmentDao;
import ie.gti.asdl.rey.gtirecord.core.dao.StudentDao;
import ie.gti.asdl.rey.gtirecord.model.entity.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AssignmentServiceImpl.
 * All collaborators are mocked with Mockito; no Spring context is loaded.
 */
@ExtendWith(MockitoExtension.class)
class AssignmentServiceImplTest {

    @Mock
    private AssignmentDao assignmentDao;
    @Mock
    private StudentAssignmentDao studentAssignmentDao;
    @Mock
    private StudentDao studentDao;

    @InjectMocks
    private AssignmentServiceImpl assignmentService;

    private Student newStudent(int personId) {
        Person person = new Person(personId, "John", "Doe", null, null, null, null, null, null);
        Group group = new Group(1, "SD1", "SD1", null);
        return new Student(person, group, null, null, null);
    }

    private Assignment newAssignment(Integer id, Integer groupId) {
        Group group = new Group(groupId, "SD1", "SD1", null);
        GroupModule groupModule = new GroupModule(1, group, null, null);
        return new Assignment(id, groupModule, "CA1", 20, 100, null, null);
    }

    // Get by ID delegates to DAO.
    @Test
    void getById_delegatesToDao() {
        Assignment assignment = newAssignment(1, 1);
        when(assignmentDao.getById(1)).thenReturn(Optional.of(assignment));

        assertEquals(Optional.of(assignment), assignmentService.getById(1));
    }

    // Get all delegates to DAO.
    @Test
    void getAll_delegatesToDao() {
        List<Assignment> assignments = List.of(newAssignment(1, 1));
        when(assignmentDao.getAll()).thenReturn(assignments);

        assertEquals(assignments, assignmentService.getAll());
    }

    // Get by group ID delegates to DAO.
    @Test
    void getByGroupId_delegatesToDao() {
        List<Assignment> assignments = List.of(newAssignment(1, 1));
        when(assignmentDao.getByGroupId(1)).thenReturn(assignments);

        assertEquals(assignments, assignmentService.getByGroupId(1));
    }

    // Insert creates student assignment for every student in group.
    @Test
    void insert_createsStudentAssignment_forEveryStudentInGroup() {
        Assignment assignment = newAssignment(null, 1);
        Student student1 = newStudent(100);
        Student student2 = newStudent(101);
        when(assignmentDao.insert(assignment)).thenReturn(Optional.of(5));
        when(studentDao.getByGroupId(1)).thenReturn(List.of(student1, student2));

        Optional<Integer> result = assignmentService.insert(assignment);

        assertEquals(Optional.of(5), result);
        ArgumentCaptor<StudentAssignment> captor = ArgumentCaptor.forClass(StudentAssignment.class);
        verify(studentAssignmentDao, times(2)).insert(captor.capture());
        List<StudentAssignment> inserted = captor.getAllValues();
        assertEquals(student1, inserted.get(0).getStudent());
        assertEquals(assignment, inserted.get(0).getAssignment());
        assertEquals(student2, inserted.get(1).getStudent());
    }

    // Insert does not create student assignments when DAO returns empty.
    @Test
    void insert_doesNotCreateStudentAssignments_whenDaoReturnsEmpty() {
        Assignment assignment = newAssignment(null, 1);
        when(assignmentDao.insert(assignment)).thenReturn(Optional.empty());

        Optional<Integer> result = assignmentService.insert(assignment);

        assertEquals(Optional.empty(), result);
        verifyNoInteractions(studentDao, studentAssignmentDao);
    }

    // Update delegates to DAO.
    @Test
    void update_delegatesToDao() {
        Assignment assignment = newAssignment(1, 1);

        assignmentService.update(assignment);

        verify(assignmentDao).update(assignment);
    }

    // Delete removes student assignments first then assignment.
    @Test
    void delete_removesStudentAssignmentsFirst_thenAssignment() {
        assignmentService.delete(1);

        var inOrder = inOrder(studentAssignmentDao, assignmentDao);
        inOrder.verify(studentAssignmentDao).deleteByAssignmentId(1);
        inOrder.verify(assignmentDao).delete(1);
    }

    // Delete by group ID removes student assignments for each assignment in group then all assignments.
    @Test
    void deleteByGroupId_removesStudentAssignments_forEachAssignmentInGroup_thenAllAssignments() {
        Assignment assignment1 = newAssignment(1, 1);
        Assignment assignment2 = newAssignment(2, 1);
        when(assignmentDao.getByGroupId(1)).thenReturn(List.of(assignment1, assignment2));

        assignmentService.deleteByGroupId(1);

        verify(studentAssignmentDao).deleteByAssignmentId(1);
        verify(studentAssignmentDao).deleteByAssignmentId(2);
        verify(assignmentDao).deleteByGroupId(1);
    }
}
