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
 * Entry point for the GTI Record desktop GUI application.
 * <p>
 * This is a Spring Boot application configured to scan components under the
 * {@code ie.gti.asdl.rey.gtirecord} base package. Unlike the REST server
 * variant, this application runs in a non-headless AWT environment and
 * launches the desktop Swing UI, starting at the login frame.
 *
 * @author Andrei Levchenko
 */
@SpringBootApplication(scanBasePackages = "ie.gti.asdl.rey.gtirecord")
public class GtiRecordDesktopGuiApp {

    /**
     * Application entry point. Disables headless AWT mode, starts the Spring
     * application context via {@link SpringGuiRunner}, and displays the initial
     * login frame using the {@link FrameManager} bean.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");

        ApplicationContext context = SpringGuiRunner.run(GtiRecordDesktopGuiApp.class, args);

        FrameManager frameManager = context.getBean(FrameManager.class);
        frameManager.showSub(LOGIN);
    }
}
