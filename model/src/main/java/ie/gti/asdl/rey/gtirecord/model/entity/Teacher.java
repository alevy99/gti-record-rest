package ie.gti.asdl.rey.gtirecord.model.entity;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Teacher {
    
    private Person person;

    private String position;

    private String degree;

    private Integer workExperience;

}
