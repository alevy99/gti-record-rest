package ie.gti.asdl.rey.gtirecord.core.dao.impl;

import ie.gti.asdl.rey.gtirecord.core.dao.UserDao;
import ie.gti.asdl.rey.gtirecord.core.dao.mapper.RoleRowMapper;
import ie.gti.asdl.rey.gtirecord.core.dao.mapper.UserRowMapper;
import ie.gti.asdl.rey.gtirecord.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.*;

@Repository
public class UserDaoImpl implements UserDao {

    private final JdbcTemplate jdbcTemplate;

    private final static UserRowMapper userMapper = new UserRowMapper();
    public final static RoleRowMapper roleMapper = new RoleRowMapper();

    private final static ResultSetExtractor<User> rsExtractor = new ResultSetExtractor<User>() {
        @Override
        public User extractData(ResultSet rs) throws SQLException, DataAccessException {
            User user = null;
            int row = 0;
            while (rs.next()) {
                if (user == null) {
                    user = userMapper.mapRow(rs, row);
                }
                assert user != null;
                user.getRoles().add(roleMapper.mapRow(rs, row));
                row++;
            }
            return user;
        }
    };

    @Autowired
    public UserDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public Optional<User> getById(int id) {
        final String sql =
                """
                        SELECT u.id, u.person_id, u.username, u.password, r.id as role_id, r.name as role_name
                        FROM user u
                        LEFT OUTER JOIN users_roles ur ON u.id = ur.user_id\s
                        LEFT OUTER JOIN role r ON r.id = ur.role_id
                        WHERE u.id=?""";
        return Optional.ofNullable(jdbcTemplate.query(sql, rsExtractor, id));
    }

    @Override
    public Optional<User> getByUsername(String username) {
        final String sql =
                """
                        SELECT u.id, u.person_id, u.username, u.password, r.id as role_id, r.name as role_name
                        FROM user u
                        LEFT OUTER JOIN users_roles ur ON u.id = ur.user_id\s
                        LEFT OUTER JOIN role r ON r.id = ur.role_id
                        WHERE u.username=?""";
        return Optional.ofNullable(jdbcTemplate.query(sql, rsExtractor, username));
    }

    @Override
    public List<User> getAll() {
        final String sql =
                """
                        SELECT u.id, u.person_id, u.username, u.password, r.id as role_id, r.name as role_name
                        FROM user u
                        LEFT OUTER JOIN users_roles ur ON u.id = ur.user_id
                        LEFT OUTER JOIN role r ON r.id = ur.role_id""";
        return jdbcTemplate.query(sql, new ResultSetExtractor<List<User>>() {

            @Override
            public List<User> extractData(@NonNull ResultSet rs) throws SQLException, DataAccessException {
                Map<Integer, User> userMap = new HashMap<>();

                int row = 0;
                while (rs.next()) {
                    int userID = rs.getInt("id");
                    User user = userMap.get(userID);
                    if (user == null)  {
                        user = userMapper.mapRow(rs, row);
                        userMap.put(userID, user);
                    }
                    assert user != null;
                    user.getRoles().add(roleMapper.mapRow(rs, row));
                    row++;
                }
                return new ArrayList<>(userMap.values());
            }
        });
    }

    @Override
    public Optional<Integer> insert(User user) {
        final String sql = "INSERT INTO user (username, password) VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            @NonNull
            public PreparedStatement createPreparedStatement(@NonNull Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, user.getUsername());
                ps.setString(2, user.getPassword());
                return ps;
            }
        }, keyHolder);

        if (keyHolder.getKey() == null) {
            return Optional.empty();
        } else {
            return Optional.of(keyHolder.getKey().intValue());
        }
    }

    @Override
    public void delete(int id) {
        final String sql = "DELETE FROM user WHERE user.id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void deleteUsersById(List<Integer> ids) {
        final String sql =
                "DELETE FROM user\n" +
                "WHERE user.id = ?";
        jdbcTemplate.batchUpdate(sql, ids, ids.size(), new ParameterizedPreparedStatementSetter<Integer>() {
            @Override
            public void setValues(@NonNull PreparedStatement ps, @NonNull Integer id) throws SQLException {
                ps.setInt(1, id);
            }
        });
    }

    @Override
    public void update(User user) {
        final String sql = "UPDATE user SET person_id = ?, username = ?, password = ? WHERE id = ?";
        jdbcTemplate.update(sql, user.getPersonId(), user.getUsername(), user.getPassword(), user.getId());
    }

}
