package ie.gti.asdl.rey.gtirecord.core.service;

import ie.gti.asdl.rey.gtirecord.model.entity.Student;
import ie.gti.asdl.rey.gtirecord.model.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author Andrei Levchenko
 */
@Service
public interface StudentService {

    List<Student> getAll();

    Optional<Student> getByPersonId(Integer personId);

    Optional<Integer> insert(Student student);

    Optional<Integer> insertWithUser(Student student, User user);

    void update(Student student);

    void updateWithUser(Student student, User user);

    void save(Student student);

    Optional<Integer> saveWithUser(Student student, User user);

    void delete(Integer personId);

}
