package ie.gti.asdl.rey.gtirecord.desktop.util;

/**
 * @author Andrei Levchenko
 */
public class MathUtils {

    public static int ensureRange(Integer value, int min, int max) {
        if (value == null) {
            return min;
        }
        return Math.max(min, Math.min(max, value));
    }

}
