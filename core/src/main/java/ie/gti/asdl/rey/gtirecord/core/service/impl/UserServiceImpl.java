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
            logRoles("New roles: {}", user.getRoles());

            List<Role> currentRoles = userDB.getRoles();
            logRoles("Current roles: {}", currentRoles);

            List<Role> rolesToInsert = new ArrayList<>(user.getRoles());
            rolesToInsert.removeAll(currentRoles);
            logRoles("Roles to insert: {}", rolesToInsert);
            userRolesDao.insert(user.getId(), rolesToInsert);

            List<Role> rolesToDelete = new ArrayList<>(currentRoles);
            rolesToDelete.removeAll(user.getRoles());
            logRoles("Roles to delete: {}", rolesToDelete);
            userRolesDao.deleteByUserId(user.getId(), rolesToDelete);
        }, () -> {
            throw new RuntimeException("User was not found: ID = " + user.getId());
        });
    }

    private void logRoles(String message, List<Role> roles) {
        if (logger.isTraceEnabled()) {
            logger.trace(message, roles.stream()
                    .map(Role::getName)
                    .collect(Collectors.joining(", ")));
        }
    }

    @Override
    public void updateUser(User user) {
        userDao.update(user);
    }

    @Transactional
    @Override
    public Optional<Integer> insertPersonToUser(User user) {
        if (user.getPersonId() != null) {
            return Optional.of(user.getPersonId());
        }
        Person person = new Person();
        Optional<Integer> newPersonIdOpt = personService.insert(person);
        newPersonIdOpt.ifPresent(personId -> {
            user.setPersonId(personId);
            userDao.update(user);
        });
        return newPersonIdOpt;
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
