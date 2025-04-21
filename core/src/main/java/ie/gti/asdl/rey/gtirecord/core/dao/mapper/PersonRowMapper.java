package ie.gti.asdl.rey.gtirecord.core.dao.mapper;

import ie.gti.asdl.rey.gtirecord.model.entity.Person;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;

/**
 * @author Andrei Levchenko
 */
public class PersonRowMapper implements RowMapper<Person> {

    @NotNull
    @Override
    public Person mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
        ResultSetHelper helper = new ResultSetHelper(rs);

        Person person = new Person();
        helper.setIntIfPresent("person_id", person::setId);
        helper.setStringIfPresent("first_name", person::setFirstName);
        helper.setStringIfPresent("last_name", person::setLastName);
        helper.setStringIfPresent("gender", person::setGender);
        helper.setStringIfPresent("phone_num", person::setPhoneNum);
        helper.setStringIfPresent("email", person::setEmail);
        helper.setStringIfPresent("ppsn", person::setPpsn);

        if (helper.hasColumn("date_of_birth")) {
            person.setDateOfBirth(rs.getObject("date_of_birth", LocalDate.class));
        }

        return person;
    }
}