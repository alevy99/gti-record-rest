package ie.gti.asdl.rey.gtirecord.core.service.impl;

import ie.gti.asdl.rey.gtirecord.core.dao.*;
import ie.gti.asdl.rey.gtirecord.core.service.StudentService;
import ie.gti.asdl.rey.gtirecord.core.service.UserService;
import ie.gti.asdl.rey.gtirecord.model.entity.*;
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
public class StudentServiceImpl implements StudentService {

    private final StudentDao studentDao;
    private final TeacherDao teacherDao;
    private final PersonDao personDao;
    private final UserDao userDao;
    private final UserRolesDao userRolesDao;
    private final UserService userService;

    @Autowired
    public StudentServiceImpl(StudentDao studentDao, TeacherDao teacherDao, PersonDao personDao, UserDao userDao, UserRolesDao userRolesDao, UserService userService) {
        this.studentDao = studentDao;
        this.teacherDao = teacherDao;
        this.personDao = personDao;
        this.userDao = userDao;
        this.userRolesDao = userRolesDao;
        this.userService = userService;
    }

    @Override
    public List<Student> getAll() {
        return studentDao.getAll();
    }

    @Override
    public Optional<Student> getByPersonId(Integer personId) {
        return studentDao.getByPersonId(personId);
    }

    @Override
    public List<Student> getByGroupId(Integer groupId) {
        return studentDao.getByGroupId(groupId);
    }

    @Transactional
    @Override
    public Optional<Integer> insert(Student student) {
        final ContainerOfAny<Optional<Integer>> result = new ContainerOfAny<>();
        result.setValue(Optional.empty());
        personDao.getById(student.getPerson().getId()).ifPresentOrElse((person) -> {
            result.setValue(studentDao.insert(student));
        }, () -> {
            if (personDao.insert(student.getPerson()).isPresent()) {
                result.setValue(studentDao.insert(student));
            }
        });
        return result.getValue();
    }

    @Transactional
    @Override
    public Optional<Integer> insertWithUser(Student student, User user) {
        var newStudentPersonIdOpt = insert(student);
        if (newStudentPersonIdOpt.isPresent()) {
            user.setPersonId(newStudentPersonIdOpt.get());
            userService.insert(user);
        }
        return newStudentPersonIdOpt;
    }

    @Transactional
    @Override
    public void update(Student student) {
        studentDao.update(student);
        personDao.update(student.getPerson());
    }

    @Override
    public void updateStudentOnly(Student student) {
        studentDao.update(student);
    }

    @Transactional
    @Override
    public void updateWithUser(Student student, User user) {
        update(student);
        userService.saveUserWithRoles(user);
    }

    @Transactional
    @Override
    public void save(Student student) {
        if ((student == null) || (student.getPerson() == null)) return;
        if (student.getPerson().getId() == null) {
            insert(student);
        } else {
            update(student);
        }
    }

    @Transactional
    @Override
    public Optional<Integer> saveWithUser(Student student, User user) {
        if ((student == null) || (student.getPerson() == null)) return Optional.empty();
        if (student.getPerson().getId() == null) {
            insertWithUser(student, user);
        } else {
            updateWithUser(student, user);
        }
        return Optional.of(student.getPerson().getId());
    }

    @Transactional
    @Override
    public void delete(Integer personId) {
        if (personId == null) return;
        studentDao.delete(personId);
        // Check if there is a teacher associated with the same person
        if (teacherDao.getByPersonId(personId).isEmpty()) {
            // If there is no such a teacher then delete the person
            personDao.delete(personId);
            // And delete user as well if there is one
            userDao.getByPersonId(personId).ifPresent(user -> {
                userService.delete(user.getId());
            });
        } else {
            // Get user by person id and delete student role in it
            userDao.getByPersonId(personId).ifPresent(user -> {
                userRolesDao.delete(user.getId(), Role.RoleType.STUDENT.asRole().getId());
            });
        }
    }
}
