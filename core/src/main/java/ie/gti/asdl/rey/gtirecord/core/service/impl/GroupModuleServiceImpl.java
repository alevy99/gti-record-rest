package ie.gti.asdl.rey.gtirecord.core.service.impl;

import ie.gti.asdl.rey.gtirecord.core.dao.GroupModuleDao;
import ie.gti.asdl.rey.gtirecord.core.service.GroupModuleService;
import ie.gti.asdl.rey.gtirecord.model.entity.GroupModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author Andrei Levchenko
 */
@Service
public class GroupModuleServiceImpl implements GroupModuleService {

    private final GroupModuleDao groupModuleDao;

    @Autowired
    public GroupModuleServiceImpl(GroupModuleDao groupModuleDao) {
        this.groupModuleDao = groupModuleDao;
    }

    @Override
    public List<GroupModule> getByGroupId(Integer groupId) {
        return groupModuleDao.getByGroupId(groupId);
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
}
