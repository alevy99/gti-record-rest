package ie.gti.asdl.rey.gtirecord.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ServiceManager {

    private final UserService userService;

    private final PersonService personService;

    private final DepartmentService departmentService;

    private final ModuleService moduleService;

    private final CourseService courseService;

    @Autowired
    public ServiceManager(UserService userService, PersonService personService, DepartmentService departmentService,
                          ModuleService moduleService, CourseService courseService) {
        this.userService = userService;
        this.personService = personService;
        this.departmentService = departmentService;
        this.moduleService = moduleService;
        this.courseService = courseService;
    }

    public UserService getUserService() {
        return userService;
    }

    public PersonService getPersonService() {
        return personService;
    }

    public DepartmentService getDepartmentService() {
        return departmentService;
    }

    public ModuleService getModuleService() {
        return moduleService;
    }

    public CourseService getCourseService() {
        return courseService;
    }
}
