package ie.gti.asdl.rey.gtirecord.model.entity.add;

import lombok.Data;

/**
 * @author Andrei Levchenko
 */
@Data
public class StudentAssignmentStats {

    private double weightingTotal = 0.0;
    private int maxWeightingTotal = 0;
    private int gradeTotal = 0;
    private int maxGradeTotal = 0;

    public void addWeightingTotal(double weightingTotalPercent) {
        this.weightingTotal += weightingTotalPercent;
    }

    public void addGradeTotal(int gradeTotal) {
        this.gradeTotal += gradeTotal;
    }

    public void addMaxWeightingTotal(int maxWeightingTotalPercent) {
        this.maxWeightingTotal += maxWeightingTotalPercent;
    }

    public void addMaxGradeTotal(int maxGradeTotal) {
        this.maxGradeTotal += maxGradeTotal;
    }

    public double getGradeTotalPercent() {
        return maxGradeTotal == 0 ? 0 : 100 * (double) gradeTotal / maxGradeTotal;
    }

    public double getWeightingTotalPercent() {
        return maxWeightingTotal == 0 ? 0 : 100 * (double) weightingTotal / maxWeightingTotal;
    }

    public Reward getReward() {
        return Reward.getReward((int) getGradeTotalPercent());
    }

}
