package ie.gti.asdl.rey.gtirecord.core.service.impl;

import ie.gti.asdl.rey.gtirecord.core.dao.*;
import ie.gti.asdl.rey.gtirecord.core.service.UserService;
import ie.gti.asdl.rey.gtirecord.model.entity.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for StudentServiceImpl.
 * All collaborators are mocked with Mockito; no Spring context is loaded.
 */
@ExtendWith(MockitoExtension.class)
class StudentServiceImplTest {

    @Mock
    private StudentAssignmentDao studentAssignmentDao;
    @Mock
    private AssignmentDao assignmentDao;
    @Mock
    private StudentDao studentDao;
    @Mock
    private TeacherDao teacherDao;
    @Mock
    private PersonDao personDao;
    @Mock
    private UserDao userDao;
    @Mock
    private ModuleDao moduleDao;
    @Mock
    private UserRolesDao userRolesDao;
    @Mock
    private UserService userService;

    @InjectMocks
    private StudentServiceImpl studentService;

    private Person newPerson(Integer id) {
        return new Person(id, "John", "Doe", null, null, null, null, null, null);
    }

    private Group newGroup(Integer id) {
        return new Group(id, "SD1", "SD1", null);
    }

    private Student newStudent(Person person, Group group) {
        return new Student(person, group, null, null, null);
    }

    // Get all delegates to DAO.
    @Test
    void getAll_delegatesToDao() {
        List<Student> students = List.of(newStudent(newPerson(1), newGroup(1)));
        when(studentDao.getAll()).thenReturn(students);

        assertEquals(students, studentService.getAll());
    }

    // Get by person ID delegates to DAO.
    @Test
    void getByPersonId_delegatesToDao() {
        Student student = newStudent(newPerson(1), newGroup(1));
        when(studentDao.getByPersonId(1)).thenReturn(Optional.of(student));

        assertEquals(Optional.of(student), studentService.getByPersonId(1));
    }

    // Get by group ID delegates to DAO.
    @Test
    void getByGroupId_delegatesToDao() {
        List<Student> students = List.of(newStudent(newPerson(1), newGroup(1)));
        when(studentDao.getByGroupId(1)).thenReturn(students);

        assertEquals(students, studentService.getByGroupId(1));
    }

    // Insert uses existing person and creates assignments for students group.
    @Test
    void insert_usesExistingPerson_andCreatesAssignmentsForStudentsGroup() {
        Person person = newPerson(1);
        Group group = newGroup(5);
        Student student = newStudent(person, group);
        Assignment assignment = new Assignment(1, null, "CA1", 20, 100, null, null);
        when(personDao.getById(1)).thenReturn(Optional.of(person));
        when(studentDao.insert(student)).thenReturn(Optional.of(1));
        when(assignmentDao.getByGroupId(5)).thenReturn(List.of(assignment));

        Optional<Integer> result = studentService.insert(student);

        assertEquals(Optional.of(1), result);
        verify(personDao, never()).insert(any());
        verify(studentAssignmentDao).insert(any(StudentAssignment.class));
    }

    // Insert creates person when not existing yet.
    @Test
    void insert_createsPerson_whenNotExistingYet() {
        Person person = newPerson(1);
        Group group = newGroup(null);
        Student student = newStudent(person, group);
        when(personDao.getById(1)).thenReturn(Optional.empty());
        when(personDao.insert(person)).thenReturn(Optional.of(1));
        when(studentDao.insert(student)).thenReturn(Optional.of(1));

        Optional<Integer> result = studentService.insert(student);

        assertEquals(Optional.of(1), result);
        verify(personDao).insert(person);
        verifyNoInteractions(assignmentDao);
    }

    // Insert does not create assignments when group ID is null.
    @Test
    void insert_doesNotCreateAssignments_whenGroupIdIsNull() {
        Person person = newPerson(1);
        Group group = newGroup(null);
        Student student = newStudent(person, group);
        when(personDao.getById(1)).thenReturn(Optional.of(person));
        when(studentDao.insert(student)).thenReturn(Optional.of(1));

        studentService.insert(student);

        verifyNoInteractions(assignmentDao, studentAssignmentDao);
    }

    // Insert with user inserts student then links user.
    @Test
    void insertWithUser_insertsStudent_thenLinksUser() {
        Person person = newPerson(1);
        Group group = newGroup(null);
        Student student = newStudent(person, group);
        User user = new User(null, "jdoe", "pwd", null);
        when(personDao.getById(1)).thenReturn(Optional.of(person));
        when(studentDao.insert(student)).thenReturn(Optional.of(1));

        studentService.insertWithUser(student, user);

        assertEquals(1, user.getPersonId());
        verify(userService).insert(user);
    }

    // Update updates person and delegates to update student and assignments.
    @Test
    void update_updatesPerson_andDelegatesToUpdateStudentAndAssignments() {
        Person person = newPerson(1);
        Group group = newGroup(5);
        Student student = newStudent(person, group);
        when(studentDao.getByPersonId(1)).thenReturn(Optional.of(student));

        studentService.update(student);

        verify(personDao).update(person);
        verify(studentDao).update(student);
    }

