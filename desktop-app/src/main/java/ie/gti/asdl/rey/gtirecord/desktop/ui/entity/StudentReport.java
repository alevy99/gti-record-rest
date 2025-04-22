package ie.gti.asdl.rey.gtirecord.desktop.ui.entity;

import ie.gti.asdl.rey.gtirecord.model.entity.Student;
import lombok.Data;

/**
 * @author Andrei Levchenko
 */
@Data
public class StudentReport {

    private Student student;

    private Double averageGrade;

    private Double totalGrade;

}
