package ie.gti.asdl.rey.gtirecord.core.dao.mapper;

import ie.gti.asdl.rey.gtirecord.model.annotation.InstanceFactory;
import ie.gti.asdl.rey.gtirecord.model.entity.Course;
import ie.gti.asdl.rey.gtirecord.model.entity.CourseType;
import ie.gti.asdl.rey.gtirecord.model.entity.Department;
import ie.gti.asdl.rey.gtirecord.model.entity.QQILevel;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CourseRowMapper implements RowMapper<Course> {

    @NotNull
    @Override
    public Course mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
        ResultSetHelper helper = new ResultSetHelper(rs);

        Course course = InstanceFactory.create(Course.class);
        helper.setIntIfPresent("course_id", course::setId);
        helper.setStringIfPresent("course_name", course::setName);
        helper.setStringIfPresent("course_code", course::setCode);

        Department department = InstanceFactory.create(Department.class);
        helper.setIntIfPresent("department_id", department::setId);
        helper.setStringIfPresent("department_name", department::setName);
        course.setDepartment(department);

        CourseType courseType = InstanceFactory.create(CourseType.class);
        helper.setIntIfPresent("course_type_id", courseType::setId);
        helper.setStringIfPresent("course_type_name", courseType::setType);
        course.setCourseType(courseType);

        QQILevel qqiLevel = InstanceFactory.create(QQILevel.class);
        helper.setIntIfPresent("qqi_level_id", courseType::setId);
        helper.setStringIfPresent("qqi_name", qqiLevel::setName);
        course.setQqiLevel(qqiLevel);

        return course;
    }
}
