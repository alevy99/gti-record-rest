package ie.gti.asdl.rey.gtirecord.core.dao.impl;

import ie.gti.asdl.rey.gtirecord.core.dao.TeacherModuleDao;
import ie.gti.asdl.rey.gtirecord.core.dao.mapper.ModuleRowMapper;
import ie.gti.asdl.rey.gtirecord.core.dao.mapper.TeacherModuleRowMapper;
import ie.gti.asdl.rey.gtirecord.core.dao.mapper.TeacherRowMapper;
import ie.gti.asdl.rey.gtirecord.model.entity.Teacher;
import ie.gti.asdl.rey.gtirecord.model.entity.TeacherModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrei Levchenko
 */
@Repository
public class TeacherModuleDaoImpl implements TeacherModuleDao {

    private final JdbcTemplate jdbcTemplate;

    public static final TeacherModuleRowMapper teacherModuleRowMapper = new TeacherModuleRowMapper();

    @Autowired
    public TeacherModuleDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<TeacherModule> getByGroupId(Integer groupId) {
        if (groupId == null) return new ArrayList<>();
        final String sql = """
                SELECT t.*, p.*, m.name as module_name, m.code as module_code
                FROM group_has_module gm, teacher t, person p, module m
                WHERE t.person_id = p.id and gm.teacher_person_id = t.person_id and m.id = gm.module_id and gm.group_id = ?
            """;
        return jdbcTemplate.query(sql, teacherModuleRowMapper, groupId);
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
