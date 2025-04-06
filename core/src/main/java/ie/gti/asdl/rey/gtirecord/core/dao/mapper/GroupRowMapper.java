package ie.gti.asdl.rey.gtirecord.core.dao.mapper;

import ie.gti.asdl.rey.gtirecord.model.entity.*;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Andrei Levchenko
 */
public class GroupRowMapper implements RowMapper<Group> {

    @Override
    public Group mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
        Group group = new Group();
        group.setId(rs.getInt("group_id"));
        group.setName(rs.getString("group_name"));
        group.setCode(rs.getString("group_code"));

        Course course = new Course();
        course.setId(rs.getInt("course_id"));
        group.setCourse(course);

//        Department department = new Department();
//        department.setId(rs.getInt("department_id"));
//        department.setName(rs.getString("department_name"));
//        group.setDepartment(department);
//
//        CourseType courseType = new CourseType();
//        courseType.setId(rs.getInt("course_type_id"));
//        courseType.setType(rs.getString("type"));
//        group.setCourseType(courseType);
//
//        QQILevel qqiLevel = new QQILevel();
//        qqiLevel.setId(rs.getInt("qqi_level_id"));
//        qqiLevel.setName(rs.getString("qqi_name"));
//        group.setQqiLevel(qqiLevel);

        return group;
    }
}