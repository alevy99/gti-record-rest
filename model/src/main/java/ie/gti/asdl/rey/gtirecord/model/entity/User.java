package ie.gti.asdl.rey.gtirecord.model.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class User implements Serializable {

    private int id;

    private String username;

    private String password;

    private final List<Role> roles = new ArrayList<>();

    private Integer personId;

}
