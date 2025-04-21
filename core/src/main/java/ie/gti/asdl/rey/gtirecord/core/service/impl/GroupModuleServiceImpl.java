package ie.gti.asdl.rey.gtirecord.core.service.impl;

import ie.gti.asdl.rey.gtirecord.core.dao.GroupModuleDao;
import ie.gti.asdl.rey.gtirecord.core.service.AssignmentService;
import ie.gti.asdl.rey.gtirecord.core.service.GroupModuleService;
import ie.gti.asdl.rey.gtirecord.model.entity.Group;
import ie.gti.asdl.rey.gtirecord.model.entity.GroupModule;
import ie.gti.asdl.rey.gtirecord.model.entity.Module;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Andrei Levchenko
 */
@Service
public class GroupModuleServiceImpl implements GroupModuleService {

    private final GroupModuleDao groupModuleDao;

    private final AssignmentService assignmentService;

    @Autowired
    public GroupModuleServiceImpl(GroupModuleDao groupModuleDao, AssignmentService assignmentService) {
        this.groupModuleDao = groupModuleDao;
        this.assignmentService = assignmentService;
    }

    @Override
    public List<GroupModule> getAll() {
        return groupModuleDao.getAll();
    }

    @Override
    public List<GroupModule> getByGroupId(Integer groupId) {
        return groupModuleDao.getByGroupId(groupId);
    }

    @Override
    public Map<Group, List<Module>> getAllGroupedByGroup() {
        List<GroupModule> groupModules = groupModuleDao.getAll();
        return groupModules
                .stream()
                .collect(Collectors.groupingBy(GroupModule::getGroup,
                        Collectors.mapping(GroupModule::getModule, Collectors.toList())));
    }

    @Override
    public Optional<Integer> insert(GroupModule groupModule) {
        return groupModuleDao.insert(groupModule);
    }

    @Override
    public Optional<Integer> insert(Integer groupId, Integer moduleId, Integer teacherPersonId) {
        return groupModuleDao.insert(groupId, moduleId, teacherPersonId);
    }

    @Override
    public void update(Integer groupId, Integer moduleId, Integer teacherPersonId) {
        groupModuleDao.update(groupId, moduleId, teacherPersonId);
    }

    @Override
    public void delete(Integer id) {
        groupModuleDao.delete(id);
    }

    @Override
    public void delete(Integer groupId, Integer moduleId) {
        groupModuleDao.delete(groupId, moduleId);
    }

    @Transactional
    @Override
    public void deleteByGroupId(Integer groupId) {
        assignmentService.deleteByGroupId(groupId);
        groupModuleDao.deleteByGroupId(groupId);
    }
}
