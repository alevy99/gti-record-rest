package ie.gti.asdl.rey.gtirecord.core.dao;

import org.springframework.stereotype.Repository;

/**
 * @author Andrei Levchenko
 */
@Repository
public interface TeacherModuleDao {


    void insert(Integer teacherPersonId, Integer moduleId);

    void delete(Integer teacherPersonId, Integer moduleId);
}
