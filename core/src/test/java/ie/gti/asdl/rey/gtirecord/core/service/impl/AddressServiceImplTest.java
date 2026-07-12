package ie.gti.asdl.rey.gtirecord.core.service.impl;

import ie.gti.asdl.rey.gtirecord.core.dao.AddressDao;
import ie.gti.asdl.rey.gtirecord.model.entity.Address;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for AddressServiceImpl.
 * All collaborators are mocked with Mockito; no Spring context is loaded.
 */
@ExtendWith(MockitoExtension.class)
class AddressServiceImplTest {

    @Mock
    private AddressDao addressDao;

    @InjectMocks
    private AddressServiceImpl addressService;

    // Get by person ID delegates to DAO and returns result.
    @Test
    void getByPersonId_delegatesToDao_andReturnsResult() {
        Address address = new Address(1, "Line 1", null, "Galway", "Clare", "Ireland", "H91 X1X1");
        when(addressDao.getByPersonId(1)).thenReturn(Optional.of(address));

        Optional<Address> result = addressService.getByPersonId(1);

        assertTrue(result.isPresent());
        assertEquals(address, result.get());
        verify(addressDao).getByPersonId(1);
    }

    // Get by person ID returns empty when DAO returns empty.
    @Test
    void getByPersonId_returnsEmpty_whenDaoReturnsEmpty() {
        when(addressDao.getByPersonId(99)).thenReturn(Optional.empty());

        Optional<Address> result = addressService.getByPersonId(99);

        assertTrue(result.isEmpty());
        verify(addressDao).getByPersonId(99);
    }
}
