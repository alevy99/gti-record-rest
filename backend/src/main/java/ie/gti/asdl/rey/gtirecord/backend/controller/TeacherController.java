package ie.gti.asdl.rey.gtirecord.backend.controller;

import ie.gti.asdl.rey.gtirecord.core.service.TeacherService;
import ie.gti.asdl.rey.gtirecord.model.entity.Teacher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller exposing endpoints for retrieving {@link Teacher} information.
 * <p>
 * This controller is only active when the {@code "web"} Spring profile is
 * enabled, and delegates all data access to {@link TeacherService}.
 *
 * @author Andrei Levchenko
 */
@Profile("web")
@RestController
public class TeacherController {

    /** Service used to retrieve teacher data. */
    private final TeacherService teacherService;

    /**
     * Creates a new {@code TeacherController} with the given teacher service.
     *
     * @param teacherService the service used to look up teachers
     */
    @Autowired
    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    /**
     * Retrieves all teachers.
     *
     * @return a list of all {@link Teacher} records
     */
    @GetMapping("/teachers")
    public List<Teacher> getTeachers() {
        return teacherService.getAll();
    }
}
