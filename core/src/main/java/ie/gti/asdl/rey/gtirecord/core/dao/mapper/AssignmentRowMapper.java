package ie.gti.asdl.rey.gtirecord.core.dao.mapper;

import ie.gti.asdl.rey.gtirecord.model.entity.Assignment;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Andrei Levchenko
 */
public class AssignmentRowMapper implements RowMapper<Assignment> {

    private static final GroupModuleRowMapper groupModuleRowMapper = new GroupModuleRowMapper();

    @NotNull
    @Override
    public Assignment mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
        ResultSetHelper helper = new ResultSetHelper(rs);

        Assignment assignment = new Assignment();
        helper.setIntIfPresent("assignment_id", assignment::setId);
        helper.setStringIfPresent("assignment_name", assignment::setName);
        helper.setIntIfPresent("weighting", assignment::setWeighting);
        helper.setIntIfPresent("max_grade", assignment::setMaxGrade);

        assignment.setGroupModule(groupModuleRowMapper.mapRow(rs, rowNum));

        return assignment;
    }
}