package ie.gti.asdl.rey.gtirecord.core.util;


import ie.gti.asdl.rey.gtirecord.model.entity.Assignment;


/**
 * Utility class providing calculations related to {@link Assignment} grading,
 * such as converting raw grades into percentages and applying assignment
 * weightings to compute their contribution to an overall total.
 *
 * @author Andrei Levchenko
 */
public class AssignmentUtils {

    /**
     * Calculates the percentage score for a given grade relative to the
     * assignment's maximum possible grade.
     *
     * @param assignment the assignment defining the maximum grade
     * @param grade      the grade achieved, or {@code null} if not graded
     * @return the grade expressed as a percentage of the assignment's maximum grade,
     *         or {@code null} if the grade is {@code null} or the assignment's
     *         maximum grade is {@code null} or not positive
     */
    public static Double calcGradePercent(Assignment assignment, Integer grade) {
        Double gradePercent = null;
        if (assignment.getMaxGrade() != null && assignment.getMaxGrade() > 0 && grade != null) {
            double maxGrade = assignment.getMaxGrade();
            gradePercent = grade / maxGrade * 100.0;
        }

        return gradePercent;
    }

    /**
     * Calculates the weighted contribution of a grade towards an overall total,
     * based on the assignment's grade percentage and its weighting.
     * <p>
     * This is computed as {@code gradePercent * weighting / 100}, where
     * {@code gradePercent} is derived via {@link #calcGradePercent(Assignment, Integer)}.
     *
     * @param assignment the assignment defining the maximum grade and weighting
     * @param grade      the grade achieved, or {@code null} if not graded
     * @return the weighted percentage contribution of this grade towards the total,
     *         or {@code null} if the grade percentage could not be calculated
     *         (see {@link #calcGradePercent(Assignment, Integer)})
     */
    public static Double calcWeightingTotalPercent(Assignment assignment, Integer grade) {
        Double gradePercent = calcGradePercent(assignment, grade);
        if (gradePercent == null) {
            return null;
        } else {
            return gradePercent * assignment.getWeighting() / 100.0;
        }
    }
}
