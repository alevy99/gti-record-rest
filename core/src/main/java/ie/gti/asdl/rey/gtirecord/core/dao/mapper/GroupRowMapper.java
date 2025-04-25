package ie.gti.asdl.rey.gtirecord.core.dao.mapper;

import ie.gti.asdl.rey.gtirecord.model.annotation.InstanceFactory;
import ie.gti.asdl.rey.gtirecord.model.entity.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Andrei Levchenko
 */
public class GroupRowMapper implements RowMapper<Group> {

    private static final CourseRowMapper courseRowMapper = new CourseRowMapper();

    @NotNull
    @Override
    public Group mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
        ResultSetHelper helper = new ResultSetHelper(rs);

        Group group = InstanceFactory.create(Group.class);
        helper.setIntIfPresent("group_id", group::setId);
        helper.setStringIfPresent("group_name", group::setName);
        helper.setStringIfPresent("group_code", group::setCode);

        group.setCourse(courseRowMapper.mapRow(rs, rowNum));

        return group;
    }
}