package ie.gti.asdl.rey.gtirecord.core.service.impl;

import ie.gti.asdl.rey.gtirecord.core.dao.CourseModuleDao;
import ie.gti.asdl.rey.gtirecord.core.dao.GroupDao;
import ie.gti.asdl.rey.gtirecord.core.dao.GroupModuleDao;
import ie.gti.asdl.rey.gtirecord.core.service.CourseModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Andrei Levchenko
 */
@Repository
public class CourseModuleServiceImpl implements CourseModuleService {

    private final CourseModuleDao courseModuleDao;
    private final GroupModuleDao groupModuleDao;
    private final GroupDao groupDao;

    @Autowired
    public CourseModuleServiceImpl(CourseModuleDao courseModuleDao, GroupModuleDao groupModuleDao, GroupDao groupDao) {
        this.courseModuleDao = courseModuleDao;
        this.groupModuleDao = groupModuleDao;
        this.groupDao = groupDao;
    }

    @Transactional
    @Override
    public void insert(Integer courseId, Integer moduleId) {
        courseModuleDao.insert(courseId, moduleId);
        groupDao.getByCourseId(courseId).forEach(group -> {
            groupModuleDao.insert(group.getId(), moduleId, null); // Add groupModule with no Teacher
        });
    }

    @Transactional
    @Override
    public void delete(Integer courseId, Integer moduleId) {
        courseModuleDao.delete(courseId, moduleId);
        groupDao.getByCourseId(courseId).forEach(group -> {
            groupModuleDao.deleteByGroupIdAndModuleId(group.getId(), moduleId); // Delete groupModule by course and module ID
        });
    }

//    @Transactional
//    @Override
//    public void deleteByCourseId(Integer courseId) {
//        courseModuleDao.deleteByCourseId(courseId);
//        groupDao.getByCourseId(courseId).forEach(group -> {
//            groupModuleDao.deleteByGroupId(group.getId()); // Add groupModule with no Teacher
//        });
//    }
//
//    @Override
//    public void deleteByModuleId(Integer moduleId) {
//        courseModuleDao.deleteByModuleId(moduleId);
//    }
}
