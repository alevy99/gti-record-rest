package ie.gti.asdl.rey.gtirecord.core.dao.impl;

import ie.gti.asdl.rey.gtirecord.core.dao.ModuleDao;
import ie.gti.asdl.rey.gtirecord.model.entity.Department;
import ie.gti.asdl.rey.gtirecord.model.entity.Module;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class ModuleDaoImpl implements ModuleDao {

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<Module> moduleRowMapper = new BeanPropertyRowMapper<Module>(Module.class);

    @Autowired
    public ModuleDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Module> getById(Integer id) {
        if (id == null) return Optional.empty();
        final String sql = "SELECT * FROM module WHERE id = ?";
        List<Module> modules = jdbcTemplate.query(sql, moduleRowMapper, id);
        return modules.isEmpty() ? Optional.empty() : Optional.of(modules.getFirst());
    }

    @Override
    public List<Module> getByCourseId(Integer courseId) {
        if (courseId == null) return List.of();
        final String sql = """
                SELECT m.id,  m.name, m.code
                FROM module m, course_has_module cm
                WHERE m.id = cm.module_id and cm.course_id = ?;
            """;
        return jdbcTemplate.query(sql, moduleRowMapper, courseId);
    }

    @Override
    public List<Module> getByTeacherPersonId(Integer teacherPersonId) {
        if (teacherPersonId == null) return List.of();
        final String sql = """
                SELECT m.id,  m.name, m.code
                FROM module m, teacher_has_module tm
                WHERE m.id = tm.module_id and tm.teacher_person_id = ?;
            """;
        return jdbcTemplate.query(sql, moduleRowMapper, teacherPersonId);
    }

    @Override
    public List<Module> getAll() {
        final String sql = "SELECT * FROM module";
        return jdbcTemplate.query(sql, moduleRowMapper);
    }

    @Override
    public Optional<Integer> insert(Module module) {
        final String sql = "INSERT INTO module (name, code) VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            @NonNull
            public PreparedStatement createPreparedStatement(@NonNull Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, module.getName());
                ps.setString(2, module.getCode());
                return ps;
            }
        }, keyHolder);

        if (keyHolder.getKey() == null) {
            return Optional.empty();
        } else {
            return Optional.of(keyHolder.getKey().intValue());
        }
    }

    @Override
    public void update(Module module) {
        final String sql = "UPDATE module SET name = ?, code = ?  WHERE id = ?";
        jdbcTemplate.update(sql, module.getName(), module.getCode(), module.getId());
    }

    @Override
    public void delete(int id) {
        final String sql = "DELETE FROM module WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
