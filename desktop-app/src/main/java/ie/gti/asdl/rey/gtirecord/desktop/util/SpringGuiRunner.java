package ie.gti.asdl.rey.gtirecord.desktop.util;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;

public class SpringGuiRunner {

    private static boolean isTestMode = false;

    public static <T> ApplicationContext run(Class<T> clazz, boolean isTestMode, String[] args) {
        SpringGuiRunner.isTestMode = isTestMode;

        SpringApplication app = new SpringApplication(clazz);
        // Disable Tomcat based on active profile
        if (Arrays.asList(args).contains("web")) {
            app.setWebApplicationType(WebApplicationType.SERVLET);
        } else {
            app.setWebApplicationType(WebApplicationType.NONE);
        }
        return app.run(args);
    }

    public static boolean isTestMode() {
        return isTestMode;
    }

    public static void setTestMode(boolean isTestMode) {
        SpringGuiRunner.isTestMode = isTestMode;
    }
}
