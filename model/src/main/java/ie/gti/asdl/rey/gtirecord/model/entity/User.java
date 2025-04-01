package ie.gti.asdl.rey.gtirecord.model.entity;

import ie.gti.asdl.rey.gtirecord.model.annotation.KeyField;
import ie.gti.asdl.rey.gtirecord.model.annotation.ShortDescriptionField;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class User implements Serializable {

    @KeyField
    private Integer id;

    @ShortDescriptionField
    private String username;

    private String password;

    private final List<Role> roles = new ArrayList<>();

    private Integer personId;

}
