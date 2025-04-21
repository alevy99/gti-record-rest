package ie.gti.asdl.rey.gtirecord.core.dao;

import ie.gti.asdl.rey.gtirecord.model.entity.StudentAssignment;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author Andrei Levchenko
 */
@Repository
public interface StudentAssignmentDao {

    List<StudentAssignment> getByAssignmentId(Integer assignmentId);

    void insert(StudentAssignment studentAssignment);

    void deleteByAssignmentId(Integer assignmentId);
}
