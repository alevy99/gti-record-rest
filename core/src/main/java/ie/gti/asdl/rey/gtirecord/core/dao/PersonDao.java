package ie.gti.asdl.rey.gtirecord.core.dao;

import ie.gti.asdl.rey.gtirecord.model.entity.Person;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonDao {

    List<Person> getAllPersons();

    Optional<Person> getPersonById(int id);

    Optional<Integer> insertPerson(Person person);

    void updatePerson(Person person);

    void deletePerson(int id);

}
