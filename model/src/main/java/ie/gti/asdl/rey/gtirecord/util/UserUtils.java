package ie.gti.asdl.rey.gtirecord.util;

import ie.gti.asdl.rey.gtirecord.model.Role;
import ie.gti.asdl.rey.gtirecord.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserUtils {

    private static final Logger logger = LoggerFactory.getLogger(UserUtils.class);

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
