package ie.gti.asdl.rey.gtirecord.desktop.util;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;

/**
 * Utility class responsible for bootstrapping a Spring application context for
 * the desktop GUI variant of the application.
 * <p>
 * Unlike the REST server, the desktop GUI does not require an embedded servlet
 * container. This runner inspects the provided command-line arguments and
 * configures the {@link WebApplicationType} accordingly: if the {@code "web"}
 * argument is present, a {@link WebApplicationType#SERVLET} (Tomcat-based)
 * context is started; otherwise, the web server is disabled entirely via
 * {@link WebApplicationType#NONE}.
 */
public class SpringGuiRunner {

    /**
     * Configures and runs a Spring application context for the given application class.
     * <p>
     * The web application type is determined based on the presence of the
     * {@code "web"} argument: if present, an embedded servlet container is
     * started; otherwise, no web server is started at all.
     *
     * @param clazz the primary Spring Boot application class to bootstrap
     * @param args  command-line arguments passed to the application; if it
     *              contains {@code "web"}, a servlet-based context is started
     * @param <T>   the type of the application class
     * @return the resulting {@link ApplicationContext}
     */
    public static <T> ApplicationContext run(Class<T> clazz, String[] args) {
        SpringApplication app = new SpringApplication(clazz);
        // Disable Tomcat based on the active profile
        if (Arrays.asList(args).contains("web")) {
            app.setWebApplicationType(WebApplicationType.SERVLET);
        } else {
            app.setWebApplicationType(WebApplicationType.NONE);
        }
        return app.run(args);
    }

}
