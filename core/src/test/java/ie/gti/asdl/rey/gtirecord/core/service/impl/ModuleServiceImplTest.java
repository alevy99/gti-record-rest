package ie.gti.asdl.rey.gtirecord.core.service.impl;

import ie.gti.asdl.rey.gtirecord.core.dao.CourseModuleDao;
import ie.gti.asdl.rey.gtirecord.core.dao.ModuleDao;
import ie.gti.asdl.rey.gtirecord.model.entity.Module;
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
 * Unit tests for ModuleServiceImpl.
 * All collaborators are mocked with Mockito; no Spring context is loaded.
 */
@ExtendWith(MockitoExtension.class)
class ModuleServiceImplTest {

    @Mock
    private ModuleDao moduleDao;
    @Mock
    private CourseModuleDao courseModuleDao;

    @InjectMocks
    private ModuleServiceImpl moduleService;

    // Get by ID delegates to DAO.
    @Test
    void getById_delegatesToDao() {
        Module module = new Module(1, "Java", "JV1");
        when(moduleDao.getById(1)).thenReturn(Optional.of(module));

        assertEquals(Optional.of(module), moduleService.getById(1));
    }

    // Get by ID returns empty without calling DAO when ID is null.
    @Test
    void getById_returnsEmpty_withoutCallingDao_whenIdIsNull() {
        Optional<Module> result = moduleService.getById(null);

        assertTrue(result.isEmpty());
        verifyNoInteractions(moduleDao);
    }

    // Get by course ID delegates to DAO.
    @Test
    void getByCourseId_delegatesToDao() {
        List<Module> modules = List.of(new Module(1, "Java", "JV1"));
        when(moduleDao.getByCourseId(1)).thenReturn(modules);

        assertEquals(modules, moduleService.getByCourseId(1));
    }

    // Get by course ID returns empty list without calling DAO when course ID is null.
    @Test
    void getByCourseId_returnsEmptyList_withoutCallingDao_whenCourseIdIsNull() {
        List<Module> result = moduleService.getByCourseId(null);

        assertTrue(result.isEmpty());
        verifyNoInteractions(moduleDao);
    }

    // Get by group ID delegates to DAO.
    @Test
    void getByGroupId_delegatesToDao() {
        List<Module> modules = List.of(new Module(1, "Java", "JV1"));
        when(moduleDao.getByGroupId(5)).thenReturn(modules);

        assertEquals(modules, moduleService.getByGroupId(5));
    }

    // Get by teacher person ID delegates to DAO.
    @Test
    void getByTeacherPersonId_delegatesToDao() {
        List<Module> modules = List.of(new Module(1, "Java", "JV1"));
        when(moduleDao.getByTeacherPersonId(5)).thenReturn(modules);

        assertEquals(modules, moduleService.getByTeacherPersonId(5));
    }

    // Get all delegates to DAO.
    @Test
    void getAll_delegatesToDao() {
        List<Module> modules = List.of(new Module(1, "Java", "JV1"));
        when(moduleDao.getAll()).thenReturn(modules);

        assertEquals(modules, moduleService.getAll());
    }

    // Insert sets generated ID on module.
    @Test
    void insert_setsGeneratedId_onModule() {
        Module module = new Module(null, "Java", "JV1");
        when(moduleDao.insert(module)).thenReturn(Optional.of(4));

        Optional<Integer> result = moduleService.insert(module);

        assertEquals(Optional.of(4), result);
        assertEquals(4, module.getId());
    }

    // Update delegates to DAO.
    @Test
    void update_delegatesToDao() {
        Module module = new Module(1, "Java", "JV1");

        moduleService.update(module);

        verify(moduleDao).update(module);
    }

    // Delete removes course modules first then module.
    @Test
    void delete_removesCourseModulesFirst_thenModule() {
        moduleService.delete(1);

        var inOrder = inOrder(courseModuleDao, moduleDao);
        inOrder.verify(courseModuleDao).deleteByModuleId(1);
        inOrder.verify(moduleDao).delete(1);
    }
}
