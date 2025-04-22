package ie.gti.asdl.rey.gtirecord.model.entity;

import jakarta.validation.Valid;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StudentAssignment {

    @Valid
    private Student student;

    @Valid
    private Assignment assignment;

    private Boolean isSubmitted;

    private Boolean isGraded;

    private Integer grade;

    private LocalDateTime submitDateTime;
    
}
