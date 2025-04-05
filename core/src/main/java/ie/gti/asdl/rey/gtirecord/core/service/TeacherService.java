package ie.gti.asdl.rey.gtirecord.core.service;

import ie.gti.asdl.rey.gtirecord.model.entity.Teacher;
import ie.gti.asdl.rey.gtirecord.model.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author Andrei Levchenko
 */
@Service
public interface TeacherService {

    List<Teacher> getAll();

    Optional<Teacher> getByPersonId(Integer personId);

    Optional<Integer> insert(Teacher teacher);

    Optional<Integer> insertWithUser(Teacher teacher, User user);

    void update(Teacher teacher);

    void updateWithUser(Teacher teacher, User user);

    void save(Teacher teacher);

    Optional<Integer> saveWithUser(Teacher teacher, User user);

    void delete(Integer personId);
}
