package ie.gti.asdl.rey.gtirecord.model.entity;

import lombok.Data;

@Data
public class QQILevel {

    public enum QQILevelType {
        QQI5(1, "QQI 5"),
        QQI6(2, "QQI 6");

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

    private int id;

    private String name;

}
