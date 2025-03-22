package ie.gti.recordsystem.util;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;

public class SpringGuiRunner {

    public static <T> ApplicationContext run(Class<T> clazz, String[] args) {
        SpringApplication app = new SpringApplication(clazz);
        // Disable Tomcat based on active profile
        if (Arrays.asList(args).contains("web")) {
            app.setWebApplicationType(WebApplicationType.SERVLET);
        } else {
            app.setWebApplicationType(WebApplicationType.NONE);
        }
        return app.run(args);
    }

}
