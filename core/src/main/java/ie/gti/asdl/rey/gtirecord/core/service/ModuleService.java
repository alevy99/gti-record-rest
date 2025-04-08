package ie.gti.asdl.rey.gtirecord.core.service;

import ie.gti.asdl.rey.gtirecord.model.entity.Module;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface ModuleService {

    Optional<Module> getById(Integer id);

    List<Module> getByCourseId(Integer courseId);

    List<Module> getByGroupId(Integer groupId);

    List<Module> getByTeacherPersonId(Integer teacherPersonId);

    List<Module> getAll();

    Optional<Integer> insert(Module module);

    void update(Module module);

    void delete(int id);

}
