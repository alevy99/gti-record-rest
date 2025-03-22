package ie.gti.asdl.rey.gtirecord.core.dao;

import ie.gti.asdl.rey.gtirecord.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;
import java.util.Optional;

@Repository
public class PersonDaoImpl implements PersonDao {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Person> personRowMapper = new BeanPropertyRowMapper<Person>(Person.class);

    @Autowired
    public PersonDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Person> getAllPersons() {
        final String sql = "SELECT * FROM person";
        return jdbcTemplate.query(sql, personRowMapper);
    }

    @Override
    public Optional<Person> getPersonById(int id) {
        final String sql = "SELECT * FROM person WHERE id = ?";
        return Optional.ofNullable(jdbcTemplate.queryForObject(sql, personRowMapper, id));
    }

    @Override
    public Optional<Integer> insertPerson(Person person) {
        final String sql = """
                INSERT INTO person
                (first_name, last_name, date_of_birth, phone_num, email, ppsn)
                VALUES (?,?,?,?,?,?);
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            @NonNull
            public PreparedStatement createPreparedStatement(@NonNull Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, person.getFirstName());
                ps.setString(2, person.getLastName());
                ps.setDate(3, Date.valueOf(person.getDateOfBirth()));
                ps.setString(4, person.getPhoneNum());
                ps.setString(5, person.getEmail());
                ps.setString(6, person.getPpsn());
                return ps;
            }
        }, keyHolder);

        return Optional.ofNullable((Integer) keyHolder.getKey());
    }

    @Override
    public void updatePerson(Person person) {
        final String sql = """
                UPDATE person
                SET first_name = ?, last_name = ?, date_of_birth = ?, phone_num = ?, email = ?, ppsn = ?
                WHERE id = ?;
                """;
        jdbcTemplate.update(sql, person.getFirstName(), person.getLastName(), Date.valueOf(person.getDateOfBirth()),
                person.getPhoneNum(), person.getEmail(), person.getPpsn(), person.getId());
    }

    @Override
    public void deletePerson(int id) {
        final String sql = "DELETE FROM person WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
