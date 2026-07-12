package ie.gti.asdl.rey.gtirecord.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the GTI Record REST server application.
 * <p>
 * This is a Spring Boot application configured to scan components under the
 * {@code ie.gti.asdl.rey.gtirecord} base package. On startup, it activates the
 * {@code "web"} Spring profile before launching the embedded application context.
 */
@SpringBootApplication(scanBasePackages = "ie.gti.asdl.rey.gtirecord")
public class GtiRecordRestServer {

    /**
     * Application entry point. Sets the active Spring profile to {@code "web"}
     * and starts the Spring Boot application.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        System.setProperty("spring.profiles.active", "web");
        SpringApplication app = new SpringApplication(GtiRecordRestServer.class);
        // Disable Tomcat based on active profile
        // Todo: remove the next line as SERVLET type is used by default anyway
//        app.setWebApplicationType(WebApplicationType.SERVLET);
        app.run(args);
    }
}
