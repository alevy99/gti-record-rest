package ie.gti.asdl.rey.gtirecord.model.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Andrei Levchenko
 */
@Getter
@Setter
@AllArgsConstructor
public class ContainerOfAny<T> {
    private T value;
}
