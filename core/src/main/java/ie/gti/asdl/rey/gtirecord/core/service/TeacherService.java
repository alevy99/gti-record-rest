package ie.gti.asdl.rey.gtirecord.core.service;

import ie.gti.asdl.rey.gtirecord.model.entity.Teacher;
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

    void update(Teacher teacher);

    void save(Teacher teacher);

    void delete(Integer personId);

}
