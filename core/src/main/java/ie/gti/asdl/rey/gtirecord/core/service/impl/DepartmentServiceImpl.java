package ie.gti.asdl.rey.gtirecord.core.service.impl;

import ie.gti.asdl.rey.gtirecord.core.dao.DepartmentDao;
import ie.gti.asdl.rey.gtirecord.core.service.DepartmentService;
import ie.gti.asdl.rey.gtirecord.model.entity.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentDao departmentDao;

    @Autowired
    public DepartmentServiceImpl(DepartmentDao departmentDao) {
        this.departmentDao = departmentDao;
    }

    @Override
    public Optional<Department> getById(int id) {
        return departmentDao.getById(id);
    }

    @Override
    public List<Department> getAll() {
        return departmentDao.getAll();
    }

    @Override
    public Optional<Integer> insert(Department department) {
        var departmentOpt = departmentDao.insert(department);
        departmentOpt.ifPresent(department::setId);
        return departmentOpt;
    }

    @Override
    public void update(Department department) {
        departmentDao.update(department);
    }

    @Override
    public void delete(int id) {
        departmentDao.delete(id);
    }
}
