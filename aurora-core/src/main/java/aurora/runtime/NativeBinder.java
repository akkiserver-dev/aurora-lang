package aurora.runtime;

import java.lang.reflect.Method;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class responsible for binding Java classes to Aurora the Environment.
 * It handles the registration of native modules and methods, as well as
 * bidirectional type conversion between Java and Aurora objects.
 */
public class NativeBinder {
    private static final Pattern SIGNATURE_PATTERN = Pattern.compile("([\\w.]+)\\((.*)\\)");

    /**
     * Binds a Java module instance to the Virtual Machine.
     * The module class must be annotated with {@link AuroraLib}.
     * @param vm The VM to bind to.
     * @param module The Java module instance.
     */
    public static void bind(VM vm, Object module) {
        Class<?> clazz = module.getClass();
        AuroraLib libAnn = clazz.getAnnotation(AuroraLib.class);
        if (libAnn == null) {
            throw new AuroraRuntimeException("Class " + clazz.getName() + " is not annotated with @AuroraLib");
        }

        // We use the namespace from the AuroraLib annotation if it looks like a
        // path/namespace
        // But the user suggested "source_file.ar". Let's assume it correlates to the
        // namespace.
        // For now, let's extract the namespace from the class itself or methods.
        // Actually, the full name in globals is what matters.

        for (Method method : clazz.getDeclaredMethods()) {
            AuroraNative nativeAnn = method.getAnnotation(AuroraNative.class);
            if (nativeAnn != null) {
                bindMethod(vm, module, method, nativeAnn.value());
            }
        }
    }

    private static void bindMethod(VM vm, Object instance, Method method, String signature) {
        Matcher matcher = SIGNATURE_PATTERN.matcher(signature);
        if (!matcher.matches()) {
            throw new AuroraRuntimeException("Invalid native signature: " + signature);
        }

        String auroraName = matcher.group(1);
        String paramsPart = matcher.group(2);

        // Count arity (simplified, just count L...; and primitives)
        int arity = 0;
        if (!paramsPart.isEmpty()) {
            for (int i = 0; i < paramsPart.length(); i++) {
                char c = paramsPart.charAt(i);
                if (c == 'L') {
                    arity++;
                    while (paramsPart.charAt(i) != ';')
                        i++;
                } else if ("IJFDZ".indexOf(c) != -1) {
                    arity++;
                }
            }
        }

        String fqn;
        if (auroraName.contains(".")) {
            // Full name or at least qualified name provided
            fqn = auroraName;
        } else {
            String namespace = "";
            Class<?> clazz = instance.getClass();
            AuroraLib lib = clazz.getAnnotation(AuroraLib.class);
            if (lib != null) {
                namespace = lib.value().replace(".ar", "");
            }
            fqn = namespace.isEmpty() ? auroraName : namespace + "." + auroraName;
        }

        ArNativeFunction wrapper = new ArNativeFunction(auroraName, arity) {
            @Override
            public ArObject call(List<ArObject> args) {
                try {
                    Object[] javaArgs = new Object[args.size()];
                    Class<?>[] paramTypes = method.getParameterTypes();
                    for (int i = 0; i < args.size(); i++) {
                        javaArgs[i] = convertToJava(vm, args.get(i), paramTypes[i]);
                    }
                    Object result = method.invoke(instance, javaArgs);
                    return convertToAurora(vm, result);
                } catch (Exception e) {
                    throw new AuroraRuntimeException(
                            "Error calling native method " + method.getName() + ": " + e.getMessage());
                }
            }
        };

        vm.shared.globals.put(fqn, wrapper);
    }

    public static Object convertToJava(VM vm, ArObject auroraObj, Class<?> targetType) {
        if (ArObject.class.isAssignableFrom(targetType)) {
            if (targetType.isInstance(auroraObj)) {
                return auroraObj;
            }
            // If it's ArNativeObject, we might want to unwrap it even if target is
            // ArObject?
            // Usually not. If target is ArObject, they want the wrapper.
        }

        if (auroraObj instanceof ArInt) {
            int val = ((ArInt) auroraObj).value;
            if (targetType == int.class || targetType == Integer.class)
                return val;
            if (targetType == long.class || targetType == Long.class)
                return (long) val;
            if (targetType == double.class || targetType == Double.class)
                return (double) val;
            if (targetType == Object.class)
                return val;
        }
        if (auroraObj instanceof ArLong) {
            long val = ((ArLong) auroraObj).value;
            if (targetType == long.class || targetType == Long.class)
                return val;
            if (targetType == Object.class)
                return val;
        }
        if (auroraObj instanceof ArFloat) {
            float val = ((ArFloat) auroraObj).value;
            if (targetType == float.class || targetType == Float.class)
                return val;
            if (targetType == double.class || targetType == Double.class)
                return (double) val;
            if (targetType == Object.class)
                return val;
        }
        if (auroraObj instanceof ArDouble) {
            double val = ((ArDouble) auroraObj).value;
            if (targetType == double.class || targetType == Double.class)
                return val;
            if (targetType == Object.class)
                return val;
        }
        if (auroraObj instanceof ArBool) {
            boolean val = ((ArBool) auroraObj).value;
            if (targetType == boolean.class || targetType == Boolean.class)
                return val;
            if (targetType == Object.class)
                return val;
        }
        if (auroraObj instanceof ArString) {
            String val = ((ArString) auroraObj).value;
            if (targetType == String.class)
                return val;
            if (targetType == Object.class)
                return val;
        }
        if (auroraObj instanceof ArNativeObject)
            return ((ArNativeObject) auroraObj).object;

        // Use ObjectBinder for ArInstance mapping to POJO
        if (auroraObj instanceof ArInstance) {
            try {
                return ObjectBinder.toJava(vm, (ArInstance) auroraObj, targetType);
            } catch (Exception e) {
                // Ignore, let it fall through
            }
        }

        return auroraObj;
    }

    public static ArObject convertToAurora(VM vm, Object javaObj) {
        if (javaObj == null)
            return ArNone.INSTANCE;
        if (javaObj instanceof ArObject)
            return (ArObject) javaObj;
        if (javaObj instanceof Integer)
            return new ArInt((Integer) javaObj);
        if (javaObj instanceof Long)
            return new ArLong((Long) javaObj);
        if (javaObj instanceof Float)
            return new ArFloat((Float) javaObj);
        if (javaObj instanceof Double)
            return new ArDouble((Double) javaObj);
        if (javaObj instanceof Boolean)
            return (Boolean) javaObj ? ArBool.TRUE : ArBool.FALSE;
        if (javaObj instanceof String)
            return new ArString((String) javaObj);

        // Complex Java Object -> Aurora Instance
        try {
            ArInstance instance = ObjectBinder.toAurora(vm, javaObj);
            if (instance != null) {
                return instance;
            }
        } catch (Exception e) {
            // Unbound, treat as native object
        }

        return new ArNativeObject(javaObj);
    }
}
