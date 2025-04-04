package ie.gti.asdl.rey.gtirecord.core.service.impl;

import ie.gti.asdl.rey.gtirecord.core.dao.CourseModuleDao;
import ie.gti.asdl.rey.gtirecord.core.service.CourseModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author Andrei Levchenko
 */
@Repository
public class CourseModuleServiceImpl implements CourseModuleService {

    private final CourseModuleDao courseModuleDao;

    @Autowired
    public CourseModuleServiceImpl(CourseModuleDao courseModuleDao) {
        this.courseModuleDao = courseModuleDao;
    }

    @Override
    public void insert(Integer courseId, Integer moduleId) {
        courseModuleDao.insert(courseId, moduleId);
    }

    @Override
    public void delete(Integer courseId, Integer moduleId) {
        courseModuleDao.delete(courseId, moduleId);
    }

    @Override
    public void deleteByCourseId(Integer courseId) {
        courseModuleDao.deleteByCourseId(courseId);
    }

    @Override
    public void deleteByModuleId(Integer moduleId) {
        courseModuleDao.deleteByModuleId(moduleId);
    }
}
