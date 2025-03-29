package ie.gti.asdl.rey.gtirecord.model.entity;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Assignment {

    private int id;

    private GroupModule groupModule;

    private String name;

    private Double weighting;

    private LocalDateTime openDate;

    private LocalDateTime dueDate;

}
