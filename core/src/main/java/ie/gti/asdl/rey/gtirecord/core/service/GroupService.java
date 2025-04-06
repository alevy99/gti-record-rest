package ie.gti.asdl.rey.gtirecord.core.service;

import ie.gti.asdl.rey.gtirecord.model.entity.Course;
import ie.gti.asdl.rey.gtirecord.model.entity.Department;
import ie.gti.asdl.rey.gtirecord.model.entity.Group;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Andrei Levchenko
 */
@Service
public interface GroupService {

    Optional<Group> getById(Integer id);

    List<Group> getAll();

    Map<Course, List<Group>> getAllGroupedByCourse();

    Optional<Integer> insert(Group group);

    void update(Group group);

    void delete(Integer id);

}
