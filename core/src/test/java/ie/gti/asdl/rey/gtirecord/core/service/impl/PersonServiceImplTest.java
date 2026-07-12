package ie.gti.asdl.rey.gtirecord.core.service.impl;

import ie.gti.asdl.rey.gtirecord.core.dao.AddressDao;
import ie.gti.asdl.rey.gtirecord.core.dao.PersonDao;
import ie.gti.asdl.rey.gtirecord.core.dao.UserDao;
import ie.gti.asdl.rey.gtirecord.core.service.StudentService;
import ie.gti.asdl.rey.gtirecord.core.service.TeacherService;
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
 * Unit tests for PersonServiceImpl.
 * All collaborators are mocked with Mockito; no Spring context is loaded.
 */
@ExtendWith(MockitoExtension.class)
class PersonServiceImplTest {

    @Mock
    private PersonDao personDao;
    @Mock
    private AddressDao addressDao;
    @Mock
    private StudentService studentService;
    @Mock
    private TeacherService teacherService;
    @Mock
    private UserDao userDao;

    @InjectMocks
    private PersonServiceImpl personService;

    private Person newPerson(Integer id) {
        return new Person(id, "John", "Doe", null, null, null, null, null, null);
    }

    private Person newPersonWithAddress(Integer id, Address address) {
        Person person = newPerson(id);
        person.setAddress(address);
        return person;
    }

    // Get all delegates to DAO.
    @Test
    void getAll_delegatesToDao() {
        List<Person> people = List.of(newPerson(1));
        when(personDao.getAll()).thenReturn(people);

        assertEquals(people, personService.getAll());
    }

    // Get by ID attaches address when person and address exist.
    @Test
    void getById_attachesAddress_whenPersonAndAddressExist() {
        Person person = newPerson(1);
        Address address = new Address(1, "Line 1", null, "Gort", "Galway", "Ireland", "H91X1X1");
        when(personDao.getById(1)).thenReturn(Optional.of(person));
        when(addressDao.getByPersonId(1)).thenReturn(Optional.of(address));

        Optional<Person> result = personService.getById(1);

        assertTrue(result.isPresent());
        assertEquals(address, result.get().getAddress());
    }

    // Get by ID returns empty without querying address when person not found.
    @Test
    void getById_returnsEmpty_withoutQueryingAddress_whenPersonNotFound() {
        when(personDao.getById(1)).thenReturn(Optional.empty());

        Optional<Person> result = personService.getById(1);

        assertTrue(result.isEmpty());
        verifyNoInteractions(addressDao);
    }

    // Insert saves address when person has address.
    @Test
    void insert_savesAddress_whenPersonHasAddress() {
        Address address = new Address(null, "Line 1", null, "Gort", "Galway", "Ireland", "H91X1X1");
        Person person = newPersonWithAddress(null, address);
        when(personDao.insert(person)).thenReturn(Optional.of(10));

        Optional<Integer> result = personService.insert(person);

        assertEquals(Optional.of(10), result);
        assertEquals(10, person.getId());
        assertEquals(10, address.getPersonId());
        verify(addressDao).insert(address);
    }

    // Insert does not touch address when person has no address.
    @Test
    void insert_doesNotTouchAddress_whenPersonHasNoAddress() {
        Person person = newPerson(null);
        when(personDao.insert(person)).thenReturn(Optional.of(10));

        personService.insert(person);

        verifyNoInteractions(addressDao);
    }

    // Insert does nothing further when DAO returns empty.
    @Test
    void insert_doesNothingFurther_whenDaoReturnsEmpty() {
        Person person = newPerson(null);
        when(personDao.insert(person)).thenReturn(Optional.empty());

        Optional<Integer> result = personService.insert(person);

        assertTrue(result.isEmpty());
        assertNull(person.getId());
        verifyNoInteractions(addressDao);
    }

    // Update updates existing address when address has person ID.
    @Test
    void update_updatesExistingAddress_whenAddressHasPersonId() {
        Address address = new Address(1, "Line 1", null, "Gort", "Galway", "Ireland", "H91X1X1");
        Person person = newPersonWithAddress(1, address);

        personService.update(person);

        verify(personDao).update(person);
        verify(addressDao).update(address);
        verify(addressDao, never()).insert(any());
    }

    // Update inserts new address when address has no person ID yet.
    @Test
    void update_insertsNewAddress_whenAddressHasNoPersonIdYet() {
        Address address = new Address(null, "Line 1", null, "Gort", "Galway", "Ireland", "H91X1X1");
        Person person = newPersonWithAddress(1, address);

        personService.update(person);

        verify(personDao).update(person);
        assertEquals(1, address.getPersonId());
        verify(addressDao).insert(address);
        verify(addressDao, never()).update(any());
    }

    // Update does not touch address when person has no address.
    @Test
    void update_doesNotTouchAddress_whenPersonHasNoAddress() {
        Person person = newPerson(1);

        personService.update(person);

        verify(personDao).update(person);
        verifyNoInteractions(addressDao);
    }

    // Save inserts person when ID is null.
    @Test
    void save_insertsPerson_whenIdIsNull() {
        Person person = newPerson(null);
        when(personDao.insert(person)).thenReturn(Optional.of(5));

        Optional<Integer> result = personService.save(person);

        assertEquals(Optional.of(5), result);
        verify(personDao).insert(person);
        verify(personDao, never()).update(any());
    }

    // Save updates person when ID is present.
    @Test
    void save_updatesPerson_whenIdIsPresent() {
        Person person = newPerson(5);

        Optional<Integer> result = personService.save(person);

        assertEquals(Optional.of(5), result);
        verify(personDao).update(person);
        verify(personDao, never()).insert(any());
    }

    // Save with user creates student record when user has student role not yet present.
    @Test
    void saveWithUser_createsStudentRecord_whenUserHasStudentRoleNotYetPresent() {
        Person person = newPerson(5);
        User user = new User(1, "jdoe", "pwd", 5);
        user.getRoles().add(Role.RoleType.STUDENT.asRole());
        when(studentService.getByPersonId(5)).thenReturn(Optional.empty());

        Optional<Integer> result = personService.saveWithUser(person, user);

        assertEquals(Optional.of(5), result);
        verify(studentService).insert(any(Student.class));
        verify(teacherService, never()).insert(any());
    }

    // Save with user does not duplicate student record when already exists.
    @Test
    void saveWithUser_doesNotDuplicateStudentRecord_whenAlreadyExists() {
        Person person = newPerson(5);
        User user = new User(1, "jdoe", "pwd", 5);
        user.getRoles().add(Role.RoleType.STUDENT.asRole());
        Student existingStudent = new Student(person, null, null, null, null);
        when(studentService.getByPersonId(5)).thenReturn(Optional.of(existingStudent));

        personService.saveWithUser(person, user);

        verify(studentService, never()).insert(any());
    }

    // Save with user links user to new person when user has no person yet.
    @Test
    void saveWithUser_linksUserToNewPerson_whenUserHasNoPersonYet() {
        Person person = newPerson(null);
        User user = new User(1, "jdoe", "pwd", null);
        when(personDao.insert(person)).thenReturn(Optional.of(9));

        personService.saveWithUser(person, user);

        assertEquals(9, user.getPersonId());
        verify(userDao).update(user);
    }

    // Delete removes person and address.
    @Test
    void delete_removesPerson_andAddress() {
        Person person = newPerson(5);

        personService.delete(person);

        verify(personDao).delete(5);
        verify(addressDao).deleteByPersonId(5);
    }
}
