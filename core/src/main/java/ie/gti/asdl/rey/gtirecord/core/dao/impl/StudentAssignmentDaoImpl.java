package ie.gti.asdl.rey.gtirecord.core.dao.impl;

import ie.gti.asdl.rey.gtirecord.core.dao.StudentAssignmentDao;
import ie.gti.asdl.rey.gtirecord.core.dao.mapper.StudentAssignmentRowMapper;
import ie.gti.asdl.rey.gtirecord.model.entity.StudentAssignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Andrei Levchenko
 */
@Repository
public class StudentAssignmentDaoImpl implements StudentAssignmentDao {

    private final JdbcTemplate jdbcTemplate;
    private static final StudentAssignmentRowMapper studentAssignmentRowMapper = new StudentAssignmentRowMapper();

    @Autowired
    public StudentAssignmentDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<StudentAssignment> getByAssignmentId(Integer assignmentId) {
        if (assignmentId == null) return new ArrayList<>();
        final String sql = """
                SELECT *
                FROM student_has_assignment sa, student s, person p
                WHERE sa.student_person_id=s.person_id and s.person_id=p.id and sa.assignment_id = ?""";
        return jdbcTemplate.query(sql, studentAssignmentRowMapper, assignmentId);
    }

    @Override
    public void insert(StudentAssignment studentAssignment) {
        if (studentAssignment == null) return;
        final String sql = """
                INSERT INTO student_has_assignment (student_person_id, assignment_id, is_submitted, is_graded, grade)
                VALUES (?, ?, ?, ?, ?)""";
        jdbcTemplate.update(sql, studentAssignment.getStudent().getPerson().getId(),
                studentAssignment.getAssignment().getId(),
                studentAssignment.getIsSubmitted(),
                studentAssignment.getIsGraded(),
                studentAssignment.getGrade());
    }

    @Override
    public void deleteByAssignmentId(Integer assignmentId) {
        if (assignmentId == null) return;
        final String sql = "DELETE FROM student_has_assignment WHERE assignment_id = ?";
        jdbcTemplate.update(sql, assignmentId);
    }
}
