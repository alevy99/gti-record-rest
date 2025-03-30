package ie.gti.asdl.rey.gtirecord.core.service;

import ie.gti.asdl.rey.gtirecord.model.entity.Course;
import ie.gti.asdl.rey.gtirecord.model.entity.Department;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public interface CourseService {

    Optional<Course> getById(int id);

    List<Course> getAll();

    Map<Department, List<Course>> getAllGroupedByDepartment();

    Optional<Integer> insert(Course course);

    void update(Course course);

    void delete(int id);

}
