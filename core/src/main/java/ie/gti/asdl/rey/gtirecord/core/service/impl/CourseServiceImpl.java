package ie.gti.asdl.rey.gtirecord.core.service.impl;

import ie.gti.asdl.rey.gtirecord.core.dao.CourseDao;
import ie.gti.asdl.rey.gtirecord.core.dao.CourseModuleDao;
import ie.gti.asdl.rey.gtirecord.core.service.CourseService;
import ie.gti.asdl.rey.gtirecord.model.entity.Course;
import ie.gti.asdl.rey.gtirecord.model.entity.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CourseServiceImpl implements CourseService {

    private final CourseDao courseDao;

    private final CourseModuleDao courseModuleDao;

    @Autowired
    public CourseServiceImpl(CourseDao courseDao, CourseModuleDao courseModuleDao) {
        this.courseDao = courseDao;
        this.courseModuleDao = courseModuleDao;
    }

    @Override
    public Optional<Course> getById(int id) {
        return courseDao.getById(id);
    }

    @Override
    public List<Course> getAll() {
        return courseDao.getAll();
    }

    @Override
    public Map<Department, List<Course>> getAllGroupedByDepartment() {
        return courseDao.getAllGroupedByDepartment();
    }

    @Override
    public Optional<Integer> insert(Course course) {
        var courseOpt = courseDao.insert(course);
        courseOpt.ifPresent(course::setId);
        return courseOpt;
    }

    @Override
    public void update(Course course) {
        courseDao.update(course);
    }

    @Transactional
    @Override
    public void delete(int id) {
        // Delete course_module first
        courseModuleDao.deleteByCourseId(id);
        courseDao.delete(id);
    }
}
