package ie.gti.asdl.rey.gtirecord.core.service;

import ie.gti.asdl.rey.gtirecord.model.entity.Course;
import ie.gti.asdl.rey.gtirecord.model.entity.Module;
import org.springframework.stereotype.Service;

/**
 * @author Andrei Levchenko
 */
/**
 * Service interface managing the association between {@link Course} and
 * {@link Module} entities (a many-to-many relationship).
 */
@Service
public interface CourseModuleService {

    /**
     * Creates an association between the given course and module.
     *
     * @param courseId the ID of the course to associate
     * @param moduleId the ID of the module to associate
     */
    void insert(Integer courseId, Integer moduleId);

    /**
     * Removes the association between the given course and module.
     *
     * @param courseId the ID of the course whose association should be removed
     * @param moduleId the ID of the module whose association should be removed
     */
    void delete(Integer courseId, Integer moduleId);

//    void deleteByCourseId(Integer courseId);
//
//    void deleteByModuleId(Integer moduleId);

}
