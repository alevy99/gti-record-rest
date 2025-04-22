package ie.gti.asdl.rey.gtirecord.model.entity;

import ie.gti.asdl.rey.gtirecord.model.annotation.KeyField;
import ie.gti.asdl.rey.gtirecord.model.annotation.ShortDescriptionField;
import ie.gti.asdl.rey.gtirecord.model.validation.OnCreate;
import ie.gti.asdl.rey.gtirecord.model.validation.OnUpdate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class User {

    @KeyField
    @NotNull(groups = OnUpdate.class)
    private Integer id;

    @ShortDescriptionField
    @NotBlank
    private String username;

//    @NotBlank
    private String password;

    private final Set<Role> roles = new HashSet<>();

    private Integer personId;

}
