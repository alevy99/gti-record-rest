package ie.gti.asdl.rey.gtirecord.core.dao;

import ie.gti.asdl.rey.gtirecord.model.entity.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class AddressDaoImpl implements AddressDao {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Address> addressRowMapper = new BeanPropertyRowMapper<Address>();

    @Autowired
    public AddressDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Address> getByPersonId(int personId) {
        final String sql = "SELECT * FROM address WHERE person_id = ?";
        return Optional.ofNullable(jdbcTemplate.queryForObject(sql, addressRowMapper, personId));
    }

    @Override
    public Optional<Integer> insert(Address address) {
        return Optional.empty();
    }

    @Override
    public void update(Address address) {

    }

    @Override
    public void delete(int id) {

    }

}
