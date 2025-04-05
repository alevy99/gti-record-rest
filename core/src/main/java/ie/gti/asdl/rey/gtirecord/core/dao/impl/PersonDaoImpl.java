package ie.gti.asdl.rey.gtirecord.core.dao.impl;

import ie.gti.asdl.rey.gtirecord.core.dao.PersonDao;
import ie.gti.asdl.rey.gtirecord.model.annotation.DescriptionUtil;
import ie.gti.asdl.rey.gtirecord.model.entity.Department;
import ie.gti.asdl.rey.gtirecord.model.entity.Person;
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

    private final RowMapper<Person> personRowMapper = new BeanPropertyRowMapper<>(Person.class);

    @Autowired
    public PersonDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Person> getAll() {
        final String sql = "SELECT * FROM person";
        return jdbcTemplate.query(sql, personRowMapper);
    }

    @Override
    public Optional<Person> getById(Integer id) {
        if (id == null) return Optional.empty();
        final String sql = "SELECT * FROM person WHERE id = ?";
        List<Person> persons = jdbcTemplate.query(sql, personRowMapper, id);
        return persons.isEmpty() ? Optional.empty() : Optional.of(persons.getFirst());
    }

    @Override
    public Optional<Integer> insert(Person person) {
        final String sql = """
                INSERT INTO person
                (first_name, last_name, gender, date_of_birth, phone_num, email, ppsn)
                VALUES (?,?,?,?,?,?,?);
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            @NonNull
            public PreparedStatement createPreparedStatement(@NonNull Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, person.getFirstName());
                ps.setString(2, person.getLastName());
                ps.setString(3, person.getGender());
                if (person.getDateOfBirth() != null) {
                    ps.setDate(4, Date.valueOf(person.getDateOfBirth()));
                } else {
                    ps.setNull(4, Types.DATE);
                }
                ps.setString(5, person.getPhoneNum());
                ps.setString(6, person.getEmail());
                ps.setString(7, person.getPpsn());
                return ps;
            }
        }, keyHolder);

        if (keyHolder.getKey() == null) {
            return Optional.empty();
        } else {
            person.setId(keyHolder.getKey().intValue());
            return Optional.of(person.getId());
        }
    }

    @Override
    public void update(Person person) {
        final String sql = """
                UPDATE person
                SET first_name = ?, last_name = ?, gender = ?, date_of_birth = ?, phone_num = ?, email = ?, ppsn = ?
                WHERE id = ?;
                """;
        java.sql.Date sqlDate = person.getDateOfBirth() != null ? Date.valueOf(person.getDateOfBirth()) : null;
//        if (person.getDateOfBirth() != null) {
//            ps.setDate(3, Date.valueOf(person.getDateOfBirth()));
//        } else {
//            ps.setNull(3, Types.DATE);
//        }
        jdbcTemplate.update(sql, person.getFirstName(), person.getLastName(), person.getGender(), sqlDate,
                person.getPhoneNum(), person.getEmail(), person.getPpsn(), person.getId());
    }

    @Override
    public void delete(int id) {
        final String sql = "DELETE FROM person WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
