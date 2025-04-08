package ie.gti.asdl.rey.gtirecord.core.dao;

import ie.gti.asdl.rey.gtirecord.model.entity.TeacherModule;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Andrei Levchenko
 */
@Repository
public interface TeacherModuleDao {

    List<TeacherModule> getByGroupId(Integer groupId);

    void insert(Integer teacherPersonId, Integer moduleId);

    void delete(Integer teacherPersonId, Integer moduleId);
}
