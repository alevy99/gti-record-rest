package ie.gti.asdl.rey.gtirecord.core.dao.impl;

import ie.gti.asdl.rey.gtirecord.core.dao.UserDao;
import ie.gti.asdl.rey.gtirecord.core.dao.mapper.RoleRowMapper;
import ie.gti.asdl.rey.gtirecord.core.dao.mapper.UserRowMapper;
import ie.gti.asdl.rey.gtirecord.core.service.ValidationService;
import ie.gti.asdl.rey.gtirecord.model.entity.User;
import ie.gti.asdl.rey.gtirecord.model.validation.OnCreate;
import ie.gti.asdl.rey.gtirecord.model.validation.OnUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import jakarta.validation.Validator;

import java.sql.*;
import java.util.*;

@Repository
public class UserDaoImpl implements UserDao {

    private final JdbcTemplate jdbcTemplate;
    private final ValidationService validationService;


    private final static UserRowMapper userMapper = new UserRowMapper();
    public final static RoleRowMapper roleMapper = new RoleRowMapper();

    private final static ResultSetExtractor<User> rsExtractor = rs -> {
        User user = null;
        int row = 0;
        while (rs.next()) {
            if (user == null) {
                user = userMapper.mapRow(rs, row);
            }
            var role = roleMapper.mapRow(rs, row);
            if (role.isValid()) {
                user.getRoles().add(role);
            }
            row++;
        }
        return user;
    };

    @Autowired
    public UserDaoImpl(JdbcTemplate jdbcTemplate, ValidationService validationService) {
        this.jdbcTemplate = jdbcTemplate;
        this.validationService = validationService;
    }

    @Override
    public Optional<User> getById(Integer id) {
        if (id == null) return Optional.empty();
        final String sql =
                """
                        SELECT u.id as user_id, u.person_id, u.username, u.password, r.id as role_id, r.name as role_name
                        FROM user u
                        LEFT OUTER JOIN users_roles ur ON u.id = ur.user_id
                        LEFT OUTER JOIN role r ON r.id = ur.role_id
                        WHERE u.id=?""";
        return Optional.ofNullable(jdbcTemplate.query(sql, rsExtractor, id));
    }

    @Override
    public Optional<User> getByUsername(String username) {
        final String sql =
                """
                        SELECT u.id as user_id, u.person_id, u.username, u.password, r.id as role_id, r.name as role_name
                        FROM user u
                        LEFT OUTER JOIN users_roles ur ON u.id = ur.user_id\s
                        LEFT OUTER JOIN role r ON r.id = ur.role_id
                        WHERE u.username=?""";
        return Optional.ofNullable(jdbcTemplate.query(sql, rsExtractor, username));
    }

    @Override
    public Optional<User> getByPersonId(Integer personId) {
        if (personId == null) return Optional.empty();
        final String sql =
                """
                        SELECT u.id as user_id, u.person_id, u.username, u.password, r.id as role_id, r.name as role_name
                        FROM user u
                        LEFT OUTER JOIN users_roles ur ON u.id = ur.user_id\s
                        LEFT OUTER JOIN role r ON r.id = ur.role_id
                        WHERE u.person_id=?""";
        return Optional.ofNullable(jdbcTemplate.query(sql, rsExtractor, personId));
    }

    @Override
    public List<User> getAll() {
        final String sql =
                """
                        SELECT u.id as user_id, u.person_id, u.username, u.password, r.id as role_id, r.name as role_name
                        FROM user u
                        LEFT OUTER JOIN users_roles ur ON u.id = ur.user_id
                        LEFT OUTER JOIN role r ON r.id = ur.role_id""";
        return jdbcTemplate.query(sql, rs -> {
            Map<Integer, User> userMap = new HashMap<>();

            int row = 0;
            while (rs.next()) {
                int userId = rs.getInt("user_id");
                User user = userMap.get(userId);
                if (user == null) {
                    user = userMapper.mapRow(rs, row);
                    userMap.put(userId, user);
                }
                var role = roleMapper.mapRow(rs, row);
                if (role.isValid()) {
                    user.getRoles().add(role);
                }
                row++;
            }
            return new ArrayList<>(userMap.values());
        });
    }

    @Override
    public Optional<Integer> insert(User user) {
        if (!validationService.validate(user, OnCreate.class)) return Optional.empty();

        final String sql = "INSERT INTO user (person_id, username, password) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            @NonNull
            public PreparedStatement createPreparedStatement(@NonNull Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                if (user.getPersonId() != null) {
                    ps.setInt(1, user.getPersonId());
                } else {
                    ps.setNull(1, Types.INTEGER);
                }
                ps.setString(2, user.getUsername());
                ps.setString(3, user.getPassword());
                return ps;
            }
        }, keyHolder);

        if (keyHolder.getKey() == null) {
            return Optional.empty();
        } else {
            user.setId(keyHolder.getKey().intValue());
            return Optional.of(user.getId());
        }
    }

    @Override
    public void delete(int id) {
        final String sql = "DELETE FROM user WHERE user.id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void deleteUsersById(List<Integer> ids) {
        final String sql = "DELETE FROM user WHERE user.id = ?";
        jdbcTemplate.batchUpdate(sql, ids, ids.size(), (ps, id) -> ps.setInt(1, id));
    }

    @Override
    public void update(User user) {
        final String sql = "UPDATE user SET person_id = ?, username = ?, password = ? WHERE id = ?";
        jdbcTemplate.update(sql, user.getPersonId(), user.getUsername(), user.getPassword(), user.getId());
    }

}
