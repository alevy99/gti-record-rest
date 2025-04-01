package ie.gti.asdl.rey.gtirecord.model.entity;

import ie.gti.asdl.rey.gtirecord.model.annotation.KeyField;
import ie.gti.asdl.rey.gtirecord.model.annotation.ShortDescriptionField;
import lombok.Data;

import java.io.Serializable;

@Data
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

    @KeyField
    private Integer id;

    @ShortDescriptionField
    private String name;

}
