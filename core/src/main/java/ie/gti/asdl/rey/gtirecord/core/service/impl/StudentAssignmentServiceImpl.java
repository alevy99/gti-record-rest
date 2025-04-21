package ie.gti.asdl.rey.gtirecord.core.service.impl;

import ie.gti.asdl.rey.gtirecord.core.dao.StudentAssignmentDao;
import ie.gti.asdl.rey.gtirecord.core.service.StudentAssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Andrei Levchenko
 */
@Service
public class StudentAssignmentServiceImpl implements StudentAssignmentService {

    private final StudentAssignmentDao studentAssignmentDao;

    @Autowired
    public StudentAssignmentServiceImpl(StudentAssignmentDao studentAssignmentDao) {
        this.studentAssignmentDao = studentAssignmentDao;
    }

    @Override
    public void deleteByAssignmentId(Integer assignmentId) {
        studentAssignmentDao.deleteByAssignmentId(assignmentId);
    }
}
