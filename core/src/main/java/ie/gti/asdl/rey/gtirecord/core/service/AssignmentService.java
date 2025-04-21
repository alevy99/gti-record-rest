package ie.gti.asdl.rey.gtirecord.core.service;

import ie.gti.asdl.rey.gtirecord.model.entity.Assignment;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author Andrei Levchenko
 */
@Service
public interface AssignmentService {

    Optional<Assignment> getById(Integer id);

    List<Assignment> getAll();

    List<Assignment> getByGroupId(Integer groupId);

    Optional<Integer> insert(Assignment assignment);

    void update(Assignment assignment);

    void delete(Integer id);

    void deleteByGroupId(Integer groupId);
}
