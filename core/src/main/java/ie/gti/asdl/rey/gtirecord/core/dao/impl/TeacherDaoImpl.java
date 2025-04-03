package ie.gti.asdl.rey.gtirecord.core.dao.impl;

import ie.gti.asdl.rey.gtirecord.core.dao.TeacherDao;
import ie.gti.asdl.rey.gtirecord.model.entity.Person;
import ie.gti.asdl.rey.gtirecord.model.entity.Teacher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Andrei Levchenko
 */
@Repository
public class TeacherDaoImpl implements TeacherDao {

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<Person> personRowMapper = new BeanPropertyRowMapper<>(Person.class);

    private static final RowMapper<Teacher> teacherRowMapper = new BeanPropertyRowMapper<>(Teacher.class);

    @Autowired
    public TeacherDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Teacher> getByPersonId(Integer personId) {
        if (personId == null) return Optional.empty();
        final String sql = """
                SELECT t.qualification, p.*
                FROM teacher t, person p
                WHERE t.person_id = p.id AND p.id = ?""";
        return Optional.ofNullable(jdbcTemplate.query(sql, (rs) -> {
            if (! rs.next()) return null;

            // Always take the very first result from the ResultSet
            Teacher teacher = teacherRowMapper.mapRow(rs, 0);
            Person person = personRowMapper.mapRow(rs, 0);
            if (teacher != null) {
                teacher.setPerson(person);
            }
            return teacher;
        }, personId));
    }

    @Override
    public void insert(Teacher teacher) {
        if ((teacher == null) || (teacher.getPerson() == null) || (teacher.getPerson().getId() == null)) return;
        final String sql = "INSERT INTO teacher(person_id, qualification) VALUES (?, ?)";
        jdbcTemplate.update(sql, teacher.getPerson().getId(), teacher.getQualification());
    }

    @Override
    public void update(Teacher teacher) {
        if ((teacher == null) || (teacher.getPerson() == null) || (teacher.getPerson().getId() == null)) return;
        final String sql = "UPDATE teacher SET qualification = ? WHERE person_id = ?";
        jdbcTemplate.update(sql, teacher.getQualification(), teacher.getPerson().getId());
    }

    @Override
    public void delete(Integer personId) {
        if (personId == null) return;
        final String sql = "DELETE FROM teacher WHERE person_id = ?";
        jdbcTemplate.update(sql, personId);
    }
}
