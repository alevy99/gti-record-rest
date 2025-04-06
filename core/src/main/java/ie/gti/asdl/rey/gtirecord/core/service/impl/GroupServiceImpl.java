package ie.gti.asdl.rey.gtirecord.core.service.impl;

import ie.gti.asdl.rey.gtirecord.core.dao.GroupDao;
import ie.gti.asdl.rey.gtirecord.core.service.GroupService;
import ie.gti.asdl.rey.gtirecord.model.entity.Course;
import ie.gti.asdl.rey.gtirecord.model.entity.Department;
import ie.gti.asdl.rey.gtirecord.model.entity.Group;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Andrei Levchenko
 */
@Service
public class GroupServiceImpl implements GroupService {

    private final GroupDao groupDao;

    @Autowired
    public GroupServiceImpl(GroupDao groupDao) {
        this.groupDao = groupDao;
    }

    @Override
    public Optional<Group> getById(Integer id) {
        return Optional.empty();
    }

    @Override
    public List<Group> getAll() {
        return List.of();
    }

    @Override
    public Map<Course, List<Group>> getAllGroupedByCourse() {
        return groupDao.getAllGroupedByCourse();
    }

    @Override
    public Optional<Integer> insert(Group group) {
        return Optional.empty();
    }

    @Override
    public void update(Group group) {

    }

    @Override
    public void delete(Integer id) {

    }
}
