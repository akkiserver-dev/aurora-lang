package aurora.runtime;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to mark a Java method to be exposed as a native function in Aurora.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AuroraNative {
    /**
     * The Aurora function signature, e.g. {@code "print(Lobject;)"}.
     * <p>
     * Signature format: {@code name(types)}
     * Types use JVM-style descriptors:
     * <ul>
     *   <li>{@code Lclassname;} (e.g., {@code Lobject;}, {@code Lstring;})</li>
     *   <li>{@code I} for int</li>
     *   <li>{@code J} for long</li>
     *   <li>{@code F} for float</li>
     *   <li>{@code D} for double</li>
     *   <li>{@code Z} for boolean</li>
     * </ul>
     */
    String value();
}
