package ie.gti.asdl.rey.gtirecord.model.entity;


import ie.gti.asdl.rey.gtirecord.model.annotation.KeyField;
import ie.gti.asdl.rey.gtirecord.model.annotation.ShortDescriptionField;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Assignment {

    @KeyField
    private Integer id;

    private GroupModule groupModule;

    @ShortDescriptionField
    private String name;

    private Double weighting;

    private LocalDateTime openDate;

    private LocalDateTime dueDate;

}
