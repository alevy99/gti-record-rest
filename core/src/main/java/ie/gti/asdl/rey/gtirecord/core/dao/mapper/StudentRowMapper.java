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
    public Student mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Student student = new Student();

        student.setEducation(resultSet.getString("education"));
        student.setEmergencyContacts(resultSet.getString("emergency_contacts"));
        student.setOnErasmus(resultSet.getBoolean("is_on_erasmus"));

        student.setPerson(personRowMapper.mapRow(resultSet, rowNum));
        student.setGroup(groupRowMapper.mapRow(resultSet, rowNum));

        return student;
    }

}