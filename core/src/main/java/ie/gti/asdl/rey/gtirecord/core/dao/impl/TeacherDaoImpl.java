package ie.gti.asdl.rey.gtirecord.core.dao.impl;

import ie.gti.asdl.rey.gtirecord.core.dao.TeacherDao;
import ie.gti.asdl.rey.gtirecord.model.entity.Person;
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
                SELECT t.person_id, t.position, t.degree, t.work_experience, p.*
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
    public List<Teacher> getAll() {
        final String sql = """
                SELECT *
                FROM teacher t, person p
                WHERE t.person_id = p.id;
                """;
        return jdbcTemplate.query(sql, (rs) -> {
            List<Teacher> teachers = new ArrayList<>();

            while (rs.next()) {
                Teacher teacher = teacherRowMapper.mapRow(rs, 0);
                Person person = personRowMapper.mapRow(rs, 0);
                if (teacher != null) {
                    teacher.setPerson(person);
                }
                teachers.add(teacher);
            }
            return teachers;
        });
    }

    @Override
    public Optional<Integer> insert(Teacher teacher) {
        if ((teacher == null) || (teacher.getPerson() == null) || (teacher.getPerson().getId() == null)) return Optional.empty();
        final String sql = "INSERT INTO teacher(person_id, position, degree, work_experience) VALUES (?, ?, ?, ?)";

        jdbcTemplate.update(sql, teacher.getPerson().getId(), teacher.getPosition(), teacher.getDegree(), teacher.getWorkExperience());

        return Optional.of(teacher.getPerson().getId());

//        jdbcTemplate.update(new PreparedStatementCreator() {
//            @Override
//            @NonNull
//            public PreparedStatement createPreparedStatement(@NonNull Connection connection) throws SQLException {
//                PreparedStatement ps = connection.prepareStatement(sql);
//                ps.setInt(1, teacher.getPerson().getId());
//                ps.setString(2, teacher.getPosition());
//                ps.setString(3, teacher.getDegree());
//                if (teacher.getWorkExperience() == null) {
//                    ps.setNull(4, Types.INTEGER);
//                } else {
//                    ps.setInt(4, teacher.getWorkExperience());
//                }
//                return ps;
//            }
//        });

//        return Optional.of(teacher.getPerson().getId());


//        final String sql = "INSERT INTO teacher(person_id, position, degree, work_experience) VALUES (?, ?, ?, ?)";
//        return Optional.ofNullable(jdbcTemplate.update(sql, teacher.getPerson().getId(), teacher.getPosition(), teacher.getDegree(), teacher.getWorkExperience()));
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
