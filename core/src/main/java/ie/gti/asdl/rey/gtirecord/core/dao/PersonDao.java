package ie.gti.asdl.rey.gtirecord.core.dao;

import ie.gti.asdl.rey.gtirecord.model.entity.Person;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonDao {

    List<Person> getAll();

    Optional<Person> getById(Integer id);

    Optional<Integer> insert(Person person);

    void update(Person person);

    void delete(int id);

}
