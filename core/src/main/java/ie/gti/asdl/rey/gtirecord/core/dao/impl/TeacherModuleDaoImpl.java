package ie.gti.asdl.rey.gtirecord.core.dao.impl;

import ie.gti.asdl.rey.gtirecord.core.dao.TeacherModuleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * @author Andrei Levchenko
 */
@Repository
public class TeacherModuleDaoImpl implements TeacherModuleDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TeacherModuleDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void insert(Integer teacherPersonId, Integer moduleId) {
        if (teacherPersonId == null || moduleId == null) return;
        final String sql = "INSERT INTO teacher_has_module (teacher_person_id, module_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, teacherPersonId, moduleId);
    }

    @Override
    public void delete(Integer teacherPersonId, Integer moduleId) {
        if (teacherPersonId == null || moduleId == null) return;
        final String sql = "DELETE FROM teacher_has_module WHERE teacher_person_id = ? and module_id = ?";
        jdbcTemplate.update(sql, teacherPersonId, moduleId);
    }

}
