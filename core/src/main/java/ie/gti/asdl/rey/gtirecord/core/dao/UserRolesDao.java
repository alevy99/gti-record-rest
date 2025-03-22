package ie.gti.asdl.rey.gtirecord.core.dao;

import ie.gti.asdl.rey.gtirecord.model.Role;
import ie.gti.asdl.rey.gtirecord.model.User;
import ie.gti.asdl.rey.gtirecord.model.UserRoles;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRolesDao {

    void insertUserRoles(int userID, List<Role> roles);

    void deleteUserRoles(int userID, List<Role> roles);

    void deleteUserRoles(int userID);
}
