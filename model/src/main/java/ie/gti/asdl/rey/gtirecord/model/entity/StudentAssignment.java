package ie.gti.asdl.rey.gtirecord.model.entity;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
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
