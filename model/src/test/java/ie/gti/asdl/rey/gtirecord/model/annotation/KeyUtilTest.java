package ie.gti.asdl.rey.gtirecord.model.annotation;

import ie.gti.asdl.rey.gtirecord.model.entity.Address;
import ie.gti.asdl.rey.gtirecord.model.entity.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class KeyUtilTest {

    // Test-only fixture used to exercise the recursive branch of KeyUtil:
    // the @KeyField here is not an Integer, so KeyUtil must recurse into it.
    private static class Wrapper {
        @KeyField
        private final Address address;

        private Wrapper(Address address) {
            this.address = address;
        }
    }

    @Test
    void hasKey_returnsTrue_whenDirectIntegerKeyIsSetAndNonZero() {
        Address address = new Address(1, "", "", "", "", "", "");

        assertTrue(KeyUtil.hasKey(address));
    }

    @Test
    void hasKey_returnsFalse_whenDirectIntegerKeyIsNull() {
        Address address = new Address(null, "", "", "", "", "", "");

        assertFalse(KeyUtil.hasKey(address));
    }

    @Test
    void hasKey_returnsFalse_whenDirectIntegerKeyIsZero() {
        Address address = new Address(0, "", "", "", "", "", "");

        assertFalse(KeyUtil.hasKey(address));
    }

    @Test
    void hasKey_returnsFalse_whenNoKeyFieldIsFound() {
        Object plainObject = new Object();

        assertFalse(KeyUtil.hasKey(plainObject));
    }

    @Test
    void hasKey_recursesIntoNestedKeyField() {
        Address address = new Address(42, "", "", "", "", "", "");
        Wrapper wrapper = new Wrapper(address);

        assertTrue(KeyUtil.hasKey(wrapper));
    }

    @Test
    void getKey_returnsValue_forDirectIntegerKey() {
        User user = new User(7, "jdoe", "pw", 1);

        assertEquals(7, KeyUtil.getKey(user));
    }

    @Test
    void getKey_returnsNull_whenNoKeyFieldIsFound() {
        assertNull(KeyUtil.getKey(new Object()));
    }

    @Test
    void getKey_recursesIntoNestedKeyField() {
        Address address = new Address(99, "", "", "", "", "", "");
        Wrapper wrapper = new Wrapper(address);

        assertEquals(99, KeyUtil.getKey(wrapper));
    }

    @Test
    void setKey_updatesDirectIntegerKey() {
        Address address = new Address(null, "", "", "", "", "", "");

        KeyUtil.setKey(address, 5);

        assertEquals(5, address.getPersonId());
    }

    @Test
    void setKey_throwsRuntimeException_whenNoKeyFieldIsFound() {
        Object plainObject = new Object();

        assertThrows(RuntimeException.class, () -> KeyUtil.setKey(plainObject, 1));
    }
}
