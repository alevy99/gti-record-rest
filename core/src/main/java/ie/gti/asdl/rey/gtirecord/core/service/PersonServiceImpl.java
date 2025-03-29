package ie.gti.asdl.rey.gtirecord.core.service;

import ie.gti.asdl.rey.gtirecord.core.dao.AddressDao;
import ie.gti.asdl.rey.gtirecord.core.dao.PersonDao;
import ie.gti.asdl.rey.gtirecord.model.entity.Address;
import ie.gti.asdl.rey.gtirecord.model.entity.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PersonServiceImpl implements PersonService {

    private final PersonDao personDao;
    private final AddressDao addressDao;

    @Autowired
    PersonServiceImpl(PersonDao personDao, AddressDao addressDao) {
        this.personDao = personDao;
        this.addressDao = addressDao;
    }

    @Override
    public List<Person> getAllPersons() {
        return personDao.getAll();
    }

    @Override
    public Optional<Person> getById(int id) {
        Optional<Person> person = personDao.getById(id);
        person.ifPresent( p -> {
            Optional<Address> address = addressDao.getByPersonId(id);
            p.setAddress(address.orElse(null));
        });
        return person;
    }

    @Override
    public Optional<Integer> insert(Person person) {
        return personDao.insert(person);
    }

    @Override
    public void update(Person person) {
        personDao.update(person);

    }

    @Override
    public void delete(Person person) {
        personDao.delete(person.getId());
    }
}
