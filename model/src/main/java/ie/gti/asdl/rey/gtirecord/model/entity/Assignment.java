package ie.gti.asdl.rey.gtirecord.model.entity;


import ie.gti.asdl.rey.gtirecord.model.annotation.KeyField;
import ie.gti.asdl.rey.gtirecord.model.annotation.ShortDescriptionField;
import ie.gti.asdl.rey.gtirecord.model.validation.OnUpdate;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Assignment {

    @KeyField
    @NotNull(groups = OnUpdate.class)
    private Integer id;

    @NotNull
    @Valid
    private GroupModule groupModule;

    @ShortDescriptionField
    @NotBlank(message = "Assignment name must be provided")
    private String name;

    private Double weighting;

    private LocalDateTime openDate;

    private LocalDateTime dueDate;

}
