package ie.gti.asdl.rey.gtirecord.core.dao.impl;

import ie.gti.asdl.rey.gtirecord.core.dao.TeacherDao;
import ie.gti.asdl.rey.gtirecord.core.dao.mapper.TeacherRowMapper;
import ie.gti.asdl.rey.gtirecord.model.entity.Person;
import ie.gti.asdl.rey.gtirecord.model.entity.Student;
import ie.gti.asdl.rey.gtirecord.model.entity.Teacher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Andrei Levchenko
 */
@Repository
public class TeacherDaoImpl implements TeacherDao {

    private final JdbcTemplate jdbcTemplate;

//    private static final RowMapper<Person> personRowMapper = new BeanPropertyRowMapper<>(Person.class);

    private static final TeacherRowMapper teacherRowMapper = new TeacherRowMapper();

    @Autowired
    public TeacherDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Teacher> getAll() {
        final String sql = """
                SELECT t.*, p.*
                FROM teacher t, person p
                WHERE t.person_id = p.id;
                """;
        return jdbcTemplate.query(sql, teacherRowMapper);
    }

    @Override
    public List<Teacher> getByModuleId(Integer moduleId) {
        if (moduleId == null) return new ArrayList<>();
        final String sql = """
                SELECT t.*, p.*
                FROM teacher t, person p, teacher_has_module tm
                WHERE t.person_id = p.id and p.id = tm.teacher_person_id and tm.module_id = ?
                """;
        return jdbcTemplate.query(sql, teacherRowMapper, moduleId);
    }

    @Override
    public Optional<Teacher> getByPersonId(Integer personId) {
        if (personId == null) return Optional.empty();
        final String sql = """
                SELECT t.*, p.*
                FROM teacher t, person p
                WHERE t.person_id = p.id AND p.id = ?""";
        List<Teacher> teachers = jdbcTemplate.query(sql, teacherRowMapper, personId);
        return teachers.isEmpty() ? Optional.empty() : Optional.of(teachers.getFirst());
    }

    @Override
    public Optional<Integer> insert(Teacher teacher) {
        if ((teacher == null) || (teacher.getPerson() == null) || (teacher.getPerson().getId() == null)) return Optional.empty();
        final String sql = "INSERT INTO teacher(person_id, position, degree, work_experience) VALUES (?, ?, ?, ?)";

        jdbcTemplate.update(sql, teacher.getPerson().getId(), teacher.getPosition(), teacher.getDegree(), teacher.getWorkExperience());

        return Optional.of(teacher.getPerson().getId());
    }

    @Override
    public void update(Teacher teacher) {
        if ((teacher == null) || (teacher.getPerson() == null) || (teacher.getPerson().getId() == null)) return;
        final String sql = "UPDATE teacher SET position = ?, degree = ?, work_experience = ? WHERE person_id = ?";
        jdbcTemplate.update(sql, teacher.getPosition(), teacher.getDegree(), teacher.getWorkExperience(), teacher.getPerson().getId());
    }

    @Override
    public void delete(Integer personId) {
        if (personId == null) return;
        final String sql = "DELETE FROM teacher WHERE person_id = ?";
        jdbcTemplate.update(sql, personId);
    }
}
