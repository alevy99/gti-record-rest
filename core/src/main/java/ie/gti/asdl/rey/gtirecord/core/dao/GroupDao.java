package ie.gti.asdl.rey.gtirecord.core.dao;

import ie.gti.asdl.rey.gtirecord.model.entity.Course;
import ie.gti.asdl.rey.gtirecord.model.entity.Department;
import ie.gti.asdl.rey.gtirecord.model.entity.Group;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Andrei Levchenko
 */
@Repository
public interface GroupDao {

    Optional<Group> getById(Integer id);

    List<Group> getAll();

    List<Group> getByCourseId(Integer courseId);

    Map<Course, List<Group>> getAllGroupedByCourse();

    Optional<Integer> insert(Group group);

    void update(Group group);

    void delete(Integer id);

}
