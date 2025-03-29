package ie.gti.asdl.rey.gtirecord.model.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class Person implements Serializable {

    private Integer id;

    private String firstName;

    private String lastName;

    private LocalDate dateOfBirth;

    private String ppsn;

    private String email;

    private String phoneNum;

    private Address address;

}
