package ie.gti.asdl.rey.gtirecord.core.service.impl;

import ie.gti.asdl.rey.gtirecord.core.dao.*;
import ie.gti.asdl.rey.gtirecord.core.service.TeacherService;
import ie.gti.asdl.rey.gtirecord.model.entity.Person;
import ie.gti.asdl.rey.gtirecord.model.entity.Role;
import ie.gti.asdl.rey.gtirecord.model.entity.Teacher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Autowired
    public TeacherServiceImpl(StudentDao studentDao, TeacherDao teacherDao, PersonDao personDao, UserDao userDao, UserRolesDao userRolesDao) {
        this.studentDao = studentDao;
        this.teacherDao = teacherDao;
        this.personDao = personDao;
        this.userDao = userDao;
        this.userRolesDao = userRolesDao;
    }

    @Override
    public List<Teacher> getAll() {
        return teacherDao.getAll();
    }

    @Override
    public Optional<Teacher> getByPersonId(Integer personId) {
        return teacherDao.getByPersonId(personId);
    }

    @Override
    public Optional<Integer> insert(Teacher teacher) {
        return teacherDao.insert(teacher);
    }

    @Override
    public void update(Teacher teacher) {
        teacherDao.update(teacher);
    }

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
    public void delete(Integer personId) {
        if (personId == null) return;
        teacherDao.delete(personId);
        // Check if there is a student associated with the same person
        if (studentDao.getByPersonId(personId).isEmpty()) {
            // If there is no such a student then delete the person
            personDao.delete(personId);
        }
        // Get user by person id and delete teacher role in it
        userDao.getByPersonId(personId).ifPresent(user -> {
            userRolesDao.delete(user.getId(), Role.RoleType.TEACHER.asRole().getId());
        });
    }
}
