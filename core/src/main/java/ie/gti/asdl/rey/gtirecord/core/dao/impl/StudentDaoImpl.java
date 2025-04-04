package ie.gti.asdl.rey.gtirecord.core.dao.impl;

import ie.gti.asdl.rey.gtirecord.core.dao.StudentDao;
import ie.gti.asdl.rey.gtirecord.model.entity.Person;
import ie.gti.asdl.rey.gtirecord.model.entity.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * @author Andrei Levchenko
 */
@Repository
public class StudentDaoImpl implements StudentDao {

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<Person> personRowMapper = new BeanPropertyRowMapper<>(Person.class);

    private static final RowMapper<Student> studentRowMapper = new BeanPropertyRowMapper<>(Student.class);

    @Autowired
    public StudentDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Student> getByPersonId(Integer personId) {
        if (personId == null) return Optional.empty();
        final String sql = """
                SELECT s.certificates, p.*
                FROM student s, person p
                WHERE s.person_id = p.id AND s.person_id = ?""";
        return Optional.ofNullable(jdbcTemplate.query(sql, rs -> {
            if (! rs.next()) return null;

            // Always take the very first result from the ResultSet
            Student student = studentRowMapper.mapRow(rs, 0);
            Person person = personRowMapper.mapRow(rs, 0);
            if (student != null) {
                student.setPerson(person);
            }
            return student;
        }, personId));
    }

    @Override
    public void insert(Student student) {
        if ((student == null) || (student.getPerson() == null) || (student.getPerson().getId() == null)) return;
        final String sql = "INSERT INTO student(person_id, certificates) VALUES (?, ?)";
        jdbcTemplate.update(sql, student.getPerson().getId(), student.getEducation());
    }

    @Override
    public void update(Student student) {
        if ((student == null) || (student.getPerson() == null) || (student.getPerson().getId() == null)) return;
        final String sql = "UPDATE student SET certificates = ? WHERE person_id = ?";
        jdbcTemplate.update(sql, student.getEducation(), student.getPerson().getId());
    }

    @Override
    public void delete(Integer personId) {
        if (personId == null) return;
        final String sql = "DELETE FROM student WHERE person_id = ?";
        jdbcTemplate.update(sql, personId);
    }
}
