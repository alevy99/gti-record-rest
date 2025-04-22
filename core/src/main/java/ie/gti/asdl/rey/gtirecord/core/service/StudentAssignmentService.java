package ie.gti.asdl.rey.gtirecord.core.service;

import ie.gti.asdl.rey.gtirecord.model.entity.StudentAssignment;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Andrei Levchenko
 */
@Service
public interface StudentAssignmentService {

    List<StudentAssignment> getByAssignmentId(Integer assignmentId);

    void update(StudentAssignment studentAssignment);

    void deleteByAssignmentId(Integer assignmentId);
}
