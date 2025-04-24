package ie.gti.asdl.rey.gtirecord.model.entity.add;

import lombok.Data;

/**
 * @author Andrei Levchenko
 */
@Data
public class StudentAssignmentStats {

    private double weightingTotalPercent = 0.0;
    private int maxWeightingTotalPercent = 0;
    private int gradeTotal = 0;
    private int maxGradeTotal = 0;

    public void addWeightingTotalPercent(double weightingTotalPercent) {
        this.weightingTotalPercent += weightingTotalPercent;
    }

    public void addGradeTotal(int gradeTotal) {
        this.gradeTotal += gradeTotal;
    }

    public void addMaxWeightingTotalPercent(int maxWeightingTotalPercent) {
        this.maxWeightingTotalPercent += maxWeightingTotalPercent;
    }

    public void addMaxGradeTotal(int maxGradeTotal) {
        this.maxGradeTotal += maxGradeTotal;
    }

    public double getGradeTotalPercent() {
        return maxGradeTotal == 0 ? 0 : 100 * (double) gradeTotal / maxGradeTotal;
    }

}
