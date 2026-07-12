package ie.gti.asdl.rey.gtirecord.desktop.ui;

import ie.gti.asdl.rey.gtirecord.desktop.ui.frame.*;
import ie.gti.asdl.rey.gtirecord.core.ServiceManager;
import ie.gti.asdl.rey.gtirecord.model.entity.Role;
import ie.gti.asdl.rey.gtirecord.model.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Manages the application's frames (screens), handling navigation between them,
 * caching created frame instances, and tracking the currently authenticated user.
 * <p>
 * Uses a stack of parent frame types ({@link #parentFrameStack}) to support
 * "back" navigation, and a cache ({@link #formCache}) to reuse already created
 * frame instances instead of recreating them each time.
 */
@Component
public class FrameManager {

    /** The currently authenticated user, or {@code null} if no user is logged in. */
    @Setter
    @Getter
    private User activeUser;

    /** Service manager passed to created frames to give them access to business logic. */
    private final ServiceManager serviceManager;

    /** The type of the currently active (displayed) frame. */
    private FrameType activeFrameType = FrameType.NO_FRAME;

    /** Stack of parent frame types, used for "back" navigation. */
    private final Stack<FrameType> parentFrameStack = new Stack<>();

    /**
     * Enumerates all available frame types in the application, each associated
     * with its corresponding frame class.
     */
    public enum FrameType {
        /** Represents the absence of a frame (used as a placeholder / navigation root marker). */
        NO_FRAME(null),
        /** Login frame. */
        LOGIN(LoginFrame.class),
        /** Main application frame. */
        MAIN(MainFrame.class),
        /** User management frame. */
        USER(UserFrame.class),
        /** Person management frame. */
        PERSON(PersonFrame.class),
        /** Teacher management frame. */
        TEACHER(TeacherFrame.class),
        /** Student management frame. */
        STUDENT(StudentFrame.class),
        /** Group management frame. */
        GROUP(GroupFrame.class),
        /** Department management frame. */
        DEPARTMENT(DepartmentFrame.class),
        /** Module management frame. */
        MODULE(ModuleFrame.class),
        /** Course management frame. */
        COURSE(CourseFrame.class),
        /** Assignment management frame. */
        ASSIGNMENT(AssignmentFrame.class),
        /** Student report frame. */
        STUDENT_REPORT(StudentReportFrame.class);

        /** The frame class associated with this type. */
        private final Class<? extends AbstractFrame> frameClass;

        /**
         * Creates an enum constant associated with the given frame class.
         *
         * @param frameClass the frame class implementing {@link AbstractFrame}, or {@code null} for {@link #NO_FRAME}
         */
        FrameType(Class<? extends AbstractFrame> frameClass) {
            this.frameClass = frameClass;
        }
    }

    /** Cache of created frame instances keyed by their type, to avoid re-instantiation. */
    private final Map<FrameType, AbstractFrame> formCache = new HashMap<>();

    /**
     * Creates a frame manager with the given service manager.
     *
     * @param serviceManager the service manager used when creating frames
     */
    @Autowired
    private FrameManager(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    /**
     * Returns the frame instance of the given type, creating it on first access
     * and caching it for subsequent calls.
     *
     * @param frameType the type of frame requested
     * @param <T>       the expected frame type (cast is performed automatically)
     * @return the requested frame instance, or {@code null} if {@code frameType} is {@link FrameType#NO_FRAME}
     * @throws RuntimeException if the frame instance could not be created
     */
    public <T extends AbstractFrame> T getFrame(FrameType frameType) {
        AbstractFrame frame;

        if (frameType == FrameType.NO_FRAME) {
            return null;
        }

        if (formCache.containsKey(frameType)) {
            frame = formCache.get(frameType);
        } else {
            // Create new instance for prototype forms
            try {
                frame = (AbstractFrame) frameType.frameClass.getDeclaredConstructors()[0].newInstance(this, serviceManager);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            formCache.put(frameType, frame);
        }

        return (T) formCache.get(frameType);
    }

    /**
     * Displays the parent frame from the navigation stack, hiding the current
     * active frame.
     * <p>
     * If the parent frame stack is empty or its top element is
     * {@link FrameType#NO_FRAME}, the application exits (indicating that the
     * navigation root has been reached).
     */
    // Show Parent Frame
    public void showParent() {
        // Check if we are at the Root
        if (parentFrameStack.isEmpty() || FrameType.NO_FRAME == parentFrameStack.peek()) {
            System.exit(0);
        }
        FrameType frameType = parentFrameStack.pop();
        assert activeFrameType != FrameType.NO_FRAME; // There should be a frame
        getFrame(activeFrameType).setVisible(false);
        showFrame(frameType);
    }

    /**
     * Displays a child frame, optionally hiding the current active frame and
     * pushing it onto the parent frame stack for later back-navigation.
     *
     * @param subFrame    the type of the child frame to display
     * @param hideCurrent if {@code true}, the current active frame is hidden
     *                    and pushed onto the parent frame stack
     */
    // Show
    public void showSub(FrameType subFrame, boolean hideCurrent) {
        if (hideCurrent && activeFrameType != FrameType.NO_FRAME) {
            parentFrameStack.push(activeFrameType);
            getFrame(activeFrameType).setVisible(false);
        }
        showFrame(subFrame);
    }

    /**
     * Displays a child frame, hiding the current active frame
     * (equivalent to calling {@link #showSub(FrameType, boolean)} with {@code hideCurrent = true}).
     *
     * @param subFrame the type of the child frame to display
     */
    // Show subframe
    public void showSub(FrameType subFrame) {
        showSub(subFrame, true);
    }

    /**
     * Displays the frame of the given type and marks it as the current active frame.
     *
     * @param frameType the type of frame to display
     */
    private void showFrame(FrameType frameType) {
        getFrame(frameType).showFrame();
        activeFrameType = frameType;
    }

    /**
     * Checks whether a user is currently logged in.
     *
     * @return {@code true} if a user is logged in
     */
    public boolean isLoggedIn() {
        return activeUser != null;
    }

    /**
     * Logs the current user out by clearing the active user.
     */
    public void logout() {
        activeUser = null;
    }

    /**
     * Checks whether the current user is logged in and has the administrator role.
     *
     * @return {@code true} if a user is logged in and has the {@code ADMIN} role
     */
    public boolean isLoggedInAsAdmin() {
        return isLoggedIn() && activeUser.getRoles().contains(Role.RoleType.ADMIN.asRole());
    }

    /**
     * Checks whether the current user is logged in and has the teacher role.
     *
     * @return {@code true} if a user is logged in and has the {@code TEACHER} role
     */
    public boolean isLoggedInAsTeacher() {
        return isLoggedIn() && activeUser.getRoles().contains(Role.RoleType.TEACHER.asRole());
    }

    /**
     * Checks whether the current user is logged in and has the student role.
     *
     * @return {@code true} if a user is logged in and has the {@code STUDENT} role
     */
    public boolean isLoggedInAsStudent() {
        return isLoggedIn() && activeUser.getRoles().contains(Role.RoleType.STUDENT.asRole());
    }

    /**
     * Checks whether the current user is logged in and has either the
     * administrator or teacher role.
     *
     * @return {@code true} if a user is logged in and has the {@code ADMIN} or {@code TEACHER} role
     */
    public boolean isLoggedInAsAdminOrTeacher() {
        return isLoggedIn()
                && (activeUser.getRoles().contains(Role.RoleType.ADMIN.asRole())
                || activeUser.getRoles().contains(Role.RoleType.TEACHER.asRole()));
    }
}