package ie.gti.asdl.rey.gtirecord.model.entity.add;

import lombok.Getter;

/**
 * @author Andrei Levchenko
 */
public enum Reward {
    Distinction("Distinction", 80, 100),
    Merit("Merit", 65, 79),
    Pass("Pass", 50, 64),
    Unsuccessful("Unsuccessful", 0, 49);

    private final int minGrade;
    private final int maxGrade;
    @Getter
    private final String name;

    Reward(String name, int minGrade, int maxGrade) {
        this.name = name;
        this.minGrade = minGrade;
        this.maxGrade = maxGrade;
    }

    public static Reward getReward(int grade) {
        if (grade >= Distinction.minGrade && grade <= Distinction.maxGrade) {
            return Distinction;
        } else if (grade >= Merit.minGrade && grade <= Merit.maxGrade) {
            return Merit;
        } else if (grade >= Pass.minGrade && grade <= Pass.maxGrade) {
            return Pass;
        } else {
            return Unsuccessful;
        }
    }

}
