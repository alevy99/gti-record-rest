package ie.gti.asdl.rey.gtirecord.core;

import ie.gti.asdl.rey.gtirecord.core.service.*;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ServiceManager {

    private final UserService userService;

    private final PersonService personService;

    private final DepartmentService departmentService;

    private final ModuleService moduleService;

    private final CourseService courseService;

    private final CourseModuleService courseModuleService;

    private final TeacherService teacherService;

    private final StudentService studentService;

    private final TeacherModuleService teacherModuleService;

    private final GroupService groupService;

    private final GroupModuleService groupModuleService;

    private final AssignmentService assignmentService;

    @Autowired
    public ServiceManager(UserService userService, PersonService personService, DepartmentService departmentService,
                          ModuleService moduleService, CourseService courseService, CourseModuleService courseModuleService,
                          TeacherService teacherService, StudentService studentService, TeacherModuleService teacherModuleService,
                          GroupService groupService, GroupModuleService groupModuleService, AssignmentService assignmentService) {
        this.userService = userService;
        this.personService = personService;
        this.departmentService = departmentService;
        this.moduleService = moduleService;
        this.courseService = courseService;
        this.courseModuleService = courseModuleService;
        this.teacherService = teacherService;
        this.studentService = studentService;
        this.teacherModuleService = teacherModuleService;
        this.groupService = groupService;
        this.groupModuleService = groupModuleService;
        this.assignmentService = assignmentService;
    }

}
