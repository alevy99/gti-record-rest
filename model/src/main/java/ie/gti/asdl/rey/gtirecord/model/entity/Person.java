package ie.gti.asdl.rey.gtirecord.model.entity;

import ie.gti.asdl.rey.gtirecord.model.annotation.KeyField;
import ie.gti.asdl.rey.gtirecord.model.annotation.ShortDescriptionField;
import ie.gti.asdl.rey.gtirecord.model.annotation.ShortDescriptionFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class Person implements Serializable {

    @KeyField
    private Integer id;

    @ShortDescriptionField(order = 1, format = ShortDescriptionFormat.FIRST_LETTER)
    private String firstName;

    @ShortDescriptionField()
    private String lastName;

    private LocalDate dateOfBirth;

    private String ppsn;

    private String email;

    private String phoneNum;

    private Address address;

}
