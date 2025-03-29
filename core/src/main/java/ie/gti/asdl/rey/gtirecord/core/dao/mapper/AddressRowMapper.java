package ie.gti.asdl.rey.gtirecord.core.dao.mapper;

import ie.gti.asdl.rey.gtirecord.model.entity.Address;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

//public class AddressRowMapper implements RowMapper<Address> {
//
//    @Override
//    public Address mapRow(ResultSet rs, int rowNum) throws SQLException {
//        Address address = new Address();
//
//        address.setLine1(rs.getString("line1"));
//        address.setLine2(rs.getString("line2"));
//        address.setCity(rs.getString("city"));
//        address.setCountry(rs.getString("country"));
//        address.setCounty(rs.getString("county"));
//        address.setEirCode(rs.getString("eircode"));
//
//        return address;
//    }
//
//}
