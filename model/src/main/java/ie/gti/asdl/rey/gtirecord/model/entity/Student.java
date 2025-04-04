package ie.gti.asdl.rey.gtirecord.model.entity;

import lombok.Data;

@Data
public class Student {
    
    private Person person;

    private String education;

    private Boolean onErasmus;

    private String emergencyContacts;

}
