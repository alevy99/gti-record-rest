package ie.gti.asdl.rey.gtirecord.model.entity;

import lombok.Data;

@Data
public class CourseType {

    public enum CourseTypeType {
        FULL_TIME(1, "Full time"),
        PART_TIME(2, "Part time"),
        ONLINE(3, "Online"),
        EVENING(4, "Evening");

        public final int id;
        public final String type;

        CourseTypeType(int id, String type) {
            this.id = id;
            this.type = type;
        }

        public CourseType asCourseType() {
            CourseType courseType = new CourseType();
            courseType.setId(id);
            courseType.setType(type);
            return courseType;
        }
    }

    private int id;

    private String type;

    @Override
    public String toString() {
        return type;
    }

}
