package ie.gti.asdl.rey.gtirecord.model.entity;

import ie.gti.asdl.rey.gtirecord.model.annotation.KeyField;
import ie.gti.asdl.rey.gtirecord.model.annotation.ShortDescriptionField;
import lombok.*;

/**
 * @author Andrei Levchenko
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Data
public class Gender {

    public enum GenderType {
        MALE(1, "Male"),
        FEMALE(2, "Female");

        public final int id;
        public final String name;

        GenderType(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public Gender asGender() {
            Gender qqiLevel = new Gender();
            qqiLevel.setId(id);
            qqiLevel.setName(name);
            return qqiLevel;
        }
    }

    @KeyField
    private Integer id;

    @ShortDescriptionField
    private String name;

}
