/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package ie.gti.asdl.rey.gtirecord.desktop;

import ie.gti.asdl.rey.gtirecord.desktop.ui.FrameManager;
import ie.gti.asdl.rey.gtirecord.desktop.util.SpringGuiRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import static ie.gti.asdl.rey.gtirecord.desktop.ui.FrameManager.FrameType.LOGIN;

/**
 *
 * @author Andrei
 */
@SpringBootApplication(scanBasePackages = "ie.gti.asdl.rey.gtirecord")
public class GtiRecordDesktopGuiApp {

//    @Autowired
//    private static LoginFrame loginFrame;

    //    public static AnnotationConfigApplicationContext getContext() {
//        return context;
//    }

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
//        ApplicationContext context = SpringApplication.run(GtiRecordDesktopGuiApp.class, args);

        ApplicationContext context = SpringGuiRunner.run(GtiRecordDesktopGuiApp.class, false, args);

//        SpringApplication app = new SpringApplication(GtiRecordDesktopGuiApp.class);
//        // Disable Tomcat based on active profile
//        if (Arrays.asList(args).contains("web")) {
//            app.setWebApplicationType(WebApplicationType.SERVLET);
//        } else {
//            app.setWebApplicationType(WebApplicationType.NONE);
//        }
//        ApplicationContext context = app.run(args);

//        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
//                ApplicationConfig.class
//        );

//        System.out.println("Hello World!");
        FrameManager frameManager = context.getBean(FrameManager.class);
        frameManager.showSub(LOGIN);
        
//        loginFrame = new LoginFrame(context);//context.getBean(LoginFrame.class);
    }
}
