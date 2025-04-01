package ie.gti.asdl.rey.gtirecord.core.service;

import org.springframework.stereotype.Repository;

/**
 * @author Andrei Levchenko
 */
@Repository
public interface CourseModuleService {

    void insert(int courseId, int moduleId);

    void delete(int courseId, int moduleId);

    void deleteByCourseId(int courseId);

    void deleteByModuleId(int moduleId);

}
