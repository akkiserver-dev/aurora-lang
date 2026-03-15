package aurora.runtime;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

/**
 * Utility for mapping regular Java objects (POJOs) to Aurora {@link ArInstance} objects.
 */
public class ObjectBinder {

    /**
     * Converts an arbitrary Java object into an Aurora {@link ArInstance} using reflection.
     * Public fields and getter methods are exposed as properties within the instance.
     * @param vm The current VM instance.
     * @param javaObj The Java object to convert.
     * @return A new Aurora instance representing the Java object.
     */
    public static ArInstance toAurora(VM vm, Object javaObj) {
        if (javaObj == null) {
            return null; // Should be handled by NativeBinder (ArNone)
        }

        Class<?> clazz = javaObj.getClass();
        String className = clazz.getSimpleName();

        // Check if VM has a matching class defined in scope, else create a generic
        // synthetic class
        ArObject existingClass = vm.shared.globals.get(className);
        ArClass arClass;
        if (existingClass instanceof ArClass) {
            arClass = (ArClass) existingClass;
        } else {
            // Create a synthetic class representation
            arClass = new ArClass(className, null);
            // Optionally, we could put this in globals, but dynamic/anonymous classes
            // shouldn't pollute globals automatically unless requested.
        }

        ArInstance instance = new ArInstance(arClass);

        // Bind public fields
        for (Field field : clazz.getFields()) {
            if (Modifier.isStatic(field.getModifiers()))
                continue;

            try {
                Object value = field.get(javaObj);
                instance.fields.put(field.getName(), NativeBinder.convertToAurora(vm, value));
            } catch (IllegalAccessException e) {
                // Ignore inaccessible fields
            }
        }

        // Bind getter methods (getXXX -> xxx, isXXX -> xxx)
        for (Method method : clazz.getMethods()) {
            if (Modifier.isStatic(method.getModifiers()))
                continue;
            if (method.getParameterCount() > 0)
                continue;
            if (method.getDeclaringClass() == Object.class)
                continue;

            String methodName = method.getName();
            String propName = null;

            if (methodName.startsWith("get") && methodName.length() > 3) {
                propName = Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);
            } else if (methodName.startsWith("is") && methodName.length() > 2) {
                propName = Character.toLowerCase(methodName.charAt(2)) + methodName.substring(3);
            }

            if (propName != null) {
                try {
                    Object value = method.invoke(javaObj);
                    instance.fields.put(propName, NativeBinder.convertToAurora(vm, value));
                } catch (Exception e) {
                    // Ignore invocation errors
                }
            }
        }

        return instance;
    }

    /**
     * Attempts to convert an Aurora ArInstance into a native Java type using
     * reflection.
     * Populates public fields and setters based on the fields defined in the
     * ArInstance.
     */
    public static Object toJava(VM vm, ArInstance instance, Class<?> targetType) {
        try {
            Object javaObj = targetType.getDeclaredConstructor().newInstance();

            for (Map.Entry<String, ArObject> entry : instance.fields.entrySet()) {
                String key = entry.getKey();
                ArObject value = entry.getValue();

                // Try to set field directly
                try {
                    Field field = targetType.getField(key);
                    field.set(javaObj, NativeBinder.convertToJava(vm, value, field.getType()));
                    continue; // Success
                } catch (Exception e) {
                    // Field not found or not accessible, try setter
                }

                // Try setter method (setXXX)
                String setterName = "set" + Character.toUpperCase(key.charAt(0)) + key.substring(1);
                for (Method method : targetType.getMethods()) {
                    if (method.getName().equals(setterName) && method.getParameterCount() == 1) {
                        try {
                            Class<?> paramType = method.getParameterTypes()[0];
                            method.invoke(javaObj, NativeBinder.convertToJava(vm, value, paramType));
                            break; // Success
                        } catch (Exception e) {
                            // Method failure
                        }
                    }
                }
            }

            return javaObj;

        } catch (Exception e) {
            throw new AuroraRuntimeException(
                    "Failed to instantiate " + targetType.getName() + " from ArInstance: " + e.getMessage());
        }
    }
}
