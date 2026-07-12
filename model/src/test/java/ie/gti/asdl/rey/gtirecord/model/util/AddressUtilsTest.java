package ie.gti.asdl.rey.gtirecord.model.util;

import ie.gti.asdl.rey.gtirecord.model.entity.Address;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AddressUtilsTest {

    @Test
    void isAddressEmpty_returnsTrue_whenAddressIsNull() {
        assertTrue(AddressUtils.isAddressEmpty(null));
    }

    @Test
    void isAddressEmpty_returnsTrue_whenAllFieldsAreEmpty() {
        Address address = new Address(null, "", "", "", "", "", "");

        assertTrue(AddressUtils.isAddressEmpty(address));
    }

    @Test
    void isAddressEmpty_returnsFalse_whenPersonIdIsSet() {
        Address address = new Address(1, "", "", "", "", "", "");

        assertFalse(AddressUtils.isAddressEmpty(address));
    }

    @Test
    void isAddressEmpty_returnsFalse_whenAnyTextFieldIsNotEmpty() {
        Address address = new Address(null, "", "", "Galway", "", "", "");

        assertFalse(AddressUtils.isAddressEmpty(address));
    }

    @Test
    void isAddressEmpty_returnsFalse_whenAllFieldsArePopulated() {
        Address address = new Address(1, "Line 1", "Line 2", "Galway", "Galway", "Ireland", "H91 XXXX");

        assertFalse(AddressUtils.isAddressEmpty(address));
    }
}
