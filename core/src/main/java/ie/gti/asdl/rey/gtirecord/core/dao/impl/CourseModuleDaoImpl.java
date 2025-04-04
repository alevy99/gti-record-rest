package ie.gti.asdl.rey.gtirecord.core.dao.impl;

import ie.gti.asdl.rey.gtirecord.core.dao.CourseModuleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * @author Andrei Levchenko
 */
@Repository
public class CourseModuleDaoImpl implements CourseModuleDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CourseModuleDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void insert(Integer courseId, Integer moduleId) {
        if (courseId == null || moduleId == null) return;
        final String sql = "INSERT INTO course_has_module (course_id, module_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, courseId, moduleId);
    }

    @Override
    public void delete(Integer courseId, Integer moduleId) {
        if (courseId == null || moduleId == null) return;
        final String sql = "DELETE FROM course_has_module WHERE course_id = ? and module_id = ?";
        jdbcTemplate.update(sql, courseId, moduleId);
    }

    @Override
    public void deleteByCourseId(Integer courseId) {
        if (courseId == null) return;
        final String sql = "DELETE FROM course_has_module WHERE course_id = ?";
        jdbcTemplate.update(sql, courseId);
    }

    @Override
    public void deleteByModuleId(Integer moduleId) {
        if (moduleId == null) return;
        final String sql = "DELETE FROM course_has_module WHERE module_id = ?";
        jdbcTemplate.update(sql, moduleId);
    }

}
