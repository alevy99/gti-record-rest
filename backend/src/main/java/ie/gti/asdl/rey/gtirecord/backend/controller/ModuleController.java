package ie.gti.asdl.rey.gtirecord.backend.controller;

import ie.gti.asdl.rey.gtirecord.core.service.ModuleService;
import ie.gti.asdl.rey.gtirecord.model.entity.Module;
import ie.gti.asdl.rey.gtirecord.model.entity.Teacher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * REST controller exposing endpoints for retrieving {@link Module} information.
 * <p>
 * This controller is only active when the {@code "web"} Spring profile is
 * enabled, and delegates all data access to {@link ModuleService}.
 *
 * @author Andrei Levchenko
 */
@Profile("web")
@RestController
public class ModuleController {

    /** Service used to retrieve module data. */
    private final ModuleService moduleService;

    /**
     * Creates a new {@code ModuleController} with the given module service.
     *
     * @param moduleService the service used to look up modules
     */
    @Autowired
    public ModuleController(ModuleService moduleService) {
        this.moduleService = moduleService;
    }

    /**
     * Retrieves all modules.
     *
     * @return a list of all {@link Module} records
     */
    @GetMapping("/modules")
    public List<Module> getModules() {
        return moduleService.getAll();
    }

    /**
     * Retrieves all modules taught by the teacher with the given person ID.
     *
     * @param teacherPersonId the person ID of the teacher whose modules should be retrieved
     * @return a list of {@link Module} records taught by the specified teacher
     */
    @GetMapping("/modules/teacher/{teacherPersonId}")
    public List<Module> getModulesByTeacherId(@PathVariable Integer teacherPersonId) {
        return moduleService.getByTeacherPersonId(teacherPersonId);
    }
}
