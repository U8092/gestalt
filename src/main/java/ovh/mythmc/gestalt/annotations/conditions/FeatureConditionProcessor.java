package ovh.mythmc.gestalt.annotations.conditions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

import ovh.mythmc.gestalt.Gestalt;
import ovh.mythmc.gestalt.features.IFeature;

public final class FeatureConditionProcessor {

    public static boolean canBeEnabled(final @NotNull IFeature feature) {
        try {
            return booleanCondition(feature) && versionCondition(feature);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            System.out.println(e);
            e.printStackTrace();
        }

        return false;
    }

    private static boolean booleanCondition(final @NotNull IFeature feature) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        boolean b = false;
        
        for (Method method : feature.getClass().getMethods()) {
            if (!method.isAnnotationPresent(FeatureConditionBoolean.class))
                return true;

            b = (boolean) method.invoke(feature);
        }

        return b;
    }

    private static boolean versionCondition(final @NotNull IFeature feature) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        List<String> versions = new ArrayList<>();
        
        for (Method method : feature.getClass().getMethods()) {
            if (!method.isAnnotationPresent(FeatureConditionVersion.class))
                return true;

            List<?> objectList = (List<?>) method.invoke(feature);
            versions = objectList.stream()
                .filter(o -> o instanceof String)
                .map(o -> (String) o)
                .collect(Collectors.toList());
        }

        for (String version : versions) {
            if (version.equalsIgnoreCase("ALL") || Gestalt.get().getServerVersion().startsWith(version))
                return true;
        }

        return false;
    }

    
}
