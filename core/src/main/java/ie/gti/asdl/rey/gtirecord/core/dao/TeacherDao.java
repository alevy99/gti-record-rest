package ie.gti.asdl.rey.gtirecord.core.dao;

import ie.gti.asdl.rey.gtirecord.model.entity.Teacher;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author Andrei Levchenko
 */
@Repository
public interface TeacherDao {

    List<Teacher> getAll();

    Optional<Teacher> getByPersonId(Integer personId);

    Optional<Integer> insert(Teacher teacher);

    void update(Teacher teacher);

    void delete(Integer personId);
}
