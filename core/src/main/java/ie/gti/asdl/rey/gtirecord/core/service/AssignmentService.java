package ie.gti.asdl.rey.gtirecord.core.service;

import ie.gti.asdl.rey.gtirecord.model.entity.Assignment;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service interface providing CRUD operations for {@link Assignment} entities.
 *
 * @author Andrei Levchenko
 */
@Service
public interface AssignmentService {

    /**
     * Retrieves the assignment with the given ID.
     *
     * @param id the ID of the assignment to retrieve
     * @return an {@link Optional} containing the {@link Assignment} if found,
     *         or an empty {@link Optional} if no assignment exists with the given ID
     */
    Optional<Assignment> getById(Integer id);

    /**
     * Retrieves all assignments.
     *
     * @return a list of all {@link Assignment} records
     */
    List<Assignment> getAll();

    /**
     * Retrieves all assignments associated with the given group.
     *
     * @param groupId the ID of the group whose assignments should be retrieved
     * @return a list of {@link Assignment} records associated with the given group
     */
    List<Assignment> getByGroupId(Integer groupId);

    /**
     * Inserts a new assignment.
     *
     * @param assignment the assignment to insert
     * @return an {@link Optional} containing the generated ID of the inserted assignment,
     *         or an empty {@link Optional} if the insert did not produce an ID
     */
    Optional<Integer> insert(Assignment assignment);

    /**
     * Updates an existing assignment.
     *
     * @param assignment the assignment containing updated data
     */
    void update(Assignment assignment);

    /**
     * Deletes the assignment with the given ID.
     *
     * @param id the ID of the assignment to delete
     */
    void delete(Integer id);

    /**
     * Deletes all assignments associated with the given group.
     *
     * @param groupId the ID of the group whose assignments should be deleted
     */
    void deleteByGroupId(Integer groupId);
}
