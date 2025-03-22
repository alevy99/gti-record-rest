package ie.gti.recordsystem.dao;

import ie.gti.recordsystem.model.Person;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

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
