package ie.gti.asdl.rey.gtirecord.core.service.impl;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for ValidationServiceImpl.
 * All collaborators are mocked with Mockito; no Spring context is loaded.
 */
@ExtendWith(MockitoExtension.class)
class ValidationServiceImplTest {

    @Mock
    private Validator validator;

    @InjectMocks
    private ValidationServiceImpl validationService;

    // Validate returns true when no violations.
    @Test
    void validate_returnsTrue_whenNoViolations() {
        Object target = new Object();
        when(validator.validate(eq(target), any(Class[].class))).thenReturn(Set.of());

        boolean valid = validationService.validate(target);

        assertTrue(valid);
    }

    // Validate returns false when violations exist.
    @Test
    void validate_returnsFalse_whenViolationsExist() {
        Object target = new Object();
        @SuppressWarnings("unchecked")
        ConstraintViolation<Object> violation = org.mockito.Mockito.mock(ConstraintViolation.class);
        when(validator.validate(eq(target), any(Class[].class))).thenReturn(Set.of(violation));

        boolean valid = validationService.validate(target);

        assertFalse(valid);
    }

    // Validate passes supplied groups along with default group to validator.
    @Test
    void validate_passesSuppliedGroups_alongWithDefaultGroup_toValidator() {
        Object target = new Object();
        when(validator.validate(eq(target), any(Class[].class))).thenReturn(Set.of());

        validationService.validate(target, OnUpdateMarker.class);

        verify(validator).validate(eq(target), any(Class[].class));
    }

    private interface OnUpdateMarker {
    }
}
