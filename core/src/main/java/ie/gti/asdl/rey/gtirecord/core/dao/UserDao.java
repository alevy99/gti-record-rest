package ie.gti.asdl.rey.gtirecord.core.dao;

import ie.gti.asdl.rey.gtirecord.model.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDao {

    Optional<User> getUserById(int id);

    Optional<User> getUserByUsername(String username);

    List<User> getAllUsers();

    Optional<Integer> insertUser(User user);

    void deleteUserById(int id);

    void deleteUsersById(List<Integer> ids);

    void updateUser(User user);


}
