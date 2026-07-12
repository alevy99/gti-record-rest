package ie.gti.asdl.rey.gtirecord.backend.controller;

import ie.gti.asdl.rey.gtirecord.core.service.StudentService;
import ie.gti.asdl.rey.gtirecord.model.entity.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * REST controller exposing endpoints for retrieving {@link Student} information.
 * <p>
 * This controller is only active when the {@code "web"} Spring profile is
 * enabled, and delegates all data access to {@link StudentService}.
 *
 * @author Andrei Levchenko
 */
@Profile("web")
@RestController
public class StudentController {

    /** Service used to retrieve student data. */
    private final StudentService studentService;

    /**
     * Creates a new {@code StudentController} with the given student service.
     *
     * @param studentService the service used to look up students
     */
    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    /**
     * Retrieves all students.
     *
     * @return a list of all {@link Student} records
     */
    @GetMapping("/students")
    public List<Student> getStudents() {
        return studentService.getAll();
    }

}
