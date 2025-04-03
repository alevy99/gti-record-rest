package ie.gti.asdl.rey.gtirecord.core.dao;

import ie.gti.asdl.rey.gtirecord.model.entity.Course;
import ie.gti.asdl.rey.gtirecord.model.entity.Department;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CourseDao {

    Optional<Course> getById(Integer id);

    List<Course> getAll();

    Map<Department, List<Course>> getAllGroupedByDepartment();

    Optional<Integer> insert(Course course);

    void update(Course course);

    void delete(int id);

}
