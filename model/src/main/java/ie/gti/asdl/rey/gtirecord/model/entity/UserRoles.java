package ie.gti.asdl.rey.gtirecord.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserRoles {

    private User user;
    private List<Role> roles;

}
