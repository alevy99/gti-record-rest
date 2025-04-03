package ie.gti.asdl.rey.gtirecord.core.service;

import ie.gti.asdl.rey.gtirecord.model.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface UserService {

    Optional<Integer> insert(User user);

    void updateUserWithRoles(User user);

    void updateUser(User user);

    Optional<Integer> insertPersonToUser(User user);

    void delete(int id);

    List<User> getAll();

    Optional<User> getByUsername(String username);

    Optional<User> getById(int id);
}
