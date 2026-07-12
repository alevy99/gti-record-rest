package ie.gti.asdl.rey.gtirecord.core.service.impl;

import ie.gti.asdl.rey.gtirecord.core.dao.CourseDao;
import ie.gti.asdl.rey.gtirecord.core.dao.CourseModuleDao;
import ie.gti.asdl.rey.gtirecord.model.entity.Course;
import ie.gti.asdl.rey.gtirecord.model.entity.Department;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CourseServiceImpl.
 * All collaborators are mocked with Mockito; no Spring context is loaded.
 */
@ExtendWith(MockitoExtension.class)
class CourseServiceImplTest {

    @Mock
    private CourseDao courseDao;
    @Mock
    private CourseModuleDao courseModuleDao;

    @InjectMocks
    private CourseServiceImpl courseService;

    // Get by ID delegates to DAO.
    @Test
    void getById_delegatesToDao() {
        Course course = new Course(1, "Software Development", "SD", null, null, null);
        when(courseDao.getById(1)).thenReturn(Optional.of(course));

        assertEquals(Optional.of(course), courseService.getById(1));
    }

    // Get all delegates to DAO.
    @Test
    void getAll_delegatesToDao() {
        List<Course> courses = List.of(new Course(1, "SD", "SD", null, null, null));
        when(courseDao.getAll()).thenReturn(courses);

        assertEquals(courses, courseService.getAll());
    }

    // Get all grouped by department delegates to DAO.
    @Test
    void getAllGroupedByDepartment_delegatesToDao() {
        Department department = new Department(1, "Computing");
        Map<Department, List<Course>> grouped = Map.of(department, List.of());
        when(courseDao.getAllGroupedByDepartment()).thenReturn(grouped);

        assertEquals(grouped, courseService.getAllGroupedByDepartment());
    }

    // Insert sets generated ID on course.
    @Test
    void insert_setsGeneratedId_onCourse() {
        Course course = new Course(null, "SD", "SD", null, null, null);
        when(courseDao.insert(course)).thenReturn(Optional.of(3));

        Optional<Integer> result = courseService.insert(course);

        assertEquals(Optional.of(3), result);
        assertEquals(3, course.getId());
    }

    // Insert does not set ID when DAO returns empty.
    @Test
    void insert_doesNotSetId_whenDaoReturnsEmpty() {
        Course course = new Course(null, "SD", "SD", null, null, null);
        when(courseDao.insert(course)).thenReturn(Optional.empty());

        Optional<Integer> result = courseService.insert(course);

        assertTrue(result.isEmpty());
        assertNull(course.getId());
    }

    // Update delegates to DAO.
    @Test
    void update_delegatesToDao() {
        Course course = new Course(1, "SD", "SD", null, null, null);

        courseService.update(course);

        verify(courseDao).update(course);
    }

    // Delete removes course modules first then course.
    @Test
    void delete_removesCourseModulesFirst_thenCourse() {
        courseService.delete(1);

        var inOrder = inOrder(courseModuleDao, courseDao);
        inOrder.verify(courseModuleDao).deleteByCourseId(1);
        inOrder.verify(courseDao).delete(1);
    }
}
