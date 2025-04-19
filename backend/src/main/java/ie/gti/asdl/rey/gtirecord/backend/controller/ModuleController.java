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
 * @author Andrei Levchenko
 */
@Profile("web")
@RestController
public class ModuleController {

    private final ModuleService moduleService;

    @Autowired
    public ModuleController(ModuleService moduleService) {
        this.moduleService = moduleService;
    }

    @GetMapping("/modules")
    public List<Module> getModules() {
        return moduleService.getAll();
    }

    @GetMapping("/modules/teacher/{teacherPersonId}")
    public List<Module> getModulesByTeacherId(@PathVariable Integer teacherPersonId) {
        return moduleService.getByTeacherPersonId(teacherPersonId);
    }
}
