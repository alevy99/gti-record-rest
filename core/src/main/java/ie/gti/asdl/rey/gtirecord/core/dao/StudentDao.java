package ie.gti.asdl.rey.gtirecord.core.dao;

import ie.gti.asdl.rey.gtirecord.model.entity.Student;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Andrei Levchenko
 */
@Repository
public interface StudentDao {

    Optional<Student> getByPersonId(Integer id);

    void insert(Student student);

    void update(Student student);

    void delete(Integer personId);
}
