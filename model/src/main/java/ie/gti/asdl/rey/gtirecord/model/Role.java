package ie.gti.asdl.rey.gtirecord.model;

import java.io.Serializable;

public class Role implements Serializable {

    public enum RoleType {
        ADMIN(1, "admin"),
        TEACHER(2, "teacher"),
        STUDENT(3, "student");

        public final int id;
        public final String name;

        RoleType(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public Role asRole() {
            Role role = new Role();
            role.setId(id);
            role.setName(name);
            return role;
        }
    }

    private int id;

    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Role && ((Role) obj).getId() == id;
    }

    @Override
    public int hashCode() {
        return Integer.valueOf(id).hashCode();
    }
}
