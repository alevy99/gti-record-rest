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

    void updateTeacherByModuleId(Integer teacherPersonId, Integer moduleId);

    void delete(Integer id);

    void deleteByGroupIdAndModuleId(Integer groupId, Integer moduleId);

    void deleteByModuleIdAndTeacherPersonId(Integer moduleId, Integer teacherPersonId);

    void deleteByGroupId(Integer groupId);

    void deleteByModuleId(Integer moduleId);
}
