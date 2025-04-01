package ie.gti.asdl.rey.gtirecord.model.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StudentAssignment {

    private Student student;

    private Assignment assignment;

    private Boolean isSubmitted;

    private Boolean isGraded;

    private Double grade;

    private LocalDateTime submitDate;
    
}
