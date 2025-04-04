package ie.gti.asdl.rey.gtirecord.core.service;

import org.springframework.stereotype.Service;

/**
 * @author Andrei Levchenko
 */
@Service
public interface TeacherModuleService {

    void insert(Integer teacherPersonId, Integer moduleId);

    void delete(Integer teacherPersonId, Integer moduleId);

}
