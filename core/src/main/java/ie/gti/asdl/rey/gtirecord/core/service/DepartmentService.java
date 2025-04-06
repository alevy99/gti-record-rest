package ie.gti.asdl.rey.gtirecord.core.service;

import ie.gti.asdl.rey.gtirecord.model.entity.Department;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface DepartmentService {

    Optional<Department> getById(Integer id);

    List<Department> getAll();

    Optional<Integer> insert(Department department);

    void update(Department department);

    void delete(int id);

}
