package ie.gti.recordsystem.dao;

import ie.gti.recordsystem.model.User;
import org.springframework.stereotype.Component;
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
