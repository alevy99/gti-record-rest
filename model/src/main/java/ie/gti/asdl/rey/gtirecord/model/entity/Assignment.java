package ie.gti.asdl.rey.gtirecord.model.entity;


import ie.gti.asdl.rey.gtirecord.model.annotation.DefaultIfNull;
import ie.gti.asdl.rey.gtirecord.model.annotation.KeyField;
import ie.gti.asdl.rey.gtirecord.model.annotation.ShortDescriptionField;
import ie.gti.asdl.rey.gtirecord.model.validation.OnUpdate;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Data
public class Assignment {

    @KeyField
    @NotNull(groups = OnUpdate.class)
    private Integer id;

    @NotNull
//    @Valid
    private GroupModule groupModule;

    @ShortDescriptionField
    @NotBlank(message = "Assignment name must be provided")
    private String name;

    // In %
    @DefaultIfNull("0")
    private Integer weighting;

    @DefaultIfNull("0")
    private Integer maxGrade;

    private LocalDateTime openDate;

    private LocalDateTime dueDate;

    public void setWeighting(Integer weighting) {
        this.weighting = Objects.requireNonNullElse(weighting, 0);
    }

    public void setMaxGrade(Integer maxGrade) {
        this.maxGrade = Objects.requireNonNullElse(maxGrade, 0);
    }
}
