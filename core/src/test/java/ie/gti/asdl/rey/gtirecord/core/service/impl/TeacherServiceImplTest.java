package ie.gti.asdl.rey.gtirecord.core.service.impl;

import ie.gti.asdl.rey.gtirecord.core.dao.*;
import ie.gti.asdl.rey.gtirecord.core.service.UserService;
import ie.gti.asdl.rey.gtirecord.model.entity.Person;
import ie.gti.asdl.rey.gtirecord.model.entity.Role;
import ie.gti.asdl.rey.gtirecord.model.entity.Student;
import ie.gti.asdl.rey.gtirecord.model.entity.Teacher;
import ie.gti.asdl.rey.gtirecord.model.entity.User;
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
 * Unit tests for TeacherServiceImpl.
 * All collaborators are mocked with Mockito; no Spring context is loaded.
 */
@ExtendWith(MockitoExtension.class)
class TeacherServiceImplTest {

    @Mock
    private StudentDao studentDao;
    @Mock
    private TeacherDao teacherDao;
    @Mock
    private PersonDao personDao;
    @Mock
    private UserDao userDao;
    @Mock
    private UserRolesDao userRolesDao;
    @Mock
    private UserService userService;

    @InjectMocks
    private TeacherServiceImpl teacherService;

    private Person newPerson(Integer id) {
        return new Person(id, "Jane", "Roe", null, null, null, null, null, null);
    }

    private Teacher newTeacher(Person person) {
        return new Teacher(person, "Lecturer", "PhD", 10);
    }

    // Get all delegates to DAO.
    @Test
    void getAll_delegatesToDao() {
        List<Teacher> teachers = List.of(newTeacher(newPerson(1)));
        when(teacherDao.getAll()).thenReturn(teachers);

        assertEquals(teachers, teacherService.getAll());
    }

    // Get by module ID delegates to DAO.
    @Test
    void getByModuleId_delegatesToDao() {
        List<Teacher> teachers = List.of(newTeacher(newPerson(1)));
        when(teacherDao.getByModuleId(5)).thenReturn(teachers);

        assertEquals(teachers, teacherService.getByModuleId(5));
    }

    // Get by person ID delegates to DAO.
    @Test
    void getByPersonId_delegatesToDao() {
        Teacher teacher = newTeacher(newPerson(1));
        when(teacherDao.getByPersonId(1)).thenReturn(Optional.of(teacher));

        assertEquals(Optional.of(teacher), teacherService.getByPersonId(1));
    }

    // Insert uses existing person when person already exists.
    @Test
    void insert_usesExistingPerson_whenPersonAlreadyExists() {
        Person person = newPerson(1);
        Teacher teacher = newTeacher(person);
        when(personDao.getById(1)).thenReturn(Optional.of(person));
        when(teacherDao.insert(teacher)).thenReturn(Optional.of(1));

        Optional<Integer> result = teacherService.insert(teacher);

        assertEquals(Optional.of(1), result);
        verify(personDao, never()).insert(any());
    }

    // Insert creates person first when person does not exist yet.
    @Test
    void insert_createsPersonFirst_whenPersonDoesNotExistYet() {
        Person person = newPerson(1);
        Teacher teacher = newTeacher(person);
        when(personDao.getById(1)).thenReturn(Optional.empty());
        when(personDao.insert(person)).thenReturn(Optional.of(1));
        when(teacherDao.insert(teacher)).thenReturn(Optional.of(1));

        Optional<Integer> result = teacherService.insert(teacher);

        assertEquals(Optional.of(1), result);
        verify(personDao).insert(person);
    }

    // Insert does not insert teacher when new person could not be created.
    @Test
    void insert_doesNotInsertTeacher_whenNewPersonCouldNotBeCreated() {
        Person person = newPerson(1);
        Teacher teacher = newTeacher(person);
        when(personDao.getById(1)).thenReturn(Optional.empty());
        when(personDao.insert(person)).thenReturn(Optional.empty());

        Optional<Integer> result = teacherService.insert(teacher);

        assertTrue(result.isEmpty());
        verify(teacherDao, never()).insert(any());
    }

    // Insert with user inserts teacher then links user.
    @Test
    void insertWithUser_insertsTeacher_thenLinksUser() {
        Person person = newPerson(1);
        Teacher teacher = newTeacher(person);
        User user = new User(null, "jroe", "pwd", null);
        when(personDao.getById(1)).thenReturn(Optional.of(person));
        when(teacherDao.insert(teacher)).thenReturn(Optional.of(1));

        teacherService.insertWithUser(teacher, user);

        assertEquals(1, user.getPersonId());
        verify(userService).insert(user);
    }

