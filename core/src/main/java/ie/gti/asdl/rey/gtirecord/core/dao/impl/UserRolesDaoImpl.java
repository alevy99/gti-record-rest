package ie.gti.asdl.rey.gtirecord.core.dao.impl;

import ie.gti.asdl.rey.gtirecord.core.dao.UserRolesDao;
import ie.gti.asdl.rey.gtirecord.model.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
public class UserRolesDaoImpl implements UserRolesDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserRolesDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void insert(int userId, List<Role> roles) {
        String sql = "INSERT INTO users_roles (user_id, role_id) VALUES (?, ?)";

        jdbcTemplate.batchUpdate(sql, roles, roles.size(), new ParameterizedPreparedStatementSetter<Role>() {
            @Override
            public void setValues(PreparedStatement ps, Role role) throws SQLException {
                ps.setInt(1, userId);
                ps.setInt(2, role.getId());
            }
        });
    }

    @Override
    public void deleteByUserId(int userId, List<Role> roles) {
        final String sql = "DELETE FROM users_roles WHERE user_id = ? and role_id = ?";
        jdbcTemplate.batchUpdate(sql, roles, roles.size(), new ParameterizedPreparedStatementSetter<Role>() {
            @Override
            public void setValues(PreparedStatement ps, Role role) throws SQLException {
                ps.setInt(1, userId);
                ps.setInt(2, role.getId());
            }
        });
    }

    @Override
    public void deleteByUserId(int userId) {
        final String sql = "DELETE FROM users_roles WHERE user_id = ?";
        jdbcTemplate.update(sql, userId);
    }

    @Override
    public void delete(Integer userId, Integer roleId) {
        if (userId == null || roleId == null) return;
        final String sql = "DELETE FROM users_roles WHERE user_id = ? and role_id = ?";
        jdbcTemplate.update(sql, userId, roleId);
    }

}
