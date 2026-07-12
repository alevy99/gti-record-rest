package ie.gti.asdl.rey.gtirecord.core.util;

import ie.gti.asdl.rey.gtirecord.model.entity.Assignment;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AssignmentUtilsTest {

    private Assignment newAssignment(Integer maxGrade, Integer weighting) {
        Assignment assignment = new Assignment(1, null, "Assignment 1", weighting, maxGrade, null, null);
        return assignment;
    }

    @Test
    void calcGradePercent_returnsPercentageOfMaxGrade() {
        Assignment assignment = newAssignment(50, 20);

        assertEquals(80.0, AssignmentUtils.calcGradePercent(assignment, 40));
    }

    @Test
    void calcGradePercent_returnsNull_whenGradeIsNull() {
        Assignment assignment = newAssignment(50, 20);

        assertNull(AssignmentUtils.calcGradePercent(assignment, null));
    }

    @Test
    void calcGradePercent_returnsNull_whenMaxGradeIsNull() {
        Assignment assignment = newAssignment(null, 20);

        assertNull(AssignmentUtils.calcGradePercent(assignment, 40));
    }

    @Test
    void calcGradePercent_returnsNull_whenMaxGradeIsZeroOrNegative() {
        Assignment assignment = newAssignment(0, 20);

        assertNull(AssignmentUtils.calcGradePercent(assignment, 10));
    }

    @Test
    void calcGradePercent_returnsFullMarks_whenGradeEqualsMaxGrade() {
        Assignment assignment = newAssignment(100, 20);

        assertEquals(100.0, AssignmentUtils.calcGradePercent(assignment, 100));
    }

    @Test
    void calcWeightingTotalPercent_appliesWeightingToGradePercent() {
        // 40/50 = 80% grade, weighted at 20% => 16.0
        Assignment assignment = newAssignment(50, 20);

        assertEquals(16.0, AssignmentUtils.calcWeightingTotalPercent(assignment, 40));
    }

    @Test
    void calcWeightingTotalPercent_returnsNull_whenGradePercentCannotBeCalculated() {
        Assignment assignment = newAssignment(null, 20);

        assertNull(AssignmentUtils.calcWeightingTotalPercent(assignment, 40));
    }

    @Test
    void calcWeightingTotalPercent_returnsZero_whenWeightingIsZero() {
        Assignment assignment = newAssignment(50, 0);

        assertEquals(0.0, AssignmentUtils.calcWeightingTotalPercent(assignment, 40));
    }
}
