package ie.gti.asdl.rey.gtirecord.model.annotation;

/**
 * Defines the formatting styles applied to a field's value when it is
 * included in a short description, as used by {@link ShortDescriptionField}.
 */
public enum ShortDescriptionFormat {

    /** The field value is used as-is, without any transformation. */
    DEFAULT,      // As is (normal field)

    /** Only the first letter is used, capitalized and followed by a dot (e.g. "J."). */
    FIRST_LETTER, // First Capital letter and a dot

    /** The field value is converted entirely to uppercase. */
    UPPERCASE,    // Only UPPERCASE

    /** The field value is converted entirely to lowercase. */
    LOWERCASE,    // Only lowercase

    /** The field value is formatted as a proper name (e.g. capitalized appropriately). */
    NAME_FORMAT;  // As a 'Name'
}
