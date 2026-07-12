package ie.gti.asdl.rey.gtirecord.model.annotation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class DescriptionUtilTest {

    // Test-only fixtures exercising each ShortDescriptionFormat option and field ordering.
    private static class FormattedFields {
        @ShortDescriptionField(order = 2, format = ShortDescriptionFormat.UPPERCASE)
        private String upper = "hello";

        @ShortDescriptionField(order = 1, format = ShortDescriptionFormat.FIRST_LETTER)
        private String first = "world";

        @ShortDescriptionField(order = 3, format = ShortDescriptionFormat.LOWERCASE)
        private String lower = "GALWAY";

        @ShortDescriptionField(order = 4, format = ShortDescriptionFormat.NAME_FORMAT)
        private String name = "ireland";

        @ShortDescriptionField(order = 5)
        private String plain = "asIs";

        // Not annotated - must not appear in the description.
        private String ignored = "should not appear";
    }

    private static class Nested {
        @ShortDescriptionField
        private String label;

        private Nested(String label) {
            this.label = label;
        }
    }

    private static class Parent {
        @ShortDescriptionField
        private final Nested nested;

        private Parent(Nested nested) {
            this.nested = nested;
        }
    }

    // Test-only fixture with a mutual (cyclic) reference between two objects.
    private static class CyclicNode {
        @ShortDescriptionField
        private String label;

        @ShortDescriptionField
        private CyclicNode other;

        private CyclicNode(String label) {
            this.label = label;
        }
    }

    @Test
    void getShortDescription_ordersFieldsByOrderAttribute() {
        FormattedFields fields = new FormattedFields();

        String description = DescriptionUtil.getShortDescription(fields);

        assertEquals("W. HELLO galway Ireland asIs", description);
    }

    @Test
    void getShortDescription_ignoresUnannotatedFields() {
        FormattedFields fields = new FormattedFields();

        String description = DescriptionUtil.getShortDescription(fields);

        assertFalse(description.contains("should not appear"));
    }

    @Test
    void getShortDescription_recursesIntoNestedAnnotatedObject() {
        Parent parent = new Parent(new Nested("Downtown"));

        String description = DescriptionUtil.getShortDescription(parent);

        assertEquals("Downtown", description);
    }

    @Test
    void getShortDescription_returnsEmptyString_forNullObject() {
        assertEquals("", DescriptionUtil.getShortDescription(null));
    }

    @Test
    void getShortDescription_doesNotLoopForever_onCyclicReferences() {
        CyclicNode first = new CyclicNode("First");
        CyclicNode second = new CyclicNode("Second");
        first.other = second;
        second.other = first; // mutual reference

        String description = DescriptionUtil.getShortDescription(first);

        // Should terminate and include both labels exactly once, without stack overflow.
        assertEquals("First Second", description);
    }
}
