package ie.gti.asdl.rey.gtirecord.core.service;

import ie.gti.asdl.rey.gtirecord.model.entity.Address;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service interface providing operations for retrieving {@link Address} data.
 */
@Service
public interface AddressService {

    /**
     * Retrieves the address associated with the person having the given ID.
     *
     * @param personId the ID of the person whose address should be retrieved
     * @return an {@link Optional} containing the {@link Address} if found,
     *         or an empty {@link Optional} if no address exists for the given person
     */
    Optional<Address> getByPersonId(int personId);
}
