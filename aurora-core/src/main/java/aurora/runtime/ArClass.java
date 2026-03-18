package aurora.runtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a class definition in the Aurora runtime.
 * Classes define the structure and behavior of objects, supporting inheritance,
 * constructors (initializers), and methods.
 */
public class ArClass extends ArObject {
    /** The name of the class. */
    public final String name;

    /** The superclass from which this class inherits. May be null. */
    public ArClass superClass = null;

    /** The constructor function for this class. May be null. */
    public ArFunction initializer = null;

    /** A map of method names to their respective function definitions. */
    public final Map<String, ArObject> methods = new HashMap<>();

    public final List<ArClass> interfaces = new ArrayList<>();

    /**
     * Constructs a new class definition.
     * @param name The name of the class.
     */
    public ArClass(String name) {
        this.name = name;
    }

    public ArClass(String name, ArClass superClass) {
        this.name = name;
        this.superClass = superClass;
    }

    public ArObject findMethod(String name) {
        if (methods.containsKey(name)) {
            return methods.get(name);
        }
        if (superClass != null) {
            return superClass.findMethod(name);
        }
        return null;
    }

    public ArFunction findInitializer() {
        if (initializer != null) {
            return initializer;
        }
        if (superClass != null) {
            return superClass.findInitializer();
        }
        return null;
    }

    public boolean implementsTrait(ArClass trait) {
        for (ArClass iface : interfaces) {
            if (iface == trait || iface.implementsTrait(trait)) return true;
        }
        if (superClass != null) return superClass.implementsTrait(trait);
        return false;
    }

    @Override
    public String toString() {
        return "<class " + name + ">";
    }
}
