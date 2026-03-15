package aurora.runtime;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents an instance of an Aurora class.
 */
public class ArInstance extends ArObject {
    /** The class of which this is an instance. */
    public final ArClass klass;

    /** A map of field names to their current values within this instance. */
    public final Map<String, ArObject> fields = new HashMap<>();

    public ArInstance(ArClass klass) {
        this.klass = klass;
    }

    @Override
    public String toString() {
        return klass.name + " instance";
    }
}
