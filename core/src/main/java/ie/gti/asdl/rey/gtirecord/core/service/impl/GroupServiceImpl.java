package ie.gti.asdl.rey.gtirecord.core.service.impl;

import ie.gti.asdl.rey.gtirecord.core.dao.CourseDao;
import ie.gti.asdl.rey.gtirecord.core.dao.GroupDao;
import ie.gti.asdl.rey.gtirecord.core.dao.GroupModuleDao;
import ie.gti.asdl.rey.gtirecord.core.dao.ModuleDao;
import ie.gti.asdl.rey.gtirecord.core.service.GroupModuleService;
import ie.gti.asdl.rey.gtirecord.core.service.GroupService;
import ie.gti.asdl.rey.gtirecord.model.entity.Course;
import ie.gti.asdl.rey.gtirecord.model.entity.Department;
import ie.gti.asdl.rey.gtirecord.model.entity.Group;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Andrei Levchenko
 */
@Service
public class GroupServiceImpl implements GroupService {

    private final GroupDao groupDao;

    private final CourseDao courseDao;

    private final ModuleDao moduleDao;

    private final GroupModuleDao groupModuleDao;

    private final GroupModuleService groupModuleService;

    @Autowired
    public GroupServiceImpl(GroupDao groupDao, CourseDao courseDao, ModuleDao moduleDao, GroupModuleDao groupModuleDao, GroupModuleService groupModuleService) {
        this.groupDao = groupDao;
        this.courseDao = courseDao;
        this.moduleDao = moduleDao;
        this.groupModuleDao = groupModuleDao;
        this.groupModuleService = groupModuleService;
    }

    @Override
    public Optional<Group> getById(Integer id) {
        return groupDao.getById(id);
    }

    @Override
    public List<Group> getAll() {
        return groupDao.getAll();
    }

    @Override
    public List<Group> getAllWithFilter(Group filter) {
        return groupDao.getAllWithFilter(filter);
    }

    @Override
    public Map<Course, List<Group>> getAllGroupedByCourse() {
        return groupDao.getAllGroupedByCourse();
    }

    @Transactional
    @Override
    public Optional<Integer> insert(Group group) {
        Optional<Integer> newGroupIdOpt = groupDao.insert(group);
        newGroupIdOpt.ifPresent(newGroupId -> {
            moduleDao.getByCourseId(group.getCourse().getId()).forEach(module -> {
                groupModuleDao.insert(newGroupId, module.getId(), null);
            });
        });
        return newGroupIdOpt;
    }

    @Transactional
    @Override
    public void update(Group group) {
        groupDao.getById(group.getId()).ifPresent(groupDb -> {
            if (!Objects.equals(groupDb.getCourse().getId(), group.getCourse().getId())) {
                groupModuleService.deleteByGroupId(groupDb.getId());
                moduleDao.getByCourseId(group.getCourse().getId()).forEach(module -> {
                    groupModuleDao.insert(group.getId(), module.getId(), null);
                });
            }
            groupDao.update(group);
        });
    }

    @Transactional
    @Override
    public void delete(Integer id) {
        groupModuleService.deleteByGroupId(id);
        groupDao.delete(id);
    }
}
