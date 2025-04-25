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
public class QQILevel {

    public enum QQILevelType {
        QQI5(1, "Level 5"),
        QQI6(2, "Level 6");

        public final int id;
        public final String name;

        QQILevelType(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public QQILevel asQQILevel() {
            QQILevel qqiLevel = new QQILevel();
            qqiLevel.setId(id);
            qqiLevel.setName(name);
            return qqiLevel;
        }
    }

    @KeyField
    @NotNull(groups = OnUpdate.class)
    private Integer id;

    @ShortDescriptionField
    @NotBlank
    private String name;

    @Override
    public String toString() {
        return name;
    }

}
