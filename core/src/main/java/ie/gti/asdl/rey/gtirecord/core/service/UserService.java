package ie.gti.asdl.rey.gtirecord.core.service;

import ie.gti.recordsystem.model.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface UserService {

    Optional<Integer> insertUser(User user);

    void updateUser(User user);

    void deleteUser(int id);

    List<User> getAllUsers();

    Optional<User> getUserByUsername(String username);

    Optional<User> getUserById(int id);
}
