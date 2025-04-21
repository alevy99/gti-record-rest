package ie.gti.asdl.rey.gtirecord.core.service.impl;

import ie.gti.asdl.rey.gtirecord.core.dao.AssignmentDao;
import ie.gti.asdl.rey.gtirecord.core.dao.StudentAssignmentDao;
import ie.gti.asdl.rey.gtirecord.core.service.AssignmentService;
import ie.gti.asdl.rey.gtirecord.model.entity.Assignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * @author Andrei Levchenko
 */
@Service
public class AssignmentServiceImpl implements AssignmentService {

    private final AssignmentDao assignmentDao;

    private final StudentAssignmentDao studentAssignmentDao;

    @Autowired
    public AssignmentServiceImpl(AssignmentDao assignmentDao, StudentAssignmentDao studentAssignmentDao) {
        this.assignmentDao = assignmentDao;
        this.studentAssignmentDao = studentAssignmentDao;
    }

    @Override
    public Optional<Assignment> getById(Integer id) {
        return assignmentDao.getById(id);
    }

    @Override
    public List<Assignment> getAll() {
        return assignmentDao.getAll();
    }

    @Override
    public List<Assignment> getByGroupId(Integer groupId) {
        return assignmentDao.getByGroupId(groupId);
    }

    @Override
    public Optional<Integer> insert(Assignment assignment) {
        return assignmentDao.insert(assignment);
    }

    @Override
    public void update(Assignment assignment) {
        assignmentDao.update(assignment);
    }

    @Override
    public void delete(Integer id) {
        assignmentDao.delete(id);
    }

    @Transactional
    @Override
    public void deleteByGroupId(Integer groupId) {
        assignmentDao.getByGroupId(groupId).forEach(assignment -> {
            studentAssignmentDao.deleteByAssignmentId(assignment.getId());
        });
        assignmentDao.deleteByGroupId(groupId);
    }
}
