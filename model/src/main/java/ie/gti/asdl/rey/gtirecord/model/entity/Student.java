package ie.gti.asdl.rey.gtirecord.model.entity;

import ie.gti.asdl.rey.gtirecord.model.annotation.KeyField;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Student {

    @KeyField
    @NotNull
    @Valid
    private Person person;

    private Group group;

    private String education;

    private Boolean onErasmus;

    private String emergencyContacts;

}
