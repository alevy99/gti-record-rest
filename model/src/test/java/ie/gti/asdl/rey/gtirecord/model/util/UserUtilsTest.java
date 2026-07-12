package ie.gti.asdl.rey.gtirecord.model.util;

import ie.gti.asdl.rey.gtirecord.model.entity.Role;
import ie.gti.asdl.rey.gtirecord.model.entity.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserUtilsTest {

    private User newUser(String username, Role.RoleType... roleTypes) {
        User user = new User(1, username, "password", 1);
        for (Role.RoleType roleType : roleTypes) {
            user.getRoles().add(roleType.asRole());
        }
        return user;
    }

    @Test
    void isAdmin_returnsTrue_whenUserHasAdminRole() {
        User user = newUser("admin.user", Role.RoleType.ADMIN);

        assertTrue(UserUtils.isAdmin(user));
        assertFalse(UserUtils.isTeacher(user));
        assertFalse(UserUtils.isStudent(user));
    }

    @Test
    void isTeacher_returnsTrue_whenUserHasTeacherRole() {
        User user = newUser("teacher.user", Role.RoleType.TEACHER);

        assertTrue(UserUtils.isTeacher(user));
        assertFalse(UserUtils.isAdmin(user));
        assertFalse(UserUtils.isStudent(user));
    }

    @Test
    void isStudent_returnsTrue_whenUserHasStudentRole() {
        User user = newUser("student.user", Role.RoleType.STUDENT);

        assertTrue(UserUtils.isStudent(user));
        assertFalse(UserUtils.isAdmin(user));
        assertFalse(UserUtils.isTeacher(user));
    }

    @Test
    void roleChecks_returnFalse_whenUserHasNoRoles() {
        User user = newUser("no.roles.user");

        assertFalse(UserUtils.isAdmin(user));
        assertFalse(UserUtils.isTeacher(user));
        assertFalse(UserUtils.isStudent(user));
    }

    @Test
    void roleChecks_supportMultipleRolesOnSameUser() {
        User user = newUser("multi.role.user", Role.RoleType.TEACHER, Role.RoleType.ADMIN);

        assertTrue(UserUtils.isAdmin(user));
        assertTrue(UserUtils.isTeacher(user));
        assertFalse(UserUtils.isStudent(user));
    }
}
