package ie.gti.asdl.rey.gtirecord.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class Department {

    private int id;

    private String name;

    @Override
    public String toString() {
        return name;
    }

}
