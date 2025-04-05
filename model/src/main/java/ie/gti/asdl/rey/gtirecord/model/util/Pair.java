package ie.gti.asdl.rey.gtirecord.model.util;

import lombok.Data;

/**
 * @author Andrei Levchenko
 */
@Data
public class Pair<T,U> {

    private T value1;
    private U value2;

    public Pair(T value1, U value2) {
        this.value1 = value1;
        this.value2 = value2;
    }
}
