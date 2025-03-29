package ie.gti.asdl.rey.gtirecord.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ServiceManager {

    private final UserService userService;

    private final PersonService personService;

    @Autowired
    public ServiceManager(UserService userService, PersonService personService) {
        this.userService = userService;
        this.personService = personService;
    }

    public UserService getUserService() {
        return userService;
    }

    public PersonService getPersonService() {
        return personService;
    }
}
