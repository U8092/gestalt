package ovh.mythmc.gestalt.annotations.conditions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

import ovh.mythmc.gestalt.Gestalt;
import ovh.mythmc.gestalt.features.IFeature;

public final class FeatureConditionProcessor {

    public static boolean canBeEnabled(@NotNull IFeature feature) {
        try {
            return booleanCondition(feature) && versionCondition(feature);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            System.out.println(e);
            e.printStackTrace();
        }

        return false;
    }

    private static boolean booleanCondition(@NotNull IFeature feature) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        boolean b = false;
        
        Method[] methods = feature.getClass().getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(FeatureConditionBoolean.class)) {
                b = (boolean) method.invoke(feature);
            } else {
                return true;
            }
        }

        return b;
    }

    private static boolean versionCondition(@NotNull IFeature feature) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Collection<String> versions = new ArrayList<>();
        
        Method[] methods = feature.getClass().getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(FeatureConditionVersion.class)) {
                Collection<?> objectList = (Collection<?>) method.invoke(feature);
                versions = objectList.stream()
                    .filter(o -> o instanceof String)
                    .map(o -> (String) o)
                    .collect(Collectors.toList());
            }
        }

        if (versions.isEmpty())
            return true;

        for (String version : versions) {
            if (version.equalsIgnoreCase("ALL") || Gestalt.get().getServerVersion().startsWith(version))
                return true;
        }

        return false;
    }

    
}
