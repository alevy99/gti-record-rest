package ie.gti.asdl.rey.gtirecord.model.entity;

import ie.gti.asdl.rey.gtirecord.model.annotation.KeyField;
import lombok.Data;

@Data
public class Student {

    @KeyField
    private Person person;

    private String education;

    private Boolean onErasmus;

    private String emergencyContacts;

}
