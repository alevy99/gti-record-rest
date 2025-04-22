package ie.gti.asdl.rey.gtirecord.model.entity;

import ie.gti.asdl.rey.gtirecord.model.annotation.KeyField;
import ie.gti.asdl.rey.gtirecord.model.annotation.ShortDescriptionField;
import ie.gti.asdl.rey.gtirecord.model.validation.OnUpdate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Course {

    @KeyField
    @EqualsAndHashCode.Include
    @NotNull(groups = OnUpdate.class)
    private Integer id;

    @ShortDescriptionField
    @NotBlank(message = "Course name must be provided")
    private String name;
    
    private String code;

    @NotNull(message = "Course type must be provided")
//    @Valid
    private CourseType courseType;

    @NotNull(message = "Department must be provided")
//    @Valid
    private Department department;

    @NotNull(message = "QQI Level must be provided")
    private QQILevel qqiLevel;

}
