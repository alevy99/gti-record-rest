package ie.gti.asdl.rey.gtirecord.core.dao;

import ie.gti.asdl.rey.gtirecord.model.entity.Role;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface UserRolesDao {

    void insert(int userId, Set<Role> roles);

    void deleteByUserId(int userId, Set<Role> roles);

    void deleteByUserId(int userId);

    void delete(Integer userId, Integer roleId);
}
