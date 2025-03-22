package ie.gti.asdl.rey.gtirecord.util;

import ie.gti.asdl.rey.gtirecord.model.Role;
import ie.gti.asdl.rey.gtirecord.model.User;

public class UserUtils {

    public static boolean isAdmin(User user) {
        return user.getRoles().stream().anyMatch (role -> Role.RoleType.ADMIN.name.equalsIgnoreCase(role.getName()));
    }

    public static boolean isTeacher(User user) {
        return user.getRoles().stream().anyMatch (role -> Role.RoleType.TEACHER.name.equalsIgnoreCase(role.getName()));
    }

    public static boolean isStudent(User user) {
        return user.getRoles().stream().anyMatch (role -> Role.RoleType.STUDENT.name.equalsIgnoreCase(role.getName()));
    }
}
