package ie.gti.asdl.rey.gtirecord.model.entity;


import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Data
public class TeacherModule {

    @Valid
    private Teacher teacher;

    @Valid
    private Module module;

}
