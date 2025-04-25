package ie.gti.asdl.rey.gtirecord.model.entity;

import ie.gti.asdl.rey.gtirecord.model.annotation.KeyField;
import ie.gti.asdl.rey.gtirecord.model.annotation.ShortDescriptionField;
import ie.gti.asdl.rey.gtirecord.model.validation.OnUpdate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
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

    @KeyField
    @NotNull(groups = OnUpdate.class)
    private Integer id;

    @ShortDescriptionField
    @NotBlank
    private String type;

    @Override
    public String toString() {
        return type;
    }

}
