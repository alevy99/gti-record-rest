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

    Optional<Department> getById(Integer id);

    List<Department> getAll();

    Map<Course, List<Group>> getAllGroupedByCourse();

    Optional<Integer> insert(Department department);

    void update(Department department);

    void delete(Integer id);

}
