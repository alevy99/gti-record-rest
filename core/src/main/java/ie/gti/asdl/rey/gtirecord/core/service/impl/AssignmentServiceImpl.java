package ie.gti.asdl.rey.gtirecord.core.service.impl;

import ie.gti.asdl.rey.gtirecord.core.dao.AssignmentDao;
import ie.gti.asdl.rey.gtirecord.core.service.AssignmentService;
import ie.gti.asdl.rey.gtirecord.model.entity.Assignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author Andrei Levchenko
 */
@Service
public class AssignmentServiceImpl implements AssignmentService {

    private final AssignmentDao assignmentDao;

    @Autowired
    public AssignmentServiceImpl(AssignmentDao assignmentDao) {
        this.assignmentDao = assignmentDao;
    }

    @Override
    public Optional<Assignment> getById(Integer id) {
        return Optional.empty();
    }

    @Override
    public List<Assignment> getAll() {
        return List.of();
    }

    @Override
    public Optional<Integer> insert(Assignment assignment) {
        return Optional.empty();
    }

    @Override
    public void update(Assignment assignment) {

    }

    @Override
    public void delete(Integer id) {

    }
}
