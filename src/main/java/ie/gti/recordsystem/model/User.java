package ie.gti.recordsystem.model;

import java.util.ArrayList;
import java.util.List;

public class User {

    private long id;

    private String username;

    private String password;

    private final List<Role> roles;

    private Person person;

    public User() {
        roles = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    @Override
    public String toString() {
        return "ID: " + id + ", Username: " + username;
    }
}
