package ie.gti.asdl.rey.gtirecord.core.service.impl;

import ie.gti.asdl.rey.gtirecord.core.dao.*;
import ie.gti.asdl.rey.gtirecord.core.service.PersonService;
import ie.gti.asdl.rey.gtirecord.core.service.UserService;
import ie.gti.asdl.rey.gtirecord.model.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class UserServiceImpl implements UserService {

    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserDao userDao;

    private final UserRolesDao userRolesDao;

    private final PersonDao personDao;

    private final PersonService personService;
    private final StudentDao studentDao;
    private final TeacherDao teacherDao;

    @Autowired
    public UserServiceImpl(UserDao userDao, UserRolesDao userRolesDao, PersonDao personDao, PersonService personService, StudentDao studentDao, TeacherDao teacherDao) {
        super();
        this.userDao = userDao;
        this.userRolesDao = userRolesDao;
        this.personDao = personDao;
        this.personService = personService;
        this.studentDao = studentDao;
        this.teacherDao = teacherDao;
    }

    @Transactional
    @Override
    public Optional<Integer> insert(User user) {
        Optional<Integer> userIdOpt = userDao.insert(user);
        userIdOpt.ifPresent(userId -> {
            user.setId(userIdOpt.get());
            userRolesDao.insert(user.getId(), user.getRoles());
        });
        return userIdOpt;
    }

    @Transactional
    @Override
    public void updateUserWithRoles(User user) {
        userDao.getById(user.getId()).ifPresentOrElse(userDB -> {
            userDao.update(user);
            logRoles("New roles: {}", user.getRoles());

            List<Role> currentRoles = userDB.getRoles();
            logRoles("Current roles: {}", currentRoles);

            insertMissingRoles(user, currentRoles);

            // Delete roles we don't have anymore
            // We might want to delete associated records in student or teacher tables,
            // but in that case we would delete some data of the student or teacher
            // It is better to delete it when working directly with students or teachers,
            // rather than with a user
            List<Role> rolesToDelete = new ArrayList<>(currentRoles);
            rolesToDelete.removeAll(user.getRoles());
            logRoles("Roles to delete: {}", rolesToDelete);
            userRolesDao.deleteByUserId(user.getId(), rolesToDelete);
        }, () -> {
            throw new RuntimeException("User was not found: ID = " + user.getId());
        });
    }

    private void insertMissingRoles(User user, List<Role> currentRoles) {
        List<Role> rolesToInsert = new ArrayList<>(user.getRoles());
        rolesToInsert.removeAll(currentRoles);
        logRoles("Roles to insert: {}", rolesToInsert);
        userRolesDao.insert(user.getId(), rolesToInsert);

        // Now if we have a person already, associated with a user
        // And if it has Student or Teacher roles,
        // then we have to make sure there is a record in student or teacher tables
        if (user.getPersonId() != null) {
            personDao.getById(user.getPersonId()).ifPresent(person -> {
                rolesToInsert.stream()
                        .map(Role::getRoleTypeByRole)
                        .forEach(roleType -> {
                             switch (roleType) {
                                 case Role.RoleType.STUDENT -> {
                                     // Add student record if there is no record in DB
                                     studentDao.getByPersonId(person.getId()).ifPresentOrElse(student -> {}, () -> {
                                         Student student = new Student();
                                         student.setPerson(person);
                                         studentDao.insert(student);
                                     });
                                 }
                                 case Role.RoleType.TEACHER -> {
                                     teacherDao.getByPersonId(person.getId()).ifPresentOrElse(teacher -> {}, () -> {
                                         Teacher teacher = new Teacher();
                                         teacher.setPerson(person);
                                         teacherDao.insert(teacher);
                                     });
                                 }
                             }
                        });
            });
        }
    }

    private void logRoles(String message, List<Role> roles) {
        if (logger.isTraceEnabled()) {
            logger.trace(message, roles.stream()
                    .map(Role::getName)
                    .collect(Collectors.joining(", ")));
        }
    }

    @Override
    public void update(User user) {
        userDao.update(user);
    }

    @Transactional
    @Override
    public void delete(int id) {
        // Roles are deleted first
        userRolesDao.deleteByUserId(id);
        userDao.delete(id);
    }

    @Override
    public List<User> getAll() {
        return userDao.getAll();
    }

    @Override
    public Optional<User> getByUsername(String username) {
        return userDao.getByUsername(username);
    }

    @Override
    public Optional<User> getById(Integer id) {
        return userDao.getById(id);
    }

    @Override
    public Optional<User> getByPersonId(Integer personId) {
        return userDao.getByPersonId(personId);
    }
}
