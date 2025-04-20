package ie.gti.asdl.rey.gtirecord.core.dao;

import ie.gti.asdl.rey.gtirecord.model.entity.Group;
import ie.gti.asdl.rey.gtirecord.model.entity.Module;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface ModuleDao {

    Optional<Module> getById(Integer id);

    List<Module> getByCourseId(Integer courseId);

    List<Module> getByGroupId(Integer groupId);

    List<Module> getByTeacherPersonId(Integer teacherPersonId);

    List<Module> getAll();

    Optional<Integer> insert(Module module);

    void update(Module module);

    void delete(int id);
}
