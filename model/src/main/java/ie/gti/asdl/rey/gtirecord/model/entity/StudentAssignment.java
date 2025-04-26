package ie.gti.asdl.rey.gtirecord.model.entity;

import jakarta.validation.Valid;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
public class StudentAssignment {

    @EqualsAndHashCode.Include
    @Valid
    private Student student;

    @EqualsAndHashCode.Include
    @Valid
    private Assignment assignment;

    private Boolean isSubmitted;

    private Boolean isGraded;

    private Integer grade;

    private LocalDateTime submitDateTime;
    
}
