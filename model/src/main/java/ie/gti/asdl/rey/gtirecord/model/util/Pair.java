package ie.gti.asdl.rey.gtirecord.model.util;

import ie.gti.asdl.rey.gtirecord.model.annotation.ShortDescriptionField;
import lombok.Data;

/**
 * @author Andrei Levchenko
 */
@Data
public class Pair<T,U> {

//    @ShortDescriptionField
    private T value1;
//    @ShortDescriptionField
    private U value2;

    public Pair(T value1, U value2) {
        this.value1 = value1;
        this.value2 = value2;
    }
}
