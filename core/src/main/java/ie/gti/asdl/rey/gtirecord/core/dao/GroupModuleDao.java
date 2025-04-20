package ie.gti.asdl.rey.gtirecord.core.dao;

import ie.gti.asdl.rey.gtirecord.model.entity.GroupModule;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author Andrei Levchenko
 */
@Repository
public interface GroupModuleDao {

    List<GroupModule> getAll();

    List<GroupModule> getByGroupId(Integer groupId);

    Optional<Integer> insert(GroupModule groupModule);

    Optional<Integer> insert(Integer groupId, Integer moduleId, Integer teacherPersonId);

    void update(Integer groupId, Integer moduleId, Integer teacherPersonId);

    void delete(Integer id);

    void delete(Integer groupId, Integer moduleId);
}
