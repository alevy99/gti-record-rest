package ie.gti.asdl.rey.gtirecord.desktop.ui.entity;

import ie.gti.asdl.rey.gtirecord.model.entity.Module;
import ie.gti.asdl.rey.gtirecord.model.entity.Student;
import ie.gti.asdl.rey.gtirecord.model.entity.StudentAssignment;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrei Levchenko
 */
@Data
public class StudentModuleReport {

    private Student student;

    private Module module;

    private List<StudentAssignment> studentAssignments = new ArrayList<>();

    private Integer averageGrade;

    private Double averageGradePercent;

    private Integer totalGrade;

    private Double totalGradePercent;

}
