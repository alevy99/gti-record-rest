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

//    private static final RowMapper<Person> personRowMapper = new BeanPropertyRowMapper<>(Person.class);

    private static final StudentRowMapper studentRowMapper = new StudentRowMapper();

//    private static final GroupRowMapper groupRowMapper = new GroupRowMapper();

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
//        p.first_name, p.last_name, p.gender, p.date_of_birth, p.phone_num, p.email, p.ppsn,
        return jdbcTemplate.query(sql, studentRowMapper);
//        return jdbcTemplate.query(sql, (rs) -> {
//            List<Student> students = new ArrayList<>();
//
//            int row = 0;
//            while (rs.next()) {
////                Student student = studentRowMapper.mapRow(rs, row);
////                Person person = personRowMapper.mapRow(rs, row);
////                Group group = groupRowMapper.mapRow(rs, row);
////                if (student != null) {
////                    student.setPerson(person);
////                    student.setGroup(group);
////                }
//                students.add(studentRowMapper.mapRow(rs, row));
//                row++;
//            }
//            return students;
//        });
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
//        return Optional.ofNullable(jdbcTemplate.query(sql, rs -> {
//            if (! rs.next()) return null;
//
//            // Always take the very first result from the ResultSet
////            Student student = studentRowMapper.mapRow(rs, 0);
////            Person person = personRowMapper.mapRow(rs, 0);
////            if (student != null) {
////                student.setPerson(person);
////            }
//            // Always take the very first result from the ResultSet
//            return studentRowMapper.mapRow(rs, 0);
//        }, personId));
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
