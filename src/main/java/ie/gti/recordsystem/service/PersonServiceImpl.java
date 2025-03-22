package ie.gti.recordsystem.service;

import ie.gti.recordsystem.model.Person;

import java.util.List;
import java.util.Optional;

public class PersonServiceImpl implements PersonService {

    @Override
    public List<Person> getAllPersons() {
        return List.of();
    }

    @Override
    public Optional<Person> getPersonById(int id) {
        return Optional.empty();
    }

    @Override
    public Optional<Integer> insertPerson(Person person) {
        return Optional.empty();
    }

    @Override
    public void updatePerson(Person person) {

    }

    @Override
    public void deletePerson(Person person) {

    }
}
