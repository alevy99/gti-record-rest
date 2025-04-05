package ie.gti.asdl.rey.gtirecord.model.entity;

import ie.gti.asdl.rey.gtirecord.model.annotation.KeyField;
import lombok.Data;

@Data
public class Teacher {

    @KeyField
    private Person person;

    private String position;

    private String degree;

    private Integer workExperience;

}
