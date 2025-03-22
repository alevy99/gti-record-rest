package ie.gti.recordsystem.service;

import ie.gti.recordsystem.model.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface UserService {

    int insertUser(User user);

    void updateUser(User user);

    void deleteUser(int id);

    List<User> getAllUsers();

    Optional<User> getUserByUsername(String username);

    Optional<User> getUserById(int id);
}
