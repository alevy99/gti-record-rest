package ie.gti.asdl.rey.gtirecord.core.service;

import ie.gti.asdl.rey.gtirecord.model.entity.Student;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author Andrei Levchenko
 */
@Service
public interface StudentService {

    Optional<Student> getByPersonId(Integer personId);

    void insert(Student student);

    void update(Student student);

    void save(Student student);

    void delete(Integer personId);

}
