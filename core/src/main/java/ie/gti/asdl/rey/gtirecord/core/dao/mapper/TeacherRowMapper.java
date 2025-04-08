package ie.gti.asdl.rey.gtirecord.core.dao.mapper;

import ie.gti.asdl.rey.gtirecord.model.entity.Teacher;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Andrei Levchenko
 */
public class TeacherRowMapper implements RowMapper<Teacher> {

    private static final PersonRowMapper personRowMapper = new PersonRowMapper();

    @Override
    public Teacher mapRow(@NonNull ResultSet resultSet, int rowNum) throws SQLException {
        ResultSetHelper helper = new ResultSetHelper(resultSet);

        Teacher teacher = new Teacher();
        helper.setStringIfPresent("position", teacher::setPosition);
        helper.setStringIfPresent("degree", teacher::setDegree);
        helper.setIntIfPresent("work_experience", teacher::setWorkExperience);

        teacher.setPerson(personRowMapper.mapRow(resultSet, rowNum));

        return teacher;
    }

}