package ie.gti.asdl.rey.gtirecord.core.service.impl;

import ie.gti.asdl.rey.gtirecord.core.dao.ModuleDao;
import ie.gti.asdl.rey.gtirecord.core.service.ModuleService;
import ie.gti.asdl.rey.gtirecord.model.entity.Module;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ModuleServiceImpl implements ModuleService {

    private final ModuleDao moduleDao;

    @Autowired
    public ModuleServiceImpl(ModuleDao moduleDao) {
        this.moduleDao = moduleDao;
    }

    @Override
    public Optional<Module> getById(int id) {
        return moduleDao.getById(id);
    }

    @Override
    public List<Module> getByCourseId(int courseId) {
        return moduleDao.getByCourseId(courseId);
    }

    @Override
    public List<Module> getAll() {
        return moduleDao.getAll();
    }

    @Override
    public Optional<Integer> insert(Module module) {
        return moduleDao.insert(module);
    }

    @Override
    public void update(Module module) {
        moduleDao.update(module);
    }

    @Override
    public void delete(int id) {
        moduleDao.delete(id);
    }
}
