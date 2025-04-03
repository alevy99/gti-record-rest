package ie.gti.asdl.rey.gtirecord.core.service;

import ie.gti.asdl.rey.gtirecord.model.entity.Teacher;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author Andrei Levchenko
 */
@Service
public interface TeacherService {

    Optional<Teacher> getByPersonId(Integer personId);

    void insert(Teacher teacher);

    void update(Teacher teacher);

    void save(Teacher teacher);

    void delete(Integer personId);

}
