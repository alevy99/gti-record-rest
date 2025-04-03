package ie.gti.asdl.rey.gtirecord.core.dao;

import ie.gti.asdl.rey.gtirecord.model.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDao {

    Optional<User> getById(Integer id);

    Optional<User> getByUsername(String username);

    List<User> getAll();

    Optional<Integer> insert(User user);

    void delete(int id);

    void deleteUsersById(List<Integer> ids);

    void update(User user);


    Optional<User> getByPersonId(Integer personId);
}
