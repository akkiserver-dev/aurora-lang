package aurora.runtime.modules;

import aurora.runtime.VM;

/**
 * Interface for Java classes that provide native functionality to the Aurora VM.
 * Modules implementing this interface can be registered with a {@link VM} instance.
 */
public interface NativeModule {
    /**
     * Registers the module's functions and constants with the given VM.
     * @param vm The VM instance to register with.
     */
    void register(VM vm);
}
