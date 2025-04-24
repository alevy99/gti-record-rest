package ie.gti.asdl.rey.gtirecord.core.util;


import ie.gti.asdl.rey.gtirecord.model.entity.Assignment;

/**
 * @author Andrei Levchenko
 */
public class AssignmentUtils {

    public static Double calcGradePercent(Assignment assignment, Integer grade) {
        Double gradePercent = null;
        if (assignment.getMaxGrade() != null && assignment.getMaxGrade() > 0 && grade != null) {
            double maxGrade = assignment.getMaxGrade();
            gradePercent = grade / maxGrade * 100.0;
        }
        return gradePercent;
    }

    public static Double calcWeightingTotalPercent(Assignment assignment, Integer grade) {
        Double gradePercent = calcGradePercent(assignment, grade);
        if (gradePercent == null) {
            return null;
        } else {
            return gradePercent * assignment.getWeighting() / 100.0;
        }
    }
}
