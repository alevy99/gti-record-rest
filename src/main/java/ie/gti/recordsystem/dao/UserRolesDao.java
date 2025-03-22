package ie.gti.recordsystem.dao;

import ie.gti.recordsystem.model.Role;
import ie.gti.recordsystem.model.User;
import ie.gti.recordsystem.model.UserRoles;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRolesDao {

    void insertUserRoles(int userID, List<Role> roles);

    void deleteUserRoles(int userID, List<Role> roles);

    void deleteUserRoles(int userID);
}
