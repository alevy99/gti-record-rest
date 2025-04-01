package ie.gti.asdl.rey.gtirecord.model.entity;

import ie.gti.asdl.rey.gtirecord.model.annotation.KeyField;
import ie.gti.asdl.rey.gtirecord.model.annotation.ShortDescriptionField;
import lombok.Data;

@Data
public class Department {

    @KeyField
    private Integer id;

    @ShortDescriptionField
    private String name;

}
