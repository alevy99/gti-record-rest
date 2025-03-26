package ie.gti.asdl.rey.gtirecord.core.service;

import ie.gti.asdl.rey.gtirecord.model.entity.Person;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface PersonService {

    List<Person> getAllPersons();

    Optional<Person> getPersonById(int id);

    Optional<Integer> insertPerson(Person person);

    void updatePerson(Person person);

    void deletePerson(Person person);

}
