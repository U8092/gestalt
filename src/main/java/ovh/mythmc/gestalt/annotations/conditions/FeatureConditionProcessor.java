package ovh.mythmc.gestalt.annotations.conditions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

import org.jetbrains.annotations.NotNull;

import ovh.mythmc.gestalt.Gestalt;

public final class FeatureConditionProcessor {

    public static boolean canBeEnabled(final @NotNull Class<?> clazz) {
        try {
            return booleanCondition(clazz) && versionCondition(clazz);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            System.out.println(e);
            e.printStackTrace();
        }

        return false;
    }

    private static boolean booleanCondition(final @NotNull Class<?> clazz) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Object object = null;
        for (Method method : clazz.getMethods()) {
            if (!method.isAnnotationPresent(FeatureConditionBoolean.class))
                return true;

            method.invoke(object);
        }

        System.out.println(object);

        if (object instanceof Boolean b)
            return b;

        return false;
    }

    private static boolean versionCondition(final @NotNull Class<?> clazz) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        boolean supported = false;
        
        Object object = null;
        for (Method method : clazz.getMethods()) {
            if (!method.isAnnotationPresent(FeatureConditionVersion.class))
                return true;

            method.invoke(object);
        }

        System.out.println(object);

        if (object instanceof Collection<?> collection) {
            for (Object o : collection) {
                if (o instanceof String version)
                    if (version.equalsIgnoreCase("ALL") || Gestalt.get().getServerVersion().startsWith(version))
                        supported = true;
            }
        }

        return supported;
    }

    
}
