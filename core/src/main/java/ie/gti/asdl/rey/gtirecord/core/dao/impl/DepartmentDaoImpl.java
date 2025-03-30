package ie.gti.asdl.rey.gtirecord.core.dao.impl;

import ie.gti.asdl.rey.gtirecord.core.dao.DepartmentDao;
import ie.gti.asdl.rey.gtirecord.model.entity.Address;
import ie.gti.asdl.rey.gtirecord.model.entity.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class DepartmentDaoImpl implements DepartmentDao {

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<Department> departmentRowMapper = new BeanPropertyRowMapper<Department>(Department.class);

    @Autowired
    public DepartmentDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Department> getById(int id) {
        final String sql = "SELECT * FROM department WHERE id = ?";
        List<Department> departments = jdbcTemplate.query(sql, departmentRowMapper, id);
        return departments.isEmpty() ? Optional.empty() : Optional.of(departments.getFirst());
    }

    @Override
    public List<Department> getAll() {
        final String sql = "SELECT * FROM department";
        return jdbcTemplate.query(sql, departmentRowMapper);
    }

    @Override
    public Optional<Integer> insert(Department department) {
        final String sql = "INSERT INTO department (name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            @NonNull
            public PreparedStatement createPreparedStatement(@NonNull Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, department.getName());
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
    public void update(Department department) {
        final String sql = "UPDATE department SET name = ? WHERE id = ?";
        jdbcTemplate.update(sql, department.getName(), department.getId());
    }

    @Override
    public void delete(int id) {
        final String sql = "DELETE FROM department WHERE department.id = ?";
        jdbcTemplate.update(sql, id);
    }
}
