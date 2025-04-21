package ie.gti.asdl.rey.gtirecord.core.dao.impl;

import ie.gti.asdl.rey.gtirecord.core.dao.StudentAssignmentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * @author Andrei Levchenko
 */
@Repository
public class StudentAssignmentDaoImpl implements StudentAssignmentDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public StudentAssignmentDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public void deleteByAssignmentId(Integer assignmentId) {
        if (assignmentId == null) return;
        final String sql = "DELETE FROM student_has_assignment WHERE assignment_id = ?";
        jdbcTemplate.update(sql, assignmentId);
    }
}
