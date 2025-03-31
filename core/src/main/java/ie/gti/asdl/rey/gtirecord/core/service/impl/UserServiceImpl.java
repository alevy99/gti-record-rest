package ie.gti.asdl.rey.gtirecord.core.service.impl;

import ie.gti.asdl.rey.gtirecord.core.dao.UserDao;
import ie.gti.asdl.rey.gtirecord.core.dao.UserRolesDao;
import ie.gti.asdl.rey.gtirecord.core.service.PersonService;
import ie.gti.asdl.rey.gtirecord.core.service.UserService;
import ie.gti.asdl.rey.gtirecord.model.entity.Person;
import ie.gti.asdl.rey.gtirecord.model.entity.Role;
import ie.gti.asdl.rey.gtirecord.model.entity.User;
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

    private final PersonService personService;

    @Autowired
    public UserServiceImpl(UserDao userDao, UserRolesDao userRolesDao, PersonService personService) {
        super();
        this.userDao = userDao;
        this.userRolesDao = userRolesDao;
        this.personService = personService;
    }

    @Transactional
    @Override
    public Optional<Integer> insert(User user) {
        Optional<Integer> newId = userDao.insert(user);
        if (newId.isEmpty()) {
            return newId;
        }
        user.setId(newId.get());
        userRolesDao.insert(user.getId(), user.getRoles());
        return newId;
    }

    @Transactional
    @Override
    public void updateUserWithRoles(User user) {
        userDao.getById(user.getId()).ifPresentOrElse(userDB -> {
            userDao.update(user);

            logger.trace("New roles: {}", user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.joining(", ")));

            List<Role> currentRoles = userDB.getRoles();

            logger.trace("Current roles: {}", currentRoles.stream()
                    .map(Role::getName)
                    .collect(Collectors.joining(", ")));

            List<Role> rolesToInsert = new ArrayList<>(user.getRoles());
            rolesToInsert.removeAll(currentRoles);

            logger.trace("Roles to insert: {}", rolesToInsert.stream()
                    .map(Role::getName)
                    .collect(Collectors.joining(", ")));

            userRolesDao.insert(user.getId(), rolesToInsert);

            List<Role> rolesToDelete = new ArrayList<>(currentRoles);
            rolesToDelete.removeAll(user.getRoles());

            logger.trace("Roles to delete: {}", rolesToInsert.stream()
                    .map(Role::getName)
                    .collect(Collectors.joining(", ")));

            userRolesDao.deleteByUserId(user.getId(), rolesToDelete);
        }, () -> {
            throw new RuntimeException("User was not found: ID = " + user.getId());
        });
    }

//    @Transactional
    @Override
    public void updateUser(User user) {
        userDao.update(user);
//        Optional<Integer> personId = personService.save(user.getPerson());
//        personId.ifPresent(personIdDB -> {
//            user.setId(personIdDB);
//            userDao.update(user);
//        });
    }

    @Transactional
    @Override
    public void insertPersonToUser(User user) {
        if (user.getPersonId() != null) {
            return;
        }
        Person person = new Person();
        Optional<Integer> newPersonIdOpt = personService.insert(person);
        newPersonIdOpt.ifPresentOrElse(personId -> {
            user.setPersonId(personId);
            userDao.update(user);
        }, () -> {
            throw new RuntimeException("Error adding new person for user ID = " + user.getId());
        });
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
    public Optional<User> getById(int id) {
        return userDao.getById(id);
    }
}
