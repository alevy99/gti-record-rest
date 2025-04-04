package ie.gti.asdl.rey.gtirecord.core.dao;

import org.springframework.stereotype.Repository;

/**
 * @author Andrei Levchenko
 */
@Repository
public interface CourseModuleDao {

    void insert(Integer courseId, Integer moduleId);

    void delete(Integer courseId, Integer moduleId);

    void deleteByCourseId(Integer courseId);

    void deleteByModuleId(Integer moduleId);

}
