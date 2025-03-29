package ie.gti.asdl.rey.gtirecord.core.dao;

import ie.gti.asdl.rey.gtirecord.model.entity.Address;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressDao {

    Optional<Address> getByPersonId(int personId);

    Optional<Integer> insert(Address address);

    void update(Address address);

    void delete(int id);
}
