package ie.gti.asdl.rey.gtirecord.core.dao.mapper;

import ie.gti.asdl.rey.gtirecord.model.entity.*;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Andrei Levchenko
 */
public class GroupRowMapper implements RowMapper<Group> {

    @Override
    public Group mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
        ResultSetHelper helper = new ResultSetHelper(rs);

        Group group = new Group();
        helper.setIntIfPresent("group_id", group::setId);
        helper.setStringIfPresent("group_name", group::setName);
        helper.setStringIfPresent("group_code", group::setCode);

        return group;
    }
}