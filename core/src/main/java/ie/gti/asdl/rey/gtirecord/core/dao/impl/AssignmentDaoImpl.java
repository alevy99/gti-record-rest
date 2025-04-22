package ie.gti.asdl.rey.gtirecord.core.dao.impl;

import ie.gti.asdl.rey.gtirecord.core.dao.AssignmentDao;
import ie.gti.asdl.rey.gtirecord.core.dao.mapper.AssignmentRowMapper;
import ie.gti.asdl.rey.gtirecord.core.service.ValidationService;
import ie.gti.asdl.rey.gtirecord.model.entity.Assignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Andrei Levchenko
 */
@Repository
public class AssignmentDaoImpl implements AssignmentDao {

    private static final AssignmentRowMapper assignmentRowMapper = new AssignmentRowMapper();

    private final JdbcTemplate jdbcTemplate;
    private final ValidationService validationService;

    @Autowired
    public AssignmentDaoImpl(JdbcTemplate jdbcTemplate, ValidationService validationService) {
        this.jdbcTemplate = jdbcTemplate;
        this.validationService = validationService;
    }

    @Override
    public Optional<Assignment> getById(Integer id) {
        if (id == null) return Optional.empty();
        final String sql = """
            SELECT a.id as assignment_id, a.name as assignment_name, a.weighting,
                   g.id as group_id, g.name as group_name, g.code as group_code,
                   m.id as module_id, m.name as module_name, m.code as module_code,
                   gm.*
            FROM assignment a, group_has_module gm, `group` g, module m
            WHERE a.group_module_id = gm.id and gm.group_id = g.id and gm.module_id = m.id and a.id = ?""";
        return jdbcTemplate.query(sql, assignmentRowMapper, id).stream().findFirst();
    }

    @Override
    public List<Assignment> getByGroupModule(Integer groupModuleId) {
        if (groupModuleId == null) return new ArrayList<>();
        final String sql = """
            SELECT a.id as assignment_id, a.name as assignment_name, a.weighting,
                   g.id as group_id, g.name as group_name, g.code as group_code,
                   m.id as module_id, m.name as module_name, m.code as module_code,
                   gm.*
            FROM assignment a, group_has_module gm, `group` g, module m
            WHERE a.group_module_id = gm.id and gm.group_id = g.id and gm.module_id = m.id and gm.id = ?""";
        return jdbcTemplate.query(sql, assignmentRowMapper, groupModuleId);
    }

    @Override
    public List<Assignment> getAll() {
        final String sql = """
            SELECT a.id as assignment_id, a.name as assignment_name, a.weighting, a.group_module_id,
                   g.id as group_id, g.name as group_name, g.code as group_code,
                   m.id as module_id, m.name as module_name, m.code as module_code,
                   gm.*
            FROM assignment a, group_has_module gm, `group` g, module m
            WHERE a.group_module_id = gm.id and gm.group_id = g.id and gm.module_id = m.id""";
        return jdbcTemplate.query(sql, assignmentRowMapper);
    }

    @Override
    public List<Assignment> getByGroupId(Integer groupId) {
        final String sql = """
            SELECT a.id as assignment_id, a.name as assignment_name, a.weighting,
                   gm.*, g.*, m.*
            FROM assignment a, group_has_module gm, `group` g, module m
            WHERE a.group_module_id = gm.id and gm.group_id = g.id and gm.module_id = m.id and g.id = ?""";
        return jdbcTemplate.query(sql, assignmentRowMapper, groupId);
    }

    @Override
    public Optional<Integer> insert(Assignment assignment) {
        if (assignment == null) return Optional.empty();
        final String sql = "INSERT INTO assignment (group_module_id, name, weighting) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            @NonNull
            public PreparedStatement createPreparedStatement(@NonNull Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, assignment.getGroupModule().getId());
                ps.setString(2, assignment.getName());
                if (assignment.getWeighting() != null) {
                    ps.setInt(3, assignment.getWeighting());
                } else {
                    ps.setNull(3, Types.INTEGER);
                }
                return ps;
            }
        }, keyHolder);

        if (keyHolder.getKey() == null) {
            return Optional.empty();
        } else {
            assignment.setId(keyHolder.getKey().intValue());
            return Optional.of(assignment.getId());
        }
    }

    @Override
    public void update(Assignment assignment) {
        if ((assignment == null) || (assignment.getId() == null)) return;
        final String sql = "UPDATE assignment SET name = ?, weighting = ?, group_module_id = ? WHERE id = ?";
        jdbcTemplate.update(sql, assignment.getName(), assignment.getWeighting(), assignment.getGroupModule().getId(), assignment.getId());
    }

    @Override
    public void delete(Integer id) {
        if (id == null) return;
        final String sql = "DELETE FROM assignment WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void deleteByGroupId(Integer groupId) {
        if (groupId == null) return;
        final String sql = """
                DELETE FROM assignment
                WHERE group_module_id IN (
                    SELECT id FROM group_has_module WHERE group_id = ?
                )""";
        jdbcTemplate.update(sql, groupId);
    }
}
