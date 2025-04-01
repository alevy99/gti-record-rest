package ie.gti.asdl.rey.gtirecord.model.entity;

import ie.gti.asdl.rey.gtirecord.model.annotation.KeyField;
import ie.gti.asdl.rey.gtirecord.model.annotation.ShortDescriptionField;
import lombok.Data;

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
    private Integer id;

    @ShortDescriptionField
    private String name;

    @Override
    public String toString() {
        return name;
    }

}
