package ie.gti.asdl.rey.gtirecord.core.service;

import ie.gti.asdl.rey.gtirecord.model.entity.Course;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface CourseService {

    Optional<Course> getById(int id);

    List<Course> getAll();

    Optional<Integer> insert(Course course);

    void update(Course course);

    void delete(int id);

}
