package ie.gti.asdl.rey.gtirecord.core.service.impl;

import ie.gti.asdl.rey.gtirecord.core.dao.TeacherModuleDao;
import ie.gti.asdl.rey.gtirecord.core.service.TeacherModuleService;
import ie.gti.asdl.rey.gtirecord.model.entity.TeacherModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Andrei Levchenko
 */
@Service
public class TeacherModuleServiceImpl implements TeacherModuleService {

    private final TeacherModuleDao teacherModuleDao;

    @Autowired
    public TeacherModuleServiceImpl(TeacherModuleDao teacherModuleDao) {
        this.teacherModuleDao = teacherModuleDao;
    }


    @Override
    public List<TeacherModule> getByGroupId(Integer groupId) {
        return teacherModuleDao.getByGroupId(groupId);
    }

    @Override
    public void insert(Integer teacherPersonId, Integer moduleId) {
        teacherModuleDao.insert(teacherPersonId, moduleId);
    }

    @Override
    public void delete(Integer teacherPersonId, Integer moduleId) {
        teacherModuleDao.delete(teacherPersonId, moduleId);
    }
}
