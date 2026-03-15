package aurora.runtime;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a dynamic array of objects in the Aurora runtime.
 */
public class ArArray extends ArObject {
    /** The list of elements contained in the array. */
    public final List<ArObject> elements;

    public ArArray(List<ArObject> elements) {
        this.elements = new ArrayList<>(elements);
    }

    @Override
    public String toString() {
        return "[" + elements.stream().map(ArObject::toString).collect(Collectors.joining(", ")) + "]";
    }
}
