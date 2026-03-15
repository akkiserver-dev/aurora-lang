package aurora.runtime;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to mark a Java class as an Aurora native module.
 * The {@code value} provides the namespace for the module.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AuroraLib {
    /** The namespace or library name for this module. */
    String value();
}
