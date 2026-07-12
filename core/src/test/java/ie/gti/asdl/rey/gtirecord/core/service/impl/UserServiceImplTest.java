package ie.gti.asdl.rey.gtirecord.core.service.impl;

import ie.gti.asdl.rey.gtirecord.core.dao.PersonDao;
import ie.gti.asdl.rey.gtirecord.core.dao.StudentDao;
import ie.gti.asdl.rey.gtirecord.core.dao.TeacherDao;
import ie.gti.asdl.rey.gtirecord.core.dao.UserDao;
import ie.gti.asdl.rey.gtirecord.core.dao.UserRolesDao;
import ie.gti.asdl.rey.gtirecord.model.entity.Person;
import ie.gti.asdl.rey.gtirecord.model.entity.Role;
import ie.gti.asdl.rey.gtirecord.model.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserServiceImpl.
 * All collaborators are mocked with Mockito; no Spring context is loaded.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserDao userDao;
    @Mock
    private UserRolesDao userRolesDao;
    @Mock
    private PersonDao personDao;
    @Mock
    private StudentDao studentDao;
    @Mock
    private TeacherDao teacherDao;

    @InjectMocks
    private UserServiceImpl userService;

    // Insert saves user and inserts its roles.
    @Test
    void insert_savesUser_andInsertsItsRoles() {
        User user = new User(null, "jdoe", "pwd", null);
        user.getRoles().add(Role.RoleType.STUDENT.asRole());
        when(userDao.insert(user)).thenReturn(Optional.of(7));

        Optional<Integer> result = userService.insert(user);

        assertEquals(Optional.of(7), result);
        assertEquals(7, user.getId());
        verify(userRolesDao).insert(7, user.getRoles());
    }

    // Insert does not insert roles when DAO returns empty.
    @Test
    void insert_doesNotInsertRoles_whenDaoReturnsEmpty() {
        User user = new User(null, "jdoe", "pwd", null);
        when(userDao.insert(user)).thenReturn(Optional.empty());

        Optional<Integer> result = userService.insert(user);

        assertTrue(result.isEmpty());
        verifyNoInteractions(userRolesDao);
    }

    // Update user with roles throws when user not found.
    @Test
    void updateUserWithRoles_throws_whenUserNotFound() {
        User user = new User(1, "jdoe", "pwd", null);
        when(userDao.getById(1)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.updateUserWithRoles(user));
    }

    // Update user with roles inserts only new roles and deletes only removed roles.
    @Test
    void updateUserWithRoles_insertsOnlyNewRoles_andDeletesOnlyRemovedRoles() {
        User userDb = new User(1, "jdoe", "pwd", null);
        userDb.getRoles().add(Role.RoleType.STUDENT.asRole());
        userDb.getRoles().add(Role.RoleType.ADMIN.asRole());

        User user = new User(1, "jdoe", "pwd", null);
        user.getRoles().add(Role.RoleType.STUDENT.asRole());
        user.getRoles().add(Role.RoleType.TEACHER.asRole());

        when(userDao.getById(1)).thenReturn(Optional.of(userDb));

        userService.updateUserWithRoles(user);

        verify(userDao).update(user);
        verify(userRolesDao).insert(1, Set.of(Role.RoleType.TEACHER.asRole()));
        verify(userRolesDao).deleteByUserId(1, Set.of(Role.RoleType.ADMIN.asRole()));
    }

    // Update user with roles creates student record when new student role assigned and person has no student record yet.
    @Test
    void updateUserWithRoles_createsStudentRecord_whenNewStudentRoleAssigned_andPersonHasNoStudentRecordYet() {
        User userDb = new User(1, "jdoe", "pwd", 50);
        User user = new User(1, "jdoe", "pwd", 50);
        user.getRoles().add(Role.RoleType.STUDENT.asRole());
        Person person = new Person(50, "John", "Doe", null, null, null, null, null, null);

        when(userDao.getById(1)).thenReturn(Optional.of(userDb));
        when(personDao.getById(50)).thenReturn(Optional.of(person));
        when(studentDao.getByPersonId(50)).thenReturn(Optional.empty());

        userService.updateUserWithRoles(user);

        verify(studentDao).insert(any());
    }

    // Update user with roles does not duplicate student record when already present.
    @Test
    void updateUserWithRoles_doesNotDuplicateStudentRecord_whenAlreadyPresent() {
        User userDb = new User(1, "jdoe", "pwd", 50);
        User user = new User(1, "jdoe", "pwd", 50);
        user.getRoles().add(Role.RoleType.STUDENT.asRole());
        Person person = new Person(50, "John", "Doe", null, null, null, null, null, null);

        when(userDao.getById(1)).thenReturn(Optional.of(userDb));
        when(personDao.getById(50)).thenReturn(Optional.of(person));
        when(studentDao.getByPersonId(50)).thenReturn(Optional.of(mock(ie.gti.asdl.rey.gtirecord.model.entity.Student.class)));

        userService.updateUserWithRoles(user);

        verify(studentDao, never()).insert(any());
    }

    // Update delegates to DAO.
    @Test
    void update_delegatesToDao() {
        User user = new User(1, "jdoe", "pwd", null);

        userService.update(user);

        verify(userDao).update(user);
    }

    // Save user with roles inserts user when ID is null.
    @Test
    void saveUserWithRoles_insertsUser_whenIdIsNull() {
        User user = new User(null, "jdoe", "pwd", null);
        when(userDao.insert(user)).thenReturn(Optional.of(3));

        Optional<Integer> result = userService.saveUserWithRoles(user);

        assertEquals(Optional.of(3), result);
        verify(userDao).insert(user);
    }

    // Save user with roles updates user when ID is present.
    @Test
    void saveUserWithRoles_updatesUser_whenIdIsPresent() {
        User user = new User(1, "jdoe", "pwd", null);
        when(userDao.getById(1)).thenReturn(Optional.of(user));

        Optional<Integer> result = userService.saveUserWithRoles(user);

        assertEquals(Optional.of(1), result);
        verify(userDao).update(user);
        verify(userDao, never()).insert(any());
    }

    // Delete removes roles first then user.
    @Test
    void delete_removesRolesFirst_thenUser() {
        userService.delete(1);

        var inOrder = inOrder(userRolesDao, userDao);
        inOrder.verify(userRolesDao).deleteByUserId(1);
        inOrder.verify(userDao).delete(1);
    }

    // Get all delegates to DAO.
    @Test
    void getAll_delegatesToDao() {
        List<User> users = List.of(new User(1, "jdoe", "pwd", null));
        when(userDao.getAll()).thenReturn(users);

        assertEquals(users, userService.getAll());
    }

    // Get by username delegates to DAO.
    @Test
    void getByUsername_delegatesToDao() {
        User user = new User(1, "jdoe", "pwd", null);
        when(userDao.getByUsername("jdoe")).thenReturn(Optional.of(user));

        assertEquals(Optional.of(user), userService.getByUsername("jdoe"));
    }

    // Get by ID delegates to DAO.
    @Test
    void getById_delegatesToDao() {
        User user = new User(1, "jdoe", "pwd", null);
        when(userDao.getById(1)).thenReturn(Optional.of(user));

        assertEquals(Optional.of(user), userService.getById(1));
    }

    // Get by person ID delegates to DAO.
    @Test
    void getByPersonId_delegatesToDao() {
        User user = new User(1, "jdoe", "pwd", 50);
        when(userDao.getByPersonId(50)).thenReturn(Optional.of(user));

        assertEquals(Optional.of(user), userService.getByPersonId(50));
    }
}
