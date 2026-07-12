package ie.gti.asdl.rey.gtirecord.core.service.impl;

import ie.gti.asdl.rey.gtirecord.core.dao.CourseDao;
import ie.gti.asdl.rey.gtirecord.core.dao.GroupDao;
import ie.gti.asdl.rey.gtirecord.core.dao.GroupModuleDao;
import ie.gti.asdl.rey.gtirecord.core.dao.ModuleDao;
import ie.gti.asdl.rey.gtirecord.core.service.GroupModuleService;
import ie.gti.asdl.rey.gtirecord.model.entity.Course;
import ie.gti.asdl.rey.gtirecord.model.entity.Group;
import ie.gti.asdl.rey.gtirecord.model.entity.Module;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Unit tests for GroupServiceImpl.
 * All collaborators are mocked with Mockito; no Spring context is loaded.
 */
@ExtendWith(MockitoExtension.class)
class GroupServiceImplTest {

    @Mock
    private GroupDao groupDao;
    @Mock
    private CourseDao courseDao;
    @Mock
    private ModuleDao moduleDao;
    @Mock
    private GroupModuleDao groupModuleDao;
    @Mock
    private GroupModuleService groupModuleService;

    @InjectMocks
    private GroupServiceImpl groupService;

    // Insert creates group module for each module of course with no teacher.
    @Test
    void insert_createsGroupModuleForEachModuleOfCourse_withNoTeacher() {
        Course course = new Course(1, "SD", "SD", null, null, null);
        Group group = new Group(null, "SD1", "SD1", course);
        Module module1 = new Module(10, "Java", "JV1");
        Module module2 = new Module(11, "DB", "DB1");
        when(groupDao.insert(group)).thenReturn(Optional.of(100));
        when(moduleDao.getByCourseId(1)).thenReturn(List.of(module1, module2));

        Optional<Integer> result = groupService.insert(group);

        assertEquals(Optional.of(100), result);
        verify(groupModuleDao).insert(100, 10, null);
        verify(groupModuleDao).insert(100, 11, null);
    }

    // Insert does not add group modules when DAO returns empty.
    @Test
    void insert_doesNotAddGroupModules_whenDaoReturnsEmpty() {
        Course course = new Course(1, "SD", "SD", null, null, null);
        Group group = new Group(null, "SD1", "SD1", course);
        when(groupDao.insert(group)).thenReturn(Optional.empty());

        Optional<Integer> result = groupService.insert(group);

        assertEquals(Optional.empty(), result);
        verifyNoInteractions(moduleDao, groupModuleDao);
    }

    // Update reassigns group modules when course changed.
    @Test
    void update_reassignsGroupModules_whenCourseChanged() {
        Course oldCourse = new Course(1, "SD", "SD", null, null, null);
        Course newCourse = new Course(2, "Business", "BUS", null, null, null);
        Group groupInDb = new Group(50, "SD1", "SD1", oldCourse);
        Group updatedGroup = new Group(50, "SD1", "SD1", newCourse);
        Module module1 = new Module(10, "Accounting", "ACC1");
        when(groupDao.getById(50)).thenReturn(Optional.of(groupInDb));
        when(moduleDao.getByCourseId(2)).thenReturn(List.of(module1));

        groupService.update(updatedGroup);

        verify(groupModuleService).deleteByGroupId(50);
        verify(groupModuleDao).insert(50, 10, null);
        verify(groupDao).update(updatedGroup);
    }

    // Update does not touch group modules when course unchanged.
    @Test
    void update_doesNotTouchGroupModules_whenCourseUnchanged() {
        Course course = new Course(1, "SD", "SD", null, null, null);
        Group groupInDb = new Group(50, "SD1", "SD1", course);
        Group updatedGroup = new Group(50, "SD1-renamed", "SD1", course);
        when(groupDao.getById(50)).thenReturn(Optional.of(groupInDb));

        groupService.update(updatedGroup);

        verifyNoInteractions(groupModuleService, moduleDao, groupModuleDao);
        verify(groupDao).update(updatedGroup);
    }

    // Update does nothing when group not found.
    @Test
    void update_doesNothing_whenGroupNotFound() {
        Group updatedGroup = new Group(50, "SD1", "SD1", null);
        when(groupDao.getById(50)).thenReturn(Optional.empty());

        groupService.update(updatedGroup);

        verify(groupDao, never()).update(any());
        verifyNoInteractions(groupModuleService, moduleDao, groupModuleDao);
    }

    // Delete deletes group modules then group.
    @Test
    void delete_deletesGroupModules_thenGroup() {
        groupService.delete(1);

        var inOrder = inOrder(groupModuleService, groupDao);
        inOrder.verify(groupModuleService).deleteByGroupId(1);
        inOrder.verify(groupDao).delete(1);
    }
}
