package ie.gti.asdl.rey.gtirecord.core.dao.impl;

import ie.gti.asdl.rey.gtirecord.core.dao.StudentDao;
import ie.gti.asdl.rey.gtirecord.core.dao.mapper.GroupRowMapper;
import ie.gti.asdl.rey.gtirecord.core.dao.mapper.StudentRowMapper;
import ie.gti.asdl.rey.gtirecord.model.entity.Address;
import ie.gti.asdl.rey.gtirecord.model.entity.Group;
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

    private static final StudentRowMapper studentRowMapper = new StudentRowMapper();

    @Autowired
    public StudentDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Student> getAll() {
        final String sql = """
                SELECT s.*, p.*,
                       g.name as group_name, g.code as group_code, g.course_id
                FROM student s
                INNER JOIN person p on p.id = s.person_id
                LEFT OUTER JOIN `group` g ON s.group_id = g.id
                """;
        return jdbcTemplate.query(sql, studentRowMapper);
    }

    @Override
    public Optional<Student> getByPersonId(Integer personId) {
        if (personId == null) return Optional.empty();
        final String sql = """
                SELECT s.*, p.*
                FROM student s, person p
                WHERE s.person_id = p.id AND s.person_id = ?""";
        List<Student> students = jdbcTemplate.query(sql, studentRowMapper, personId);
        return students.isEmpty() ? Optional.empty() : Optional.of(students.getFirst());
    }

    @Override
    public List<Student> getByGroupId(Integer groupId) {
        final String sql = """
                SELECT s.*, p.*,
                       g.name as group_name, g.code as group_code, g.course_id
                FROM student s, person p, `group` g
                WHERE p.id = s.person_id and s.group_id = g.id and g.id = ?
                """;
        return jdbcTemplate.query(sql, studentRowMapper, groupId);
    }

    @Override
    public Optional<Integer> insert(Student student) {
        if ((student == null) || (student.getPerson() == null) || (student.getPerson().getId() == null)) return Optional.empty();
        final String sql = "INSERT INTO student(person_id, group_id, education, is_on_erasmus, emergency_contacts) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, student.getPerson().getId(), student.getGroup().getId(), student.getEducation(), student.getOnErasmus(), student.getEmergencyContacts());
        return Optional.of(student.getPerson().getId());
    }

    @Override
    public void update(Student student) {
        if ((student == null) || (student.getPerson() == null) || (student.getPerson().getId() == null)) return;
        final String sql = "UPDATE student SET group_id = ?, education = ?, is_on_erasmus = ?, emergency_contacts = ? WHERE person_id = ?";
        jdbcTemplate.update(sql, student.getGroup().getId(), student.getEducation(), student.getOnErasmus(), student.getEmergencyContacts(), student.getPerson().getId());
    }

    @Override
    public void delete(Integer personId) {
        if (personId == null) return;
        final String sql = "DELETE FROM student WHERE person_id = ?";
        jdbcTemplate.update(sql, personId);
    }
}
