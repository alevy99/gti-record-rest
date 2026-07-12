package ie.gti.asdl.rey.gtirecord.model.util;

import ie.gti.asdl.rey.gtirecord.model.entity.Role;
import ie.gti.asdl.rey.gtirecord.model.entity.User;

/**
 * Utility class providing helper operations for checking a {@link User}'s
 * assigned roles.
 */
public class UserUtils {

    /**
     * Checks whether the given user has the administrator role.
     *
     * @param user the user to check
     * @return {@code true} if the user has a role matching {@link Role.RoleType#ADMIN}
     */
    public static boolean isAdmin(User user) {
        return user.getRoles().stream().anyMatch (role -> Role.RoleType.ADMIN.name.equalsIgnoreCase(role.getName()));
    }

    /**
     * Checks whether the given user has the teacher role.
     *
     * @param user the user to check
     * @return {@code true} if the user has a role matching {@link Role.RoleType#TEACHER}
     */
    public static boolean isTeacher(User user) {
        return user.getRoles().stream().anyMatch (role -> Role.RoleType.TEACHER.name.equalsIgnoreCase(role.getName()));
    }

    /**
     * Checks whether the given user has the student role.
     *
     * @param user the user to check
     * @return {@code true} if the user has a role matching {@link Role.RoleType#STUDENT}
     */
    public static boolean isStudent(User user) {
        return user.getRoles().stream().anyMatch (role -> Role.RoleType.STUDENT.name.equalsIgnoreCase(role.getName()));
    }
}
