package ie.gti.asdl.rey.gtirecord.core.service;

import ie.gti.asdl.rey.gtirecord.model.entity.Group;
import ie.gti.asdl.rey.gtirecord.model.entity.GroupModule;
import ie.gti.asdl.rey.gtirecord.model.entity.Module;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Andrei Levchenko
 */
@Service
public interface GroupModuleService {

    List<GroupModule> getAll();

    List<GroupModule> getByGroupId(Integer groupId);

    Map<Group, List<Module>> getAllGroupedByGroup();

    Optional<Integer> insert(GroupModule groupModule);

    Optional<Integer> insert(Integer groupId, Integer moduleId, Integer teacherPersonId);

    void update(Integer groupId, Integer moduleId, Integer teacherPersonId);

    void delete(Integer id);

    void deleteByGroupIdAndModuleId(Integer groupId, Integer moduleId);

    void deleteByGroupId(Integer groupId);
}
