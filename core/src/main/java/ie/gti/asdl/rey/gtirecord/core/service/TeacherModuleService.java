package ie.gti.asdl.rey.gtirecord.core.service;

import ie.gti.asdl.rey.gtirecord.model.entity.Module;
import ie.gti.asdl.rey.gtirecord.model.entity.Teacher;
import ie.gti.asdl.rey.gtirecord.model.entity.TeacherModule;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Andrei Levchenko
 */
@Service
public interface TeacherModuleService {

    List<TeacherModule> getByGroupId(Integer groupId);

    void insert(Integer teacherPersonId, Integer moduleId);

    void delete(Integer teacherPersonId, Integer moduleId);

}
