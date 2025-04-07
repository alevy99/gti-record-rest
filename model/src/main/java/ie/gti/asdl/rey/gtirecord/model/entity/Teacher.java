package ie.gti.asdl.rey.gtirecord.model.entity;

import ie.gti.asdl.rey.gtirecord.model.annotation.KeyField;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Teacher {

    @KeyField
    @NotNull
    @Valid
    private Person person;

    private String position;

    private String degree;

    private Integer workExperience;

}
