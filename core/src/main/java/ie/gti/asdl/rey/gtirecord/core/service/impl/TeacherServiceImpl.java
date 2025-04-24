package ie.gti.asdl.rey.gtirecord.core.service.impl;

import ie.gti.asdl.rey.gtirecord.core.dao.*;
import ie.gti.asdl.rey.gtirecord.core.service.TeacherService;
import ie.gti.asdl.rey.gtirecord.core.service.UserService;
import ie.gti.asdl.rey.gtirecord.model.entity.Role;
import ie.gti.asdl.rey.gtirecord.model.entity.Teacher;
import ie.gti.asdl.rey.gtirecord.model.entity.User;
import ie.gti.asdl.rey.gtirecord.model.util.ContainerOfAny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * @author Andrei Levchenko
 */
@Service
public class TeacherServiceImpl implements TeacherService {

    private final StudentDao studentDao;
    private final TeacherDao teacherDao;
    private final PersonDao personDao;
    private final UserDao userDao;
    private final UserRolesDao userRolesDao;
    private final UserService userService;

    @Autowired
    public TeacherServiceImpl(StudentDao studentDao, TeacherDao teacherDao, PersonDao personDao, UserDao userDao, UserRolesDao userRolesDao, UserService userService) {
        this.studentDao = studentDao;
        this.teacherDao = teacherDao;
        this.personDao = personDao;
        this.userDao = userDao;
        this.userRolesDao = userRolesDao;
        this.userService = userService;
    }

    @Override
    public List<Teacher> getAll() {
        return teacherDao.getAll();
    }

    @Override
    public List<Teacher> getByModuleId(Integer moduleId) {
        return teacherDao.getByModuleId(moduleId);
    }

    @Override
    public Optional<Teacher> getByPersonId(Integer personId) {
        return teacherDao.getByPersonId(personId);
    }

    @Transactional
    @Override
    public Optional<Integer> insert(Teacher teacher) {
        final ContainerOfAny<Optional<Integer>> result = new ContainerOfAny<>();
        result.setValue(Optional.empty());
        personDao.getById(teacher.getPerson().getId()).ifPresentOrElse((person) -> {
            result.setValue(teacherDao.insert(teacher));
        }, () -> {
            if (personDao.insert(teacher.getPerson()).isPresent()) {
                result.setValue(teacherDao.insert(teacher));
            }
        });
        return result.getValue();
    }

    @Transactional
    @Override
    public Optional<Integer> insertWithUser(Teacher teacher, User user) {
        var newTeacherPersonIdOpt = insert(teacher);
        if (newTeacherPersonIdOpt.isPresent()) {
            user.setPersonId(newTeacherPersonIdOpt.get());
            userService.insert(user);
        }
        return newTeacherPersonIdOpt;
    }

    @Transactional
    @Override
    public void update(Teacher teacher) {
        teacherDao.update(teacher);
        personDao.update(teacher.getPerson());
    }

    @Transactional
    @Override
    public void updateWithUser(Teacher teacher, User user) {
        update(teacher);
        userService.saveUserWithRoles(user);
    }

    @Transactional
    @Override
    public void save(Teacher teacher) {
        if ((teacher == null) || (teacher.getPerson() == null)) return;
        if (teacher.getPerson().getId() == null) {
            insert(teacher);
        } else {
            update(teacher);
        }
    }

    @Transactional
    @Override
    public Optional<Integer> saveWithUser(Teacher teacher, User user) {
        if ((teacher == null) || (teacher.getPerson() == null)) return Optional.empty();
        if (teacher.getPerson().getId() == null) {
            insertWithUser(teacher, user);
        } else {
            updateWithUser(teacher, user);
        }
        return Optional.of(teacher.getPerson().getId());
    }

    @Transactional
    @Override
    public void delete(Integer personId) {
        if (personId == null) return;
        teacherDao.delete(personId);
        // Check if there is a student associated with the same person
        if (studentDao.getByPersonId(personId).isEmpty()) {
            // If there is no such a student, then delete the person
            personDao.delete(personId);
            // And delete the user as well if there is one
            userDao.getByPersonId(personId).ifPresent(user -> {
                userService.delete(user.getId());
            });
        } else {
            // Get a user by person id and delete a teacher role in it
            userDao.getByPersonId(personId).ifPresent(user -> {
                userRolesDao.delete(user.getId(), Role.RoleType.TEACHER.asRole().getId());
            });
        }
    }
}
