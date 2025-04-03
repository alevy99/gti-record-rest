package ie.gti.asdl.rey.gtirecord.core.service;

import ie.gti.asdl.rey.gtirecord.model.entity.Person;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface PersonService {

    List<Person> getAll();

    Optional<Person> getById(Integer id);

    Optional<Integer> insert(Person person);

    void update(Person person);

    Optional<Integer> save(Person person);

    void delete(Person person);

}
