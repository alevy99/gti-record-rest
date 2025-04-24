package ie.gti.asdl.rey.gtirecord.core.dao.impl;

import ie.gti.asdl.rey.gtirecord.core.dao.GroupDao;
import ie.gti.asdl.rey.gtirecord.core.dao.mapper.CourseRowMapper;
import ie.gti.asdl.rey.gtirecord.core.dao.mapper.GroupRowMapper;
import ie.gti.asdl.rey.gtirecord.core.service.ValidationService;
import ie.gti.asdl.rey.gtirecord.model.entity.Course;
import ie.gti.asdl.rey.gtirecord.model.entity.Group;
import ie.gti.asdl.rey.gtirecord.model.entity.Person;
import ie.gti.asdl.rey.gtirecord.model.entity.Student;
import ie.gti.asdl.rey.gtirecord.model.validation.OnUpdate;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.*;

/**
 * @author Andrei Levchenko
 */
@Repository
public class GroupDaoImpl implements GroupDao {

    private static final GroupRowMapper groupRowMapper = new GroupRowMapper();

    private final JdbcTemplate jdbcTemplate;
    private final ValidationService validationService;

    @Autowired
    public GroupDaoImpl(JdbcTemplate jdbcTemplate, ValidationService validationService) {
        this.jdbcTemplate = jdbcTemplate;
        this.validationService = validationService;
    }

    @Override
    public Optional<Group> getById(Integer id) {
        final String sql = """
                    SELECT g.id as group_id, g.name as group_name, g.code as group_code, 
                           c.id as course_id, c.name as course_name,
                           c.code as course_code, c.department_id, c.course_type_id, c.qqi_level_id
                    FROM `group` g, course c
                    WHERE g.course_id = c.id and g.id = ?
                """;
        return Optional.ofNullable(jdbcTemplate.queryForObject(sql, groupRowMapper, id));
    }

    @Override
    public List<Group> getAll() {
        final String sql = """
                    SELECT g.id as group_id, g.name as group_name, g.code as group_code,
                           c.id as course_id, c.name as course_name,
                           c.code as course_code, c.department_id, c.course_type_id, c.qqi_level_id
                    FROM `group` g, course c
                    WHERE g.course_id = c.id
                """;
        return jdbcTemplate.query(sql, groupRowMapper);
    }

    @Override
    public List<Group> getByCourseId(Integer courseId) {
        final String sql = """
                    SELECT g.id as group_id, g.name as group_name, g.code as group_code,
                           c.id as course_id, c.name as course_name,
                           c.code as course_code, c.department_id, c.course_type_id, c.qqi_level_id
                    FROM `group` g, course c
                    WHERE g.course_id = c.id and g.course_id = ?
                """;
        return jdbcTemplate.query(sql, groupRowMapper, courseId);
    }

    @Override
    public Map<Course, List<Group>> getAllGroupedByCourse() {
        final String sql = """
                    SELECT g.id as group_id, g.course_id, g.name as group_name, g.code as group_code, c.id, c.department_id, c.course_type_id, c.qqi_level_id, c.name, c.code
                    FROM `group` g, course c
                    WHERE g.course_id = c.id
                    ORDER BY g.course_id;
                """;
        return jdbcTemplate.query(sql, rs -> {
                    Map<Course, List<Group>> map = new HashMap<>();
                    int rowNum = 0;
                    while (rs.next()) {
                        Group group = groupRowMapper.mapRow(rs, rowNum);
                        map.computeIfAbsent(group.getCourse(), course -> new ArrayList<>()).add(group);
                        rowNum++;
                    }
                    return map;
                }
        );
    }

    @Override
    public Optional<Integer> insert(Group group) {
        if (!validationService.validate(group)) return Optional.empty();

        final String sql = "INSERT INTO `group` (course_id, name, code) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            @NonNull
            public PreparedStatement createPreparedStatement(@NonNull Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, group.getCourse().getId());
                ps.setString(2, group.getName());
                ps.setString(3, group.getCode());
                return ps;
            }
        }, keyHolder);

        if (keyHolder.getKey() == null) {
            return Optional.empty();
        } else {
            group.setId(keyHolder.getKey().intValue());
            return Optional.of(group.getId());
        }
    }

    @Override
    public void update(Group group) {
        if (!validationService.validate(group, OnUpdate.class)) return;
        final String sql = "UPDATE `group` SET course_id = ?, name = ?, code = ? WHERE id = ?";
        jdbcTemplate.update(sql, group.getCourse().getId(), group.getName(), group.getCode(), group.getId());
    }

    @Override
    public void delete(Integer id) {
        final String sql = "DELETE FROM `group` WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
