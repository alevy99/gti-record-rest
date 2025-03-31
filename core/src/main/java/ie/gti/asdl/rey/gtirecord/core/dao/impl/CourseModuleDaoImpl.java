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
    public void delete(int courseId, int moduleId) {
        final String sql = "DELETE FROM course_has_module WHERE course_id = ? and module_id = ?";
        jdbcTemplate.update(sql, courseId, moduleId);
    }

    @Override
    public void deleteByCourseId(int courseId) {
        final String sql = "DELETE FROM course_has_module WHERE course_id = ?";
        jdbcTemplate.update(sql, courseId);
    }

    @Override
    public void deleteByModuleId(int moduleId) {
        final String sql = "DELETE FROM course_has_module WHERE module_id = ?";
        jdbcTemplate.update(sql, moduleId);
    }

}
