
package cn.zmdx.kaka.locker.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class HDBReflectionUtils {
    public static boolean copyAllFields(Class<?> clsBase, Object src, Object target) {
        if (src == null || target == null) {
            return false;
        }

        Class<?> clsType = clsBase;
        while (clsType != null) {
            Field[] fields = clsType.getDeclaredFields();
            for (Field fid : fields) {
                try {
                    fid.setAccessible(true);
                    Object ref = fid.get(src);
                    fid.set(target, ref);
                } catch (Exception e) {
                }
            }
            clsType = clsType.getSuperclass();
        }

        return true;
    }

    public static Method findMethod(Class<?> cls, String name, Class<?>... parameterTypes) throws NoSuchMethodException {
        try {
            Method m = cls.getMethod(name, parameterTypes);
            if (m != null) {
                return m;
            }
        } catch (Exception e) {
            // ignore this error & pass down
        }

        Class<?> clsType = cls;
        while (clsType != null) {
            try {
                Method m = clsType.getDeclaredMethod(name, parameterTypes);
                m.setAccessible(true);
                return m;
            } catch (NoSuchMethodException e) {
            }
            clsType = clsType.getSuperclass();
        }
        throw new NoSuchMethodException();
    }

    public static Method findMethodNoThrow(Class<?> cls, String name, Class<?>... parameterTypes) {
        Method m = null;
        try {
            m = findMethod(cls, name, parameterTypes);
            m.setAccessible(true);
        } catch (NoSuchMethodException e) {
        }
        return m;
    }
    
    public static Field findField(Class<?> cls, String name) throws NoSuchFieldException {
        try {
            Field m = cls.getField(name);
            if (m != null) {
                return m;
            }
        } catch (Exception e) {
            // ignore this error & pass down
        }

        Class<?> clsType = cls;
        while (clsType != null) {
            try {
                Field m = clsType.getDeclaredField(name);
                m.setAccessible(true);
                return m;
            } catch (NoSuchFieldException e) {
            }
            clsType = clsType.getSuperclass();
        }
        throw new NoSuchFieldException();
    }

    public static Field findFieldNoThrow(Class<?> cls, String name) {
        Field f = null;
        try {
            f = findField(cls, name);
        } catch (NoSuchFieldException e) {
        }
        return f;
    }

    public static Class<?> loadClassNoThrow(ClassLoader loader, String name) {
        try {
            return loader.loadClass(name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
