package ie.gti.asdl.rey.gtirecord.core.dao.mapper;

import ie.gti.asdl.rey.gtirecord.model.entity.Role;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RoleRowMapper implements RowMapper<Role> {

    @NotNull
    @Override
    public Role mapRow(ResultSet rs, int rowNum) throws SQLException {
        Role role = new Role();
        role.setId(rs.getInt("role_id"));
        Integer roleId = rs.getInt("role_id");
        if (rs.wasNull()) {
            roleId = null;
        }
        role.setId(roleId);
        role.setName(rs.getString("role_name"));
        return role;
    }
}