    // Update student and assignments recreates assignments when group changed.
    @Test
    void updateStudentAndAssignments_recreatesAssignments_whenGroupChanged() {
        Person person = newPerson(1);
        Group oldGroup = newGroup(5);
        Group newGroupValue = newGroup(6);
        Student studentInDb = newStudent(person, oldGroup);
        Student updatedStudent = newStudent(person, newGroupValue);
        Assignment assignment = new Assignment(1, null, "CA1", 20, 100, null, null);
        when(studentDao.getByPersonId(1)).thenReturn(Optional.of(studentInDb));
        when(assignmentDao.getByGroupId(6)).thenReturn(List.of(assignment));

        studentService.updateStudentAndAssignments(updatedStudent);

        verify(studentDao).update(updatedStudent);
        verify(studentAssignmentDao).deleteByStudentPersonId(1);
        verify(studentAssignmentDao).insert(any(StudentAssignment.class));
    }

    // Update student and assignments does not touch assignments when group unchanged.
    @Test
    void updateStudentAndAssignments_doesNotTouchAssignments_whenGroupUnchanged() {
        Person person = newPerson(1);
        Group group = newGroup(5);
        Student studentInDb = newStudent(person, group);
        Student updatedStudent = newStudent(person, group);
        when(studentDao.getByPersonId(1)).thenReturn(Optional.of(studentInDb));

        studentService.updateStudentAndAssignments(updatedStudent);

        verify(studentDao).update(updatedStudent);
        verifyNoInteractions(studentAssignmentDao, assignmentDao);
    }

    // Update student and assignments does nothing when student not found.
    @Test
    void updateStudentAndAssignments_doesNothing_whenStudentNotFound() {
        Person person = newPerson(1);
        Group group = newGroup(5);
        Student updatedStudent = newStudent(person, group);
        when(studentDao.getByPersonId(1)).thenReturn(Optional.empty());

        studentService.updateStudentAndAssignments(updatedStudent);

        verify(studentDao, never()).update(any());
    }

    // Update with user updates student and saves user roles.
    @Test
    void updateWithUser_updatesStudent_andSavesUserRoles() {
        Person person = newPerson(1);
        Group group = newGroup(5);
        Student student = newStudent(person, group);
        User user = new User(1, "jdoe", "pwd", 1);
        when(studentDao.getByPersonId(1)).thenReturn(Optional.of(student));

        studentService.updateWithUser(student, user);

        verify(personDao).update(person);
        verify(userService).saveUserWithRoles(user);
    }

    // Save does nothing when student is null.
    @Test
    void save_doesNothing_whenStudentIsNull() {
        studentService.save(null);

        verifyNoInteractions(studentDao, personDao);
    }

    // Save inserts student when person ID is null.
    @Test
    void save_insertsStudent_whenPersonIdIsNull() {
        Person person = newPerson(null);
        Group group = newGroup(null);
        Student student = newStudent(person, group);
        when(personDao.getById(null)).thenReturn(Optional.empty());
        when(personDao.insert(person)).thenReturn(Optional.of(1));
        when(studentDao.insert(student)).thenReturn(Optional.of(1));

        studentService.save(student);

        verify(studentDao).insert(student);
    }

    // Save updates student when person ID is present.
    @Test
    void save_updatesStudent_whenPersonIdIsPresent() {
        Person person = newPerson(1);
        Group group = newGroup(5);
        Student student = newStudent(person, group);
        when(studentDao.getByPersonId(1)).thenReturn(Optional.of(student));

        studentService.save(student);

        verify(personDao).update(person);
        verify(studentDao, never()).insert(any());
    }

    // Delete returns early when person ID is null.
    @Test
    void delete_returnsEarly_whenPersonIdIsNull() {
        Student student = newStudent(newPerson(null), newGroup(5));

        studentService.delete(student);

        verifyNoInteractions(studentDao, personDao, userDao, userRolesDao, userService);
    }

    // Delete removes assignments and person and user when no associated teacher.
    @Test
    void delete_removesAssignmentsAndPersonAndUser_whenNoAssociatedTeacher() {
        Person person = newPerson(1);
        Group group = newGroup(5);
        Student student = newStudent(person, group);
        Assignment assignment = new Assignment(1, null, "CA1", 20, 100, null, null);
        User user = new User(9, "jdoe", "pwd", 1);
        when(assignmentDao.getByGroupId(5)).thenReturn(List.of(assignment));
        when(teacherDao.getByPersonId(1)).thenReturn(Optional.empty());
        when(userDao.getByPersonId(1)).thenReturn(Optional.of(user));

        studentService.delete(student);

        verify(studentAssignmentDao).deleteByAssignmentId(1);
        verify(studentDao).delete(1);
        verify(personDao).delete(1);
        verify(userService).delete(9);
    }

    // Delete only removes student role when associated teacher exists.
    @Test
    void delete_onlyRemovesStudentRole_whenAssociatedTeacherExists() {
        Person person = newPerson(1);
        Group group = newGroup(5);
        Student student = newStudent(person, group);
        Teacher teacher = mock(Teacher.class);
        User user = new User(9, "jdoe", "pwd", 1);
        when(assignmentDao.getByGroupId(5)).thenReturn(List.of());
        when(teacherDao.getByPersonId(1)).thenReturn(Optional.of(teacher));
        when(userDao.getByPersonId(1)).thenReturn(Optional.of(user));

        studentService.delete(student);

        verify(personDao, never()).delete(anyInt());
        verify(userRolesDao).delete(9, Role.RoleType.STUDENT.asRole().getId());
    }
}
