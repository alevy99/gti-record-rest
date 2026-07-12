package ie.gti.asdl.rey.gtirecord.model.annotation;

import ie.gti.asdl.rey.gtirecord.model.entity.Assignment;
import ie.gti.asdl.rey.gtirecord.model.entity.UserRoles;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InstanceFactoryTest {

    @Test
    void create_appliesDefaultIfNullAnnotationValues() {
        Assignment assignment = InstanceFactory.create(Assignment.class);

        // weighting and maxGrade are annotated with @DefaultIfNull("0")
        assertEquals(0, assignment.getWeighting());
        assertEquals(0, assignment.getMaxGrade());
    }

    @Test
    void create_leavesSimpleFieldsWithoutDefaultIfNullAsNull() {
        Assignment assignment = InstanceFactory.create(Assignment.class);

        // id has no @DefaultIfNull annotation
        assertNull(assignment.getId());
    }

    @Test
    void create_recursivelyPopulatesNestedObjectFields() {
        Assignment assignment = InstanceFactory.create(Assignment.class);

        // groupModule is a non-simple, non-collection field and must be recursively created
        assertNotNull(assignment.getGroupModule());
    }

    @Test
    void create_initializesListFieldsAsEmptyMutableList() {
        UserRoles userRoles = InstanceFactory.create(UserRoles.class);

        assertNotNull(userRoles.getRoles());
        assertTrue(userRoles.getRoles().isEmpty());

        // Verify the returned list is actually mutable.
        userRoles.getRoles().add(null);
        assertEquals(1, userRoles.getRoles().size());
    }

    @Test
    void create_recursivelyPopulatesNestedNonCollectionObject() {
        UserRoles userRoles = InstanceFactory.create(UserRoles.class);

        assertNotNull(userRoles.getUser());
    }
}
