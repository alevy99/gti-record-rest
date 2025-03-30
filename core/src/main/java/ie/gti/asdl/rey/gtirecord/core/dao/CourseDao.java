package ie.gti.asdl.rey.gtirecord.core.dao;

import ie.gti.asdl.rey.gtirecord.model.entity.Course;

import java.util.List;
import java.util.Optional;

public interface CourseDao {

    Optional<Course> getById(int id);

    List<Course> getAll();

    Optional<Integer> insert(Course course);

    void update(Course course);

    void delete(int id);

}
