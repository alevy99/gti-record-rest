package ie.gti.asdl.rey.gtirecord.core.service.impl;

import ie.gti.asdl.rey.gtirecord.core.dao.GroupModuleDao;
import ie.gti.asdl.rey.gtirecord.core.dao.TeacherModuleDao;
import ie.gti.asdl.rey.gtirecord.model.entity.TeacherModule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for TeacherModuleServiceImpl.
 * All collaborators are mocked with Mockito; no Spring context is loaded.
 */
@ExtendWith(MockitoExtension.class)
class TeacherModuleServiceImplTest {

    @Mock
    private TeacherModuleDao teacherModuleDao;
    @Mock
    private GroupModuleDao groupModuleDao;

    @InjectMocks
    private TeacherModuleServiceImpl teacherModuleService;

    // Get by group ID delegates to DAO.
    @Test
    void getByGroupId_delegatesToDao() {
        List<TeacherModule> teacherModules = List.of();
        when(teacherModuleDao.getByGroupId(1)).thenReturn(teacherModules);

        assertEquals(teacherModules, teacherModuleService.getByGroupId(1));
    }

    // Insert delegates to DAO.
    @Test
    void insert_delegatesToDao() {
        teacherModuleService.insert(1, 2);

        verify(teacherModuleDao).insert(1, 2);
    }

    // Delete clears teacher from group module then deletes teacher module.
    @Test
    void delete_clearsTeacherFromGroupModule_thenDeletesTeacherModule() {
        teacherModuleService.delete(1, 2);

        var inOrder = org.mockito.Mockito.inOrder(groupModuleDao, teacherModuleDao);
        inOrder.verify(groupModuleDao).updateTeacherByModuleId(null, 2);
        inOrder.verify(teacherModuleDao).delete(1, 2);
    }
}
