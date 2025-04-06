package ie.gti.asdl.rey.gtirecord.core.dao.mapper;

import ie.gti.asdl.rey.gtirecord.model.entity.Course;
import ie.gti.asdl.rey.gtirecord.model.entity.CourseType;
import ie.gti.asdl.rey.gtirecord.model.entity.Department;
import ie.gti.asdl.rey.gtirecord.model.entity.QQILevel;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CourseRowMapper implements RowMapper<Course> {

    @Override
    public Course mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
        ResultSetHelper helper = new ResultSetHelper(rs);

        Course course = new Course();
        course.setId(rs.getInt("id"));
        course.setName(rs.getString("name"));
        course.setCode(rs.getString("code"));

        Department department = new Department();
        department.setId(rs.getInt("department_id"));
        if (helper.hasColumn("department_name")) {
            department.setName(rs.getString("department_name"));
        }
        course.setDepartment(department);

        CourseType courseType = new CourseType();
        courseType.setId(rs.getInt("course_type_id"));
        if (helper.hasColumn("type")) {
            courseType.setType(rs.getString("type"));
        }
        course.setCourseType(courseType);

        QQILevel qqiLevel = new QQILevel();
        qqiLevel.setId(rs.getInt("qqi_level_id"));
        if (helper.hasColumn("qqi_name")) {
            qqiLevel.setName(rs.getString("qqi_name"));
        }
        course.setQqiLevel(qqiLevel);

        return course;
    }
}
