package ie.gti.asdl.rey.gtirecord.model.entity;


import jakarta.validation.Valid;
import lombok.Data;

@Data
public class TeacherModule {

    @Valid
    private Teacher teacher;

    @Valid
    private Module module;

}
