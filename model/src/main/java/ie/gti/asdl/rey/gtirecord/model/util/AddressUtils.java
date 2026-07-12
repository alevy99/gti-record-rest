package ie.gti.asdl.rey.gtirecord.model.util;

import ie.gti.asdl.rey.gtirecord.model.entity.Address;

/**
 * Utility class providing helper operations related to {@link Address} entities.
 */
public class AddressUtils {

    /**
     * Checks whether the given address is considered empty — that is, it has
     * no associated person ID and all of its text fields (city, country,
     * county, Eircode, and address lines) are empty.
     *
     * @param address the address to check; may be {@code null}
     * @return {@code true} if {@code address} is {@code null} or has no
     *         person ID and all text fields are empty; {@code false} otherwise
     */
    public static boolean isAddressEmpty(Address address) {
        if (address == null) {
            return true;
        }
        return (address.getPersonId() == null)
                && address.getCity().isEmpty()
                && address.getCountry().isEmpty()
                && address.getCounty().isEmpty()
                && address.getEirCode().isEmpty()
                && address.getLine1().isEmpty()
                && address.getLine2().isEmpty();
    }

}