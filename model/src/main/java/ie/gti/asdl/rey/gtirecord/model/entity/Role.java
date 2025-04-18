package ie.gti.asdl.rey.gtirecord.model.entity;

import ie.gti.asdl.rey.gtirecord.model.annotation.KeyField;
import ie.gti.asdl.rey.gtirecord.model.annotation.ShortDescriptionField;
import ie.gti.asdl.rey.gtirecord.model.validation.OnUpdate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Role {

    public static RoleType getRoleTypeByRole(@org.jetbrains.annotations.NotNull Role role) {
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
    @NotNull(groups = OnUpdate.class)
    private Integer id;

    @ShortDescriptionField
    @NotBlank
    private String name;

    public boolean isValid() {
        return id != null && name != null && !name.isEmpty();
    }

}
