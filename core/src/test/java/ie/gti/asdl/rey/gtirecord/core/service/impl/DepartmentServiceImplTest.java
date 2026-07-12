package ie.gti.asdl.rey.gtirecord.core.service.impl;

import ie.gti.asdl.rey.gtirecord.core.dao.DepartmentDao;
import ie.gti.asdl.rey.gtirecord.model.entity.Department;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DepartmentServiceImpl.
 * All collaborators are mocked with Mockito; no Spring context is loaded.
 */
@ExtendWith(MockitoExtension.class)
class DepartmentServiceImplTest {

    @Mock
    private DepartmentDao departmentDao;

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    // Get by ID delegates to DAO.
    @Test
    void getById_delegatesToDao() {
        Department department = new Department(1, "Computing");
        when(departmentDao.getById(1)).thenReturn(Optional.of(department));

        Optional<Department> result = departmentService.getById(1);

        assertEquals(Optional.of(department), result);
        verify(departmentDao).getById(1);
    }

    // Get all delegates to DAO.
    @Test
    void getAll_delegatesToDao() {
        List<Department> departments = List.of(new Department(1, "Computing"), new Department(2, "Business"));
        when(departmentDao.getAll()).thenReturn(departments);

        List<Department> result = departmentService.getAll();

        assertEquals(departments, result);
        verify(departmentDao).getAll();
    }

    // Insert sets generated ID on department when DAO returns ID.
    @Test
    void insert_setsGeneratedId_onDepartment_whenDaoReturnsId() {
        Department department = new Department(null, "Computing");
        when(departmentDao.insert(department)).thenReturn(Optional.of(7));

        Optional<Integer> result = departmentService.insert(department);

        assertEquals(Optional.of(7), result);
        assertEquals(7, department.getId());
    }

    // Insert does not set ID when DAO returns empty.
    @Test
    void insert_doesNotSetId_whenDaoReturnsEmpty() {
        Department department = new Department(null, "Computing");
        when(departmentDao.insert(department)).thenReturn(Optional.empty());

        Optional<Integer> result = departmentService.insert(department);

        assertTrue(result.isEmpty());
        assertNull(department.getId());
    }

    // Update delegates to DAO.
    @Test
    void update_delegatesToDao() {
        Department department = new Department(1, "Computing");

        departmentService.update(department);

        verify(departmentDao).update(department);
    }

    // Delete delegates to DAO.
    @Test
    void delete_delegatesToDao() {
        departmentService.delete(5);

        verify(departmentDao).delete(5);
    }
}
