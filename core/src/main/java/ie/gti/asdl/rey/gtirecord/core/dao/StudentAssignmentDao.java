package ie.gti.asdl.rey.gtirecord.core.dao;

import org.springframework.stereotype.Repository;

/**
 * @author Andrei Levchenko
 */
@Repository
public interface StudentAssignmentDao {


    void deleteByAssignmentId(Integer assignmentId);
}
