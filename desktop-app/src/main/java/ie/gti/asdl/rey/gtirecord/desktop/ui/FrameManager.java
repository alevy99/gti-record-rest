package ie.gti.asdl.rey.gtirecord.desktop.ui;

import ie.gti.asdl.rey.gtirecord.desktop.ui.frame.*;
import ie.gti.asdl.rey.gtirecord.core.ServiceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

@Component
public class FrameManager {

    private final ServiceManager serviceManager;

    private FrameType activeFrameType = FrameType.NO_FRAME;

    private final Stack<FrameType> parentFrameStack = new Stack<>();

    public enum FrameType {
        NO_FRAME(null),
        LOGIN(LoginFrame.class),
        MAIN(MainFrame.class),
        USER(UserFrame.class),
        PERSON(PersonFrame.class),
        TEACHER(TeacherFrame.class),
        GROUP(GroupFrame.class),
        DEPARTMENT(DepartmentFrame.class),
        MODULE(ModuleFrame.class),
        COURSE(CourseFrame.class);

        private final Class<? extends AbstractFrame> frameClass;

        FrameType(Class<? extends AbstractFrame> frameClass) {
            this.frameClass = frameClass;
        }
    }

    private final Map<FrameType, AbstractFrame> formCache = new HashMap<>();

    @Autowired
    private FrameManager(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
        // Initialize singleton forms
//        formCache.put(FrameType.LOGIN, new LoginFrame(this, serviceManager));
//        formCache.put(FrameType.MAIN, new MainFrame(this, serviceManager));
    }

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

    public void showParent() {
        if (parentFrameStack.isEmpty() || FrameType.NO_FRAME == parentFrameStack.peek()) {
            System.exit(0);
        }
        FrameType frameType = parentFrameStack.pop();
        assert activeFrameType != FrameType.NO_FRAME;
        getFrame(activeFrameType).setVisible(false);
        showFrame(frameType);
    }

    public void showSub(FrameType subFrame, boolean hideCurrent) {
        if (hideCurrent && activeFrameType != FrameType.NO_FRAME) {
            parentFrameStack.push(activeFrameType);
            getFrame(activeFrameType).setVisible(false);
        }
        showFrame(subFrame);
    }

    public void showSub(FrameType subFrame) {
        showSub(subFrame, true);
    }

    private void showFrame(FrameType frameType) {
        getFrame(frameType).showFrame();
        activeFrameType = frameType;
    }
}
