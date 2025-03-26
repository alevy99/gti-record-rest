package ie.gti.asdl.rey.gtirecord.model.entity;

import java.util.List;

public class UserRoles {

    private User user;
    private List<Role> roles;

    public UserRoles(User user, List<Role> roles) {
        this.user = user;
        this.roles = roles;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}
