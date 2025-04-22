package ie.gti.asdl.rey.gtirecord.core.dao.mapper;

import ie.gti.asdl.rey.gtirecord.model.entity.StudentAssignment;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Andrei Levchenko
 */
public class StudentAssignmentRowMapper  implements RowMapper<StudentAssignment> {

    private static final StudentRowMapper studentRowMapper = new StudentRowMapper();
    private static final AssignmentRowMapper assignmentRowMapper = new AssignmentRowMapper();

    @Override
    public StudentAssignment mapRow(@NotNull ResultSet rs, int rowNum) throws SQLException {
        ResultSetHelper helper = new ResultSetHelper(rs);

        StudentAssignment studentAssignment = new StudentAssignment();
        studentAssignment.setIsSubmitted(rs.getBoolean("is_submitted"));
        studentAssignment.setIsGraded(rs.getBoolean("is_graded"));
//        helper.setBooleanIfPresent("is_submitted", studentAssignment::setIsSubmitted);
//        helper.setBooleanIfPresent("is_graded", studentAssignment::setIsGraded);
        helper.setIntIfPresent("grade", studentAssignment::setGrade);

        studentAssignment.setStudent(studentRowMapper.mapRow(rs, rowNum));
        studentAssignment.setAssignment(assignmentRowMapper.mapRow(rs, rowNum));

        return studentAssignment;
    }
}
