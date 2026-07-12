package ie.gti.asdl.rey.gtirecord.backend.controller;

import ie.gti.asdl.rey.gtirecord.model.entity.User;
import ie.gti.asdl.rey.gtirecord.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * REST controller exposing endpoints for retrieving {@link User} information.
 * <p>
 * This controller is only active when the {@code "web"} Spring profile is
 * enabled, and delegates all data access to {@link UserService}.
 *
 * @author Andrei Levchenko
 */
@Profile("web")
@RestController
public class UserController {

    /** Service used to retrieve user data. */
    private final UserService userService;

    /**
     * Creates a new {@code UserController} with the given user service.
     *
     * @param userService the service used to look up users
     */
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param id the ID of the user to retrieve
     * @return the {@link User} with the given ID
     * @throws ResponseStatusException with status {@link HttpStatus#NOT_FOUND} if no user with the given ID exists
     */
    @GetMapping("/users/id/{id}")
    public User getUser(@PathVariable Integer id) {
        return userService.getById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")).getBody();
    }

    /**
     * Retrieves a user by their username.
     *
     * @param username the username of the user to retrieve
     * @return the {@link User} with the given username
     * @throws ResponseStatusException with status {@link HttpStatus#NOT_FOUND} if no user with the given username exists
     */
    @GetMapping("/users/name/{username}")
    public User getUser(@PathVariable String username) {
        return userService.getByUsername(username)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")).getBody();
    }

}
