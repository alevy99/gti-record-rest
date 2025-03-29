package ie.gti.asdl.rey.gtirecord.model.entity;

import lombok.Data;

@Data
public class CourseType {

    public enum CourseTypeType {
        FULL_TIME(1, "Full time"),
        PART_TIME(2, "Part time"),
        ONLINE(3, "Online");

        public final int id;
        public final String name;

        CourseTypeType(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public CourseType asCourseType() {
            CourseType courseType = new CourseType();
            courseType.setId(id);
            courseType.setName(name);
            return courseType;
        }
    }

    private int id;

    private String name;

}
