package ie.gti.asdl.rey.gtirecord.core.dao.impl;

import ie.gti.asdl.rey.gtirecord.core.dao.StudentAssignmentDao;
import ie.gti.asdl.rey.gtirecord.core.dao.mapper.StudentAssignmentRowMapper;
import ie.gti.asdl.rey.gtirecord.core.service.ValidationService;
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
    private final ValidationService validationService;

    private static final StudentAssignmentRowMapper studentAssignmentRowMapper = new StudentAssignmentRowMapper();

    @Autowired
    public StudentAssignmentDaoImpl(JdbcTemplate jdbcTemplate, ValidationService validationService) {
        this.jdbcTemplate = jdbcTemplate;
        this.validationService = validationService;
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
    public List<StudentAssignment> getByStudentPersonId(Integer studentPersonId) {
        if (studentPersonId == null) return new ArrayList<>();
        final String sql = """
                SELECT sa.*, 
                       a.id as assignment_id, a.name as assignment_name, a.weighting, a.max_grade, a.group_module_id
                FROM student_has_assignment sa, assignment a
                WHERE sa.assignment_id = a.id and sa.student_person_id = ?""";
        return jdbcTemplate.query(sql, studentAssignmentRowMapper, studentPersonId);
    }

    @Override
    public List<StudentAssignment> getByStudentPersonIdAndModuleId(Integer studentPersonId, Integer moduleId) {
        if (studentPersonId == null || moduleId == null) return new ArrayList<>();
        final String sql = """
                SELECT a.id as assignment_id, a.name as assignment_name, a.weighting, a.max_grade, a.group_module_id,
                       m.id as module_id, m.name as module_name, m.code as module_code,
                       gm.*, sa.*
                FROM student_has_assignment sa, assignment a, group_has_module gm, module m
                WHERE sa.assignment_id = a.id
                  and a.group_module_id = gm.id
                  and gm.module_id = m.id
                  and sa.student_person_id = ?
                  and m.id = ?""";
        return jdbcTemplate.query(sql, studentAssignmentRowMapper, studentPersonId, moduleId);
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
    public void update(StudentAssignment studentAssignment) {
        if (studentAssignment == null) return;
        final String sql = """
                UPDATE student_has_assignment SET is_submitted = ?, is_graded = ?, grade = ?
                WHERE student_person_id = ? and assignment_id = ?""";
        jdbcTemplate.update(sql, studentAssignment.getIsSubmitted(),
                studentAssignment.getIsGraded(),
                studentAssignment.getGrade(),
                studentAssignment.getStudent().getPerson().getId(),
                studentAssignment.getAssignment().getId());
    }

    @Override
    public void deleteByAssignmentId(Integer assignmentId) {
        if (assignmentId == null) return;
        final String sql = "DELETE FROM student_has_assignment WHERE assignment_id = ?";
        jdbcTemplate.update(sql, assignmentId);
    }
}
