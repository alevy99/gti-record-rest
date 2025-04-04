package ie.gti.asdl.rey.gtirecord.model.annotation;

/**
 * @author Andrei Levchenko
 */
public enum ShortDescriptionFormat {
    DEFAULT,      // As is (normal field)
    FIRST_LETTER, // First Capital letter and a dot
    UPPERCASE,    // Only UPPERCASE
    LOWERCASE,    // Only lowercase
    NAME_FORMAT;  // As a 'Name'
}
