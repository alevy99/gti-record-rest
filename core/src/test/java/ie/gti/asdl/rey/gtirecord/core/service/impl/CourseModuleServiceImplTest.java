package ie.gti.asdl.rey.gtirecord.core.service.impl;

import ie.gti.asdl.rey.gtirecord.core.dao.CourseModuleDao;
import ie.gti.asdl.rey.gtirecord.core.dao.GroupDao;
import ie.gti.asdl.rey.gtirecord.core.dao.GroupModuleDao;
import ie.gti.asdl.rey.gtirecord.model.entity.Group;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Unit tests for CourseModuleServiceImpl.
 * All collaborators are mocked with Mockito; no Spring context is loaded.
 */
@ExtendWith(MockitoExtension.class)
class CourseModuleServiceImplTest {

    @Mock
    private CourseModuleDao courseModuleDao;
    @Mock
    private GroupModuleDao groupModuleDao;
    @Mock
    private GroupDao groupDao;

    @InjectMocks
    private CourseModuleServiceImpl courseModuleService;

    // Insert adds course module and creates group module for each group of course with no teacher.
    @Test
    void insert_addsCourseModule_andCreatesGroupModule_forEachGroupOfCourse_withNoTeacher() {
        Group group1 = new Group(10, "SD1", "SD1-Code", null);
        Group group2 = new Group(11, "SD2", "SD2-Code", null);
        when(groupDao.getByCourseId(1)).thenReturn(List.of(group1, group2));

        courseModuleService.insert(1, 100);

        verify(courseModuleDao).insert(1, 100);
        verify(groupModuleDao).insert(10, 100, null);
        verify(groupModuleDao).insert(11, 100, null);
        verifyNoMoreInteractions(groupModuleDao);
    }

    // Insert only inserts course module when course has no groups.
    @Test
    void insert_onlyInsertsCourseModule_whenCourseHasNoGroups() {
        when(groupDao.getByCourseId(1)).thenReturn(List.of());

        courseModuleService.insert(1, 100);

        verify(courseModuleDao).insert(1, 100);
        verifyNoInteractions(groupModuleDao);
    }

    // Delete removes course module and group module for each group of course.
    @Test
    void delete_removesCourseModule_andGroupModuleForEachGroupOfCourse() {
        Group group1 = new Group(10, "SD1", "SD1-Code", null);
        when(groupDao.getByCourseId(1)).thenReturn(List.of(group1));

        courseModuleService.delete(1, 100);

        verify(courseModuleDao).delete(1, 100);
        verify(groupModuleDao).deleteByGroupIdAndModuleId(10, 100);
    }
}
