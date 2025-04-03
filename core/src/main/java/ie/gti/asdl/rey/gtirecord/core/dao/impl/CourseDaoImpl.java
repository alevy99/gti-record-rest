package ie.gti.asdl.rey.gtirecord.core.dao.impl;

import ie.gti.asdl.rey.gtirecord.core.dao.CourseDao;
import ie.gti.asdl.rey.gtirecord.core.dao.mapper.CourseRowMapper;
import ie.gti.asdl.rey.gtirecord.model.entity.Course;
import ie.gti.asdl.rey.gtirecord.model.entity.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.*;

@Repository
public class CourseDaoImpl implements CourseDao {

    private final JdbcTemplate jdbcTemplate;

    private static final CourseRowMapper courseRowMapper = new CourseRowMapper();

    @Autowired
    public CourseDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Course> getById(Integer id) {
        if (id == null) return Optional.empty();
        final String sql = """
                    SELECT c.id, c.department_id, c.course_type_id, c.qqi_level_id, c.name, c.code, d.name as department_name, ct.type, q.name as qqi_name
                    FROM course c, department d, course_type ct, qqi_level q
                    WHERE c.department_id = d.id and c.course_type_id = ct.id and c.qqi_level_id = q.id and c.id = ?
                """;
        return Optional.ofNullable(jdbcTemplate.queryForObject(sql, courseRowMapper, id));
    }

    @Override
    public List<Course> getAll() {
        final String sql = """
                    SELECT c.id, c.department_id, c.course_type_id, c.qqi_level_id, c.name, c.code, d.name as department_name, ct.type, q.name as qqi_name
                    FROM course c, department d, course_type ct, qqi_level q
                    WHERE c.department_id = d.id and c.course_type_id = ct.id and c.qqi_level_id = q.id
                """;
        return jdbcTemplate.query(sql, courseRowMapper);
    }

    @Override
    public Map<Department, List<Course>> getAllGroupedByDepartment() {
        final String sql = """
                    SELECT c.id, c.department_id, c.course_type_id, c.qqi_level_id, c.name, c.code, d.name as department_name, ct.type, q.name as qqi_name
                    FROM course c, department d, course_type ct, qqi_level q
                    WHERE c.department_id = d.id and c.course_type_id = ct.id and c.qqi_level_id = q.id
                    ORDER BY c.department_id;
                """;
        return jdbcTemplate.query(sql,
                new ResultSetExtractor<Map<Department, List<Course>>>() {
                    @Override
                    public Map<Department, List<Course>> extractData(ResultSet rs) throws SQLException, DataAccessException {
                        Map<Department, List<Course>> map = new HashMap<>();
                        int rowNum = 0;
                        while (rs.next()) {
                            Course course = courseRowMapper.mapRow(rs, rowNum);
                            assert course != null;
                            map.computeIfAbsent(course.getDepartment(), department -> new ArrayList<>()).add(course);
                            rowNum++;
                        }
                        return map;
                    }
                }
        );
    }

    @Override
    public Optional<Integer> insert(Course course) {

        final String sql = """
                INSERT INTO course
                (department_id, qqi_level_id, course_type_id, name, code)
                VALUES (?, ?, ?, ?, ?);
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            @NonNull
            public PreparedStatement createPreparedStatement(@NonNull Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, course.getDepartment().getId());
                ps.setInt(2, course.getQqiLevel().getId());
                ps.setInt(3, course.getCourseType().getId());
                ps.setString(4, course.getName());
                ps.setString(5, course.getCode());
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
    public void update(Course course) {
        final String sql = """
                UPDATE course
                SET department_id = ?, qqi_level_id = ?, course_type_id = ?, name = ?, code = ?
                WHERE id = ?;
                """;
        jdbcTemplate.update(sql, course.getDepartment().getId(), course.getQqiLevel().getId(),
                course.getCourseType().getId(), course.getName(), course.getCode(), course.getId());
    }

    @Override
    public void delete(int id) {
        final String sql = "DELETE FROM course WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
