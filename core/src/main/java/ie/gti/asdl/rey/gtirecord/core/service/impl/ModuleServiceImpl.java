package ie.gti.asdl.rey.gtirecord.core.service.impl;

import ie.gti.asdl.rey.gtirecord.core.dao.CourseModuleDao;
import ie.gti.asdl.rey.gtirecord.core.dao.ModuleDao;
import ie.gti.asdl.rey.gtirecord.core.service.ModuleService;
import ie.gti.asdl.rey.gtirecord.model.entity.Module;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ModuleServiceImpl implements ModuleService {

    private final ModuleDao moduleDao;

    private final CourseModuleDao courseModuleDao;

    @Autowired
    public ModuleServiceImpl(ModuleDao moduleDao, CourseModuleDao courseModuleDao) {
        this.moduleDao = moduleDao;
        this.courseModuleDao = courseModuleDao;
    }

    @Override
    public Optional<Module> getById(Integer id) {
        if (id == null) return Optional.empty();
        return moduleDao.getById(id);
    }

    @Override
    public List<Module> getByCourseId(Integer courseId) {
        if (courseId == null) return List.of();
        return moduleDao.getByCourseId(courseId);
    }

    @Override
    public List<Module> getByTeacherPersonId(Integer teacherPersonId) {
        return moduleDao.getByTeacherPersonId(teacherPersonId);
    }

    @Override
    public List<Module> getAll() {
        return moduleDao.getAll();
    }

    @Override
    public Optional<Integer> insert(Module module) {
        var moduleOpt = moduleDao.insert(module);
        moduleOpt.ifPresent(module::setId);
        return moduleOpt;
    }

    @Override
    public void update(Module module) {
        moduleDao.update(module);
    }

    @Transactional
    @Override
    public void delete(int id) {
        courseModuleDao.deleteByModuleId(id);
        moduleDao.delete(id);
    }
}
