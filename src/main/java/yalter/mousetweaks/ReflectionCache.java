package yalter.mousetweaks;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public class ReflectionCache {

    public boolean available = false;
    public boolean compatible = false;
    private final Object2ObjectMap<String, Class<?>> classes = new Object2ObjectOpenHashMap<>();
    private final Object2ObjectMap<String, Method> methods = new Object2ObjectOpenHashMap<>();
    private final Object2ObjectMap<String, Field> fields = new Object2ObjectOpenHashMap<>();

    public ReflectionCache() {}

    public Object getFieldValue(String name, Object obj) {
        try {
            Field field = fields.get(name);
            if (field != null) {
                return field.get(obj);
            }

            Constants.LOGGER.error("No such field: " + name);
        } catch (Exception e) {
            Constants.LOGGER.error("Failed to get a value of field: " + name, e);
        }

        return null;
    }

    public boolean setFieldValue(Object obj, String name, Object value) {
        try {
            Field field = fields.get(name);
            if (field != null) {
                field.set(obj, value);

                return true;
            }

            Constants.LOGGER.error("No such field: " + name);
        } catch (Exception e) {
            Constants.LOGGER.error("Failed to set a value of field: " + name, e);
        }

        return false;
    }

    public Object invokeMethod(Object obj, String name, Object... args) {
        try {
            Method method = methods.get(name);
            if (method != null) {

                if (args != null) return method.invoke(obj, args);

                return method.invoke(obj, new Object[0]);
            }

            Constants.LOGGER.error("No such method: " + name);
        } catch (Exception e) {
            Constants.LOGGER.error("Failed to invoke method: " + name, e);
        }

        return null;
    }

    public Object invokeStaticMethod(String className, String name, Object... args) {
        Class<?> clazz = classes.get(className);
        if (clazz != null) {

            return invokeMethod(clazz, name, args);
        }

        return null;
    }

    public boolean isInstance(String className, Object obj) {
        Class<?> clazz = classes.get(className);
        if (clazz != null) {
            return clazz.isInstance(obj);
        }

        return false;
    }

    public void storeClass(String name, Class<?> clazz) {
        classes.put(name, clazz);
    }

    public void storeMethod(String name, Method method) {
        methods.put(name, method);
    }

    public void storeField(String name, Field field) {
        fields.put(name, field);
    }
}
