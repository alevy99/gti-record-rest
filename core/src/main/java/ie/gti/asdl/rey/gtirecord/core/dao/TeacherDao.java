package ie.gti.asdl.rey.gtirecord.core.dao;

import ie.gti.asdl.rey.gtirecord.model.entity.Teacher;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Andrei Levchenko
 */
@Repository
public interface TeacherDao {

    Optional<Teacher> getByPersonId(Integer personId);

    void insert(Teacher teacher);

    void update(Teacher teacher);

    void delete(Integer personId);
}
