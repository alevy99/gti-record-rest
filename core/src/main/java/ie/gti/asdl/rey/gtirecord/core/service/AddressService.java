package ie.gti.asdl.rey.gtirecord.core.service;

import ie.gti.asdl.rey.gtirecord.model.entity.Address;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface AddressService {

    Optional<Address> getByPersonId(int personId);
}
