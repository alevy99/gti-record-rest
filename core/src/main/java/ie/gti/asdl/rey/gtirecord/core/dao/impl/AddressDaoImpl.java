package ie.gti.asdl.rey.gtirecord.core.dao.impl;

import ie.gti.asdl.rey.gtirecord.core.dao.AddressDao;
import ie.gti.asdl.rey.gtirecord.core.service.ValidationService;
import ie.gti.asdl.rey.gtirecord.model.entity.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.*;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;
import java.util.Optional;

@Repository
public class AddressDaoImpl implements AddressDao {

    private final JdbcTemplate jdbcTemplate;
    private final ValidationService validationService;

    private final RowMapper<Address> addressRowMapper = new BeanPropertyRowMapper<>(Address.class);

    @Autowired
    public AddressDaoImpl(JdbcTemplate jdbcTemplate, ValidationService validationService) {
        this.jdbcTemplate = jdbcTemplate;
        this.validationService = validationService;
    }

    @Override
    public Optional<Address> getByPersonId(Integer personId) {
        if (personId == null) return Optional.empty();
        final String sql = "SELECT * FROM address WHERE person_id = ?";
        List<Address> addresses = jdbcTemplate.query(sql, addressRowMapper, personId);
        return addresses.isEmpty() ? Optional.empty() : Optional.of(addresses.getFirst());
    }

    @Override
    public void insert(Address address) {
        final String sql = """
                INSERT INTO address (person_id, line1, line2, eircode, city, county, country)
                VALUES (?, ?, ?, ?, ?, ?, ?);
                """;

        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            @NonNull
            public PreparedStatement createPreparedStatement(@NonNull Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setInt(1, address.getPersonId());
                ps.setString(2, address.getLine1());
                ps.setString(3, address.getLine2());
                ps.setString(4, address.getEirCode());
                ps.setString(5, address.getCity());
                ps.setString(6, address.getCounty());
                ps.setString(7, address.getCountry());
                return ps;
            }
        });
    }

    @Override
    public void update(Address address) {
        final String sql = """
                UPDATE address
                SET line1 = ?, line2 = ?, eircode = ?, city = ?, county = ?, country = ?
                WHERE person_id = ?;
                """;
        jdbcTemplate.update(sql, address.getLine1(), address.getLine2(), address.getEirCode(),
                address.getCity(), address.getCounty(), address.getCountry(), address.getPersonId());
    }

    @Override
    public void deleteByPersonId(int personId) {
        final String sql = "DELETE FROM address WHERE person_id = ?";
        jdbcTemplate.update(sql, personId);
    }

}
