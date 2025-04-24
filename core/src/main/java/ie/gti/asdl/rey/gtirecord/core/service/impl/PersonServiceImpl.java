package ie.gti.asdl.rey.gtirecord.core.service.impl;

import ie.gti.asdl.rey.gtirecord.core.dao.*;
import ie.gti.asdl.rey.gtirecord.core.service.PersonService;
import ie.gti.asdl.rey.gtirecord.core.service.StudentService;
import ie.gti.asdl.rey.gtirecord.core.service.TeacherService;
import ie.gti.asdl.rey.gtirecord.core.service.UserService;
import ie.gti.asdl.rey.gtirecord.model.annotation.InstanceFactory;
import ie.gti.asdl.rey.gtirecord.model.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ie.gti.asdl.rey.gtirecord.model.entity.Role.getRoleTypeByRole;

@Service
public class PersonServiceImpl implements PersonService {

    private final Logger logger = LoggerFactory.getLogger(PersonServiceImpl.class);

    private final PersonDao personDao;
    private final AddressDao addressDao;
    private final StudentService studentService;
    private final TeacherService teacherService;
    private final UserDao userDao;

    @Autowired
    PersonServiceImpl(PersonDao personDao, AddressDao addressDao, StudentService studentService,
                      TeacherService teacherService, UserDao userDao) {
        this.personDao = personDao;
        this.addressDao = addressDao;
        this.studentService = studentService;
        this.teacherService = teacherService;
        this.userDao = userDao;
    }

    @Override
    public List<Person> getAll() {
        return personDao.getAll();
    }

    @Override
    public Optional<Person> getById(Integer id) {
        Optional<Person> person = personDao.getById(id);
        person.ifPresent( p -> {
            Optional<Address> addressOpt = addressDao.getByPersonId(id);
            addressOpt.ifPresent(p::setAddress);
        });
        return person;
    }

    @Transactional
    @Override
    public Optional<Integer> insert(Person person) {
        Optional<Integer> personIdOpt = personDao.insert(person);
        personIdOpt.ifPresent(personId -> {
            person.setId(personId);
            if (person.getAddress() != null) {
                person.getAddress().setPersonId(personId);
                addressDao.insert(person.getAddress());
            }
        });
        return personIdOpt;
    }

    @Transactional
    @Override
    public void update(Person person) {
        personDao.update(person);
        if (person.getAddress() != null) {
            if (person.getAddress().getPersonId() != null) {
                addressDao.update(person.getAddress());
            } else {
                person.getAddress().setPersonId(person.getId());
                addressDao.insert(person.getAddress());
            }
        }
    }

    @Transactional
    @Override
    public Optional<Integer> save(Person person) {
        Optional<Integer> personIdOpt;
        if (person.getId() == null) {
            personIdOpt = insert(person);
        } else {
            update(person);
            personIdOpt = Optional.of(person.getId());
        }
        return personIdOpt;
    }

    @Transactional
    @Override
    public Optional<Integer> saveWithUser(Person person, User user) {
        var personIdOpt = save(person);
        personIdOpt.ifPresent(personId -> {
            // Set Person for a user if he was not set previously
            if (user.getPersonId() == null) {
                user.setPersonId(personId);
                userDao.update(user);
            }

            user.getRoles().forEach(role -> {
                switch (getRoleTypeByRole(role)) {
                    case Role.RoleType.STUDENT -> {
                        if (studentService.getByPersonId(personId).isEmpty()) {
                            Student student = InstanceFactory.create(Student.class);
                            student.setPerson(person);
                            studentService.insert(student);
                        }
                    }
                    case Role.RoleType.TEACHER -> {
                        if (teacherService.getByPersonId(personId).isEmpty()) {
                            Teacher teacher = InstanceFactory.create(Teacher.class);
                            teacher.setPerson(person);
                            teacherService.insert(teacher);
                        }
                    }
                }
            });
        });
        return personIdOpt;
    }

    @Transactional
    @Override
    public void delete(Person person) {
        personDao.delete(person.getId());
        addressDao.deleteByPersonId(person.getId());
    }
}
