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
        
        for (Method method : feature.getClass().getDeclaredMethods()) {
            System.out.println(method.getName());
            if (!method.isAnnotationPresent(FeatureConditionBoolean.class))
                return true;

            System.out.println(method.getName() + " present");

            b = (boolean) method.invoke(feature);
            System.out.println(b);
        }

        return b;
    }

    private static boolean versionCondition(final @NotNull IFeature feature) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        List<String> versions = new ArrayList<>();

        System.out.println(feature.getClass().getDeclaredMethods());
        
        for (Method method : feature.getClass().getDeclaredMethods()) {
            System.out.println(method.getName());
            if (!method.isAnnotationPresent(FeatureConditionVersion.class))
                return true;

            System.out.println(method.getName() + " present");
            List<?> objectList = (List<?>) method.invoke(feature);
            versions = objectList.stream()
                .filter(o -> o instanceof String)
                .map(o -> (String) o)
                .collect(Collectors.toList());
        }

        for (String version : versions) {
            System.out.println("version " + version);
            if (version.equalsIgnoreCase("ALL") || Gestalt.get().getServerVersion().startsWith(version))
                return true;
        }

        return false;
    }

    
}
