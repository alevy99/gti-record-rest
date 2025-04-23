package ie.gti.asdl.rey.gtirecord.core.dao.mapper;

import ie.gti.asdl.rey.gtirecord.model.entity.Student;
import ie.gti.asdl.rey.gtirecord.model.entity.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Andrei Levchenko
 */
public class StudentRowMapper implements RowMapper<Student> {

    private static final PersonRowMapper personRowMapper = new PersonRowMapper();
    private static final GroupRowMapper groupRowMapper = new GroupRowMapper();

    @NotNull
    @Override
    public Student mapRow(@NotNull ResultSet resultSet, int rowNum) throws SQLException {
        ResultSetHelper helper = new ResultSetHelper(resultSet);

        Student student = new Student();

        helper.setStringIfPresent("education", student::setEducation);
        helper.setStringIfPresent("emergency_contacts", student::setEmergencyContacts);
        helper.setBooleanIfPresent("is_on_erasmus", student::setOnErasmus);
//        helper.setBooleanIfPresent("is_on_erasmus", student::setOnErasmus);

        student.setPerson(personRowMapper.mapRow(resultSet, rowNum));
        student.setGroup(groupRowMapper.mapRow(resultSet, rowNum));

        return student;
    }

}