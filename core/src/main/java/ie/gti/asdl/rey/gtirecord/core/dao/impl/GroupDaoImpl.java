package ie.gti.asdl.rey.gtirecord.core.dao.impl;

import ie.gti.asdl.rey.gtirecord.core.dao.GroupDao;
import ie.gti.asdl.rey.gtirecord.core.dao.mapper.CourseRowMapper;
import ie.gti.asdl.rey.gtirecord.core.dao.mapper.GroupRowMapper;
import ie.gti.asdl.rey.gtirecord.model.entity.Course;
import ie.gti.asdl.rey.gtirecord.model.entity.Department;
import ie.gti.asdl.rey.gtirecord.model.entity.Group;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * @author Andrei Levchenko
 */
@Repository
public class GroupDaoImpl implements GroupDao {

    private static final RowMapper<Group> groupRowMapper = new GroupRowMapper();

    private static final RowMapper<Course> courseRowMapper = new CourseRowMapper();

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GroupDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Department> getById(Integer id) {
        return Optional.empty();
    }

    @Override
    public List<Department> getAll() {
        return List.of();
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
                        Course groupCourse = courseRowMapper.mapRow(rs, rowNum);
                        assert group != null;
                        group.setCourse(groupCourse);
                        map.computeIfAbsent(group.getCourse(), course -> new ArrayList<>()).add(group);
                        rowNum++;
                    }
                    return map;
                }
        );
    }

    @Override
    public Optional<Integer> insert(Department department) {
        return Optional.empty();
    }

    @Override
    public void update(Department department) {

    }

    @Override
    public void delete(Integer id) {

    }
}
