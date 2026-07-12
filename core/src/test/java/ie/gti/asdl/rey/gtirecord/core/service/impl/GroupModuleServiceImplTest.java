package ie.gti.asdl.rey.gtirecord.core.service.impl;

import ie.gti.asdl.rey.gtirecord.core.dao.GroupModuleDao;
import ie.gti.asdl.rey.gtirecord.core.service.AssignmentService;
import ie.gti.asdl.rey.gtirecord.model.entity.Group;
import ie.gti.asdl.rey.gtirecord.model.entity.GroupModule;
import ie.gti.asdl.rey.gtirecord.model.entity.Module;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Unit tests for GroupModuleServiceImpl.
 * All collaborators are mocked with Mockito; no Spring context is loaded.
 */
@ExtendWith(MockitoExtension.class)
class GroupModuleServiceImplTest {

    @Mock
    private GroupModuleDao groupModuleDao;
    @Mock
    private AssignmentService assignmentService;

    @InjectMocks
    private GroupModuleServiceImpl groupModuleService;

    // Get all delegates to DAO.
    @Test
    void getAll_delegatesToDao() {
        List<GroupModule> groupModules = List.of(new GroupModule(1, null, null, null));
        when(groupModuleDao.getAll()).thenReturn(groupModules);

        assertEquals(groupModules, groupModuleService.getAll());
    }

    // Get by group ID delegates to DAO.
    @Test
    void getByGroupId_delegatesToDao() {
        List<GroupModule> groupModules = List.of(new GroupModule(1, null, null, null));
        when(groupModuleDao.getByGroupId(5)).thenReturn(groupModules);

        assertEquals(groupModules, groupModuleService.getByGroupId(5));
    }

    // Get all grouped by group groups modules by group.
    @Test
    void getAllGroupedByGroup_groupsModulesByGroup() {
        Group group1 = new Group(1, "SD1", "SD1", null);
        Group group2 = new Group(2, "SD2", "SD2", null);
        Module module1 = new Module(10, "Java", "JV1");
        Module module2 = new Module(11, "Databases", "DB1");
        GroupModule gm1 = new GroupModule(100, group1, module1, null);
        GroupModule gm2 = new GroupModule(101, group1, module2, null);
        GroupModule gm3 = new GroupModule(102, group2, module1, null);
        when(groupModuleDao.getAll()).thenReturn(List.of(gm1, gm2, gm3));

        Map<Group, List<Module>> result = groupModuleService.getAllGroupedByGroup();

        assertEquals(List.of(module1, module2), result.get(group1));
        assertEquals(List.of(module1), result.get(group2));
    }

    // Insert group module entity delegates to DAO.
    @Test
    void insert_groupModuleEntity_delegatesToDao() {
        GroupModule groupModule = new GroupModule(null, null, null, null);
        when(groupModuleDao.insert(groupModule)).thenReturn(Optional.of(1));

        assertEquals(Optional.of(1), groupModuleService.insert(groupModule));
    }

    // Insert by ids delegates to DAO.
    @Test
    void insert_byIds_delegatesToDao() {
        when(groupModuleDao.insert(1, 2, 3)).thenReturn(Optional.of(9));

        assertEquals(Optional.of(9), groupModuleService.insert(1, 2, 3));
    }

    // Update delegates to DAO.
    @Test
    void update_delegatesToDao() {
        groupModuleService.update(1, 2, 3);

        verify(groupModuleDao).update(1, 2, 3);
    }

    // Delete delegates to DAO.
    @Test
    void delete_delegatesToDao() {
        groupModuleService.delete(5);

        verify(groupModuleDao).delete(5);
    }

    // Delete by group ID and module ID delegates to DAO.
    @Test
    void deleteByGroupIdAndModuleId_delegatesToDao() {
        groupModuleService.deleteByGroupIdAndModuleId(1, 2);

        verify(groupModuleDao).deleteByGroupIdAndModuleId(1, 2);
    }

    // Delete by group ID deletes assignments first then group modules.
    @Test
    void deleteByGroupId_deletesAssignmentsFirst_thenGroupModules() {
        groupModuleService.deleteByGroupId(1);

        var inOrder = inOrder(assignmentService, groupModuleDao);
        inOrder.verify(assignmentService).deleteByGroupId(1);
        inOrder.verify(groupModuleDao).deleteByGroupId(1);
    }
}
