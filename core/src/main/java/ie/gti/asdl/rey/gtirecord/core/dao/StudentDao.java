package ie.gti.asdl.rey.gtirecord.core.dao;

import ie.gti.asdl.rey.gtirecord.model.entity.Student;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author Andrei Levchenko
 */
@Repository
public interface StudentDao {

    List<Student> getAll();

    Optional<Student> getByPersonId(Integer personId);

    Optional<Integer> insert(Student teacher);

    void update(Student teacher);

    void delete(Integer personId);

    List<Student> getByGroupId(Integer groupId);
}
