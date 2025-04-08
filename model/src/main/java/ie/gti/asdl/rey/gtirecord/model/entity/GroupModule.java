package ie.gti.asdl.rey.gtirecord.model.entity;

import ie.gti.asdl.rey.gtirecord.model.annotation.KeyField;
import ie.gti.asdl.rey.gtirecord.model.validation.OnUpdate;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GroupModule {

    @KeyField
    @NotNull(groups = OnUpdate.class)
    private Integer id;

    @NotNull(message = "Group must be provided")
    @Valid
    private Group group;

    @NotNull(message = "Module must be provided")
    @Valid
    private Module module;

    @Valid
    private Teacher teacher;
}
