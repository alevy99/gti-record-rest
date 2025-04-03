package ie.gti.asdl.rey.gtirecord.core.dao;

import ie.gti.asdl.rey.gtirecord.model.entity.Department;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentDao {

    Optional<Department> getById(Integer id);

    List<Department> getAll();

    Optional<Integer> insert(Department department);

    void update(Department department);

    void delete(int id);

}
