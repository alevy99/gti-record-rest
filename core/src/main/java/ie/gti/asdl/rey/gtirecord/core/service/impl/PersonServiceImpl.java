package ie.gti.asdl.rey.gtirecord.core.service.impl;

import ie.gti.asdl.rey.gtirecord.core.dao.AddressDao;
import ie.gti.asdl.rey.gtirecord.core.dao.PersonDao;
import ie.gti.asdl.rey.gtirecord.core.service.PersonService;
import ie.gti.asdl.rey.gtirecord.model.entity.Address;
import ie.gti.asdl.rey.gtirecord.model.entity.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public List<Person> getAll() {
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

    @Transactional
    @Override
    public Optional<Integer> insert(Person person) {
        Optional<Integer> personId = personDao.insert(person);
        personId.ifPresent(persId -> {
            if (person.getAddress() != null) {
                person.getAddress().setPersonId(persId);
                addressDao.insert(person.getAddress());
            }
        });
        return personId;
    }

    @Transactional
    @Override
    public void update(Person person) {
        personDao.update(person);
        if (person.getAddress() != null) {
            if (person.getAddress().getPersonId() != null) {
                addressDao.update(person.getAddress());
            } else {
                person.getAddress().setPersonId(person.getId());
                addressDao.insert(person.getAddress());
            }
        }
    }

    @Transactional
    @Override
    public Optional<Integer> save(Person person) {
        Optional<Integer> result;
        if (person.getId() == null) {
            result = insert(person);
        } else {
            update(person);
            result = Optional.of(person.getId());
        }
        return result;
    }

    @Transactional
    @Override
    public void delete(Person person) {
        personDao.delete(person.getId());
        addressDao.deleteByPersonId(person.getId());
    }
}
