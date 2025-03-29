package ie.gti.asdl.rey.gtirecord.core.service;

import ie.gti.asdl.rey.gtirecord.core.dao.UserDao;
import ie.gti.asdl.rey.gtirecord.core.dao.UserRolesDao;
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

    @Autowired
    public UserServiceImpl(UserDao userDao, UserRolesDao userRolesDao) {
        super();
        this.userDao = userDao;
        this.userRolesDao = userRolesDao;
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
    public void update(User user) {
        userDao.getById(user.getId()).ifPresentOrElse(userDB -> {
            userDao.update(user);

            logger.info("New roles: {}", user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.joining(", ")));

            List<Role> currentRoles = userDB.getRoles();

            logger.info("Current roles: {}", currentRoles.stream()
                    .map(Role::getName)
                    .collect(Collectors.joining(", ")));

            List<Role> rolesToInsert = new ArrayList<>(user.getRoles());
            rolesToInsert.removeAll(currentRoles);

            logger.info("Roles to insert: {}", rolesToInsert.stream()
                    .map(Role::getName)
                    .collect(Collectors.joining(", ")));

            userRolesDao.insert(user.getId(), rolesToInsert);

            List<Role> rolesToDelete = new ArrayList<>(currentRoles);
            rolesToDelete.removeAll(user.getRoles());

            logger.info("Roles to delete: {}", rolesToInsert.stream()
                    .map(Role::getName)
                    .collect(Collectors.joining(", ")));

            userRolesDao.delete(user.getId(), rolesToDelete);

        }, () -> {
            throw new RuntimeException("User was not found: ID = " + user.getId());
        });
    }

    @Transactional
    @Override
    public void delete(int id) {
        // Roles are deleted first
        userRolesDao.delete(id);
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