    // Insert with user does not touch user when teacher insert failed.
    @Test
    void insertWithUser_doesNotTouchUser_whenTeacherInsertFailed() {
        Person person = newPerson(1);
        Teacher teacher = newTeacher(person);
        User user = new User(null, "jroe", "pwd", null);
        when(personDao.getById(1)).thenReturn(Optional.empty());
        when(personDao.insert(person)).thenReturn(Optional.empty());

        teacherService.insertWithUser(teacher, user);

        assertNull(user.getPersonId());
        verifyNoInteractions(userService);
    }

    // Update updates teacher and person.
    @Test
    void update_updatesTeacherAndPerson() {
        Person person = newPerson(1);
        Teacher teacher = newTeacher(person);

        teacherService.update(teacher);

        verify(teacherDao).update(teacher);
        verify(personDao).update(person);
    }

    // Update with user updates teacher and saves user roles.
    @Test
    void updateWithUser_updatesTeacher_andSavesUserRoles() {
        Person person = newPerson(1);
        Teacher teacher = newTeacher(person);
        User user = new User(1, "jroe", "pwd", 1);

        teacherService.updateWithUser(teacher, user);

        verify(teacherDao).update(teacher);
        verify(userService).saveUserWithRoles(user);
    }

    // Save does nothing when teacher is null.
    @Test
    void save_doesNothing_whenTeacherIsNull() {
        teacherService.save(null);

        verifyNoInteractions(teacherDao, personDao);
    }

    // Save does nothing when teacher has no person.
    @Test
    void save_doesNothing_whenTeacherHasNoPerson() {
        teacherService.save(new Teacher(null, "Lecturer", "PhD", 10));

        verifyNoInteractions(teacherDao, personDao);
    }

    // Save inserts teacher when person ID is null.
    @Test
    void save_insertsTeacher_whenPersonIdIsNull() {
        Person person = newPerson(null);
        Teacher teacher = newTeacher(person);
        when(personDao.getById(null)).thenReturn(Optional.empty());
        when(personDao.insert(person)).thenReturn(Optional.of(1));
        when(teacherDao.insert(teacher)).thenReturn(Optional.of(1));

        teacherService.save(teacher);

        verify(teacherDao).insert(teacher);
        verify(teacherDao, never()).update(any());
    }

    // Save updates teacher when person ID is present.
    @Test
    void save_updatesTeacher_whenPersonIdIsPresent() {
        Person person = newPerson(1);
        Teacher teacher = newTeacher(person);

        teacherService.save(teacher);

        verify(teacherDao).update(teacher);
        verify(teacherDao, never()).insert(any());
    }

    // Save with user returns empty when teacher is null.
    @Test
    void saveWithUser_returnsEmpty_whenTeacherIsNull() {
        Optional<Integer> result = teacherService.saveWithUser(null, new User(1, "jroe", "pwd", null));

        assertTrue(result.isEmpty());
    }

    // Save with user inserts teacher when person ID is null.
    @Test
    void saveWithUser_insertsTeacher_whenPersonIdIsNull() {
        Person person = newPerson(null);
        Teacher teacher = newTeacher(person);
        User user = new User(null, "jroe", "pwd", null);
        when(personDao.getById(null)).thenReturn(Optional.empty());
        when(personDao.insert(person)).thenAnswer(invocation -> {
            Person p = invocation.getArgument(0);
            p.setId(1); // Same as in the actual DAO
            return Optional.of(1);
        });
        when(teacherDao.insert(teacher)).thenReturn(Optional.of(1));

        Optional<Integer> result = teacherService.saveWithUser(teacher, user);

        assertEquals(Optional.of(1), result);
        verify(userService).insert(user);
    }

    // Delete returns early when person ID is null.
    @Test
    void delete_returnsEarly_whenPersonIdIsNull() {
        teacherService.delete(null);

        verifyNoInteractions(teacherDao, personDao, userDao, userRolesDao, userService);
    }

    // Delete deletes person and user when no associated student.
    @Test
    void delete_deletesPersonAndUser_whenNoAssociatedStudent() {
        User user = new User(9, "jroe", "pwd", 1);
        when(studentDao.getByPersonId(1)).thenReturn(Optional.empty());
        when(userDao.getByPersonId(1)).thenReturn(Optional.of(user));

        teacherService.delete(1);

        verify(teacherDao).delete(1);
        verify(personDao).delete(1);
        verify(userService).delete(9);
    }

    // Delete only removes teacher role when associated student exists.
    @Test
    void delete_onlyRemovesTeacherRole_whenAssociatedStudentExists() {
        Student student = mock(Student.class);
        User user = new User(9, "jroe", "pwd", 1);
        when(studentDao.getByPersonId(1)).thenReturn(Optional.of(student));
        when(userDao.getByPersonId(1)).thenReturn(Optional.of(user));

        teacherService.delete(1);

        verify(teacherDao).delete(1);
        verify(personDao, never()).delete(anyInt());
        verify(userRolesDao).delete(9, Role.RoleType.TEACHER.asRole().getId());
    }
}
