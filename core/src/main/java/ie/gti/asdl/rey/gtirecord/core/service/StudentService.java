package ie.gti.asdl.rey.gtirecord.core.service;

import ie.gti.asdl.rey.gtirecord.model.entity.Student;
import ie.gti.asdl.rey.gtirecord.model.entity.User;
import ie.gti.asdl.rey.gtirecord.model.entity.add.StudentAssignmentStats;
import ie.gti.asdl.rey.gtirecord.model.validation.OnUpdate;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * @author Andrei Levchenko
 */
@Service
public interface StudentService {

    List<Student> getAll();

    Optional<Student> getByPersonId(Integer personId);

    List<Student> getByGroupId(@NotNull(groups = OnUpdate.class) Integer groupId);

    Optional<Integer> insert(Student student);

    Optional<Integer> insertWithUser(Student student, User user);

    void update(Student student);

    void updateStudentAndAssignments(Student student);

    void updateWithUser(Student student, User user);

    void save(Student student);

    Optional<Integer> saveWithUser(Student student, User user);

    void delete(Student student);

}
