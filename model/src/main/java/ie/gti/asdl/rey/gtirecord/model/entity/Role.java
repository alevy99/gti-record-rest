package ie.gti.asdl.rey.gtirecord.model.entity;

import ie.gti.asdl.rey.gtirecord.model.annotation.KeyField;
import ie.gti.asdl.rey.gtirecord.model.annotation.ShortDescriptionField;
import lombok.Data;

import java.io.Serializable;

@Data
public class Role implements Serializable {

    public static RoleType getRoleTypeByRole(Role role) {
        if (RoleType.STUDENT.name.equals(role.getName())) {
            return Role.RoleType.STUDENT;
        } else if (RoleType.TEACHER.name.equals(role.getName())) {
            return Role.RoleType.TEACHER;
        } else if (RoleType.ADMIN.name.equals(role.getName())) {
            return Role.RoleType.ADMIN;
        } else {
            throw new IllegalArgumentException("Invalid role: " + role.getName());
        }
    }

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

    @KeyField
    private Integer id;

    @ShortDescriptionField
    private String name;

    public boolean isValid() {
        return id != null && name != null && !name.isEmpty();
    }

}
