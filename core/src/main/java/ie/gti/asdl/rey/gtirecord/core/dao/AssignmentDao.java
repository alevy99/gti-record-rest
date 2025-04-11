package ie.gti.asdl.rey.gtirecord.core.dao;

import ie.gti.asdl.rey.gtirecord.model.entity.Assignment;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author Andrei Levchenko
 */
@Repository
public interface AssignmentDao {

    Optional<Assignment> getById(Integer id);

    List<Assignment> getByGroupModule(Integer groupModuleId);

    List<Assignment> getAll();

    Optional<Integer> insert(Assignment assignment);

    void update(Assignment assignment);

    void delete(Integer id);
}
