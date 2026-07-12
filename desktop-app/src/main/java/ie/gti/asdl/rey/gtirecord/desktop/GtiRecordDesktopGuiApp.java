/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package ie.gti.asdl.rey.gtirecord.desktop;

import ie.gti.asdl.rey.gtirecord.desktop.ui.FrameManager;
import ie.gti.asdl.rey.gtirecord.desktop.util.SpringGuiRunner;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import static ie.gti.asdl.rey.gtirecord.desktop.ui.FrameManager.FrameType.LOGIN;

/**
 *
 * @author Andrei Levchenko
 */
@SpringBootApplication(scanBasePackages = "ie.gti.asdl.rey.gtirecord")
public class GtiRecordDesktopGuiApp {

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");

        ApplicationContext context = SpringGuiRunner.run(GtiRecordDesktopGuiApp.class, args);

        FrameManager frameManager = context.getBean(FrameManager.class);
        frameManager.showSub(LOGIN);
    }
}
