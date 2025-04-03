package ie.gti.asdl.rey.gtirecord.core.service.impl;

import ie.gti.asdl.rey.gtirecord.core.dao.*;
import ie.gti.asdl.rey.gtirecord.core.service.StudentService;
import ie.gti.asdl.rey.gtirecord.core.service.UserService;
import ie.gti.asdl.rey.gtirecord.model.entity.Role;
import ie.gti.asdl.rey.gtirecord.model.entity.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    public StudentServiceImpl(StudentDao studentDao, TeacherDao teacherDao, PersonDao personDao, UserDao userDao, UserRolesDao userRolesDao) {
        this.studentDao = studentDao;
        this.teacherDao = teacherDao;
        this.personDao = personDao;
        this.userDao = userDao;
        this.userRolesDao = userRolesDao;
    }

    @Override
    public Optional<Student> getByPersonId(Integer personId) {
        return studentDao.getByPersonId(personId);
    }

    @Override
    public void insert(Student student) {
        studentDao.insert(student);
    }

    @Transactional
    @Override
    public void update(Student student) {
        studentDao.update(student);
        personDao.update(student.getPerson());
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
    public void delete(Integer personId) {
        if (personId == null) return;
        studentDao.delete(personId);
        // Check if there is a teacher associated with the same person
        if (teacherDao.getByPersonId(personId).isEmpty()) {
            // If there is no such a teacher then delete the person
            personDao.delete(personId);
        }
        // Get user by person id and delete student role in it
        userDao.getByPersonId(personId).ifPresent(user -> {
            userRolesDao.delete(user.getId(), Role.RoleType.STUDENT.asRole().getId());
        });
    }
}
