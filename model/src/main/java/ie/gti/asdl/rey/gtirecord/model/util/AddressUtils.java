package ie.gti.asdl.rey.gtirecord.model.util;

import ie.gti.asdl.rey.gtirecord.model.entity.Address;

public class AddressUtils {

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
