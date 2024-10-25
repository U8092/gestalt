package ovh.mythmc.gestalt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.conditions.FeatureConditionProcessor;
import ovh.mythmc.gestalt.annotations.status.FeatureDisable;
import ovh.mythmc.gestalt.annotations.status.FeatureEnable;
import ovh.mythmc.gestalt.annotations.status.FeatureInitialize;
import ovh.mythmc.gestalt.annotations.status.FeatureShutdown;
import ovh.mythmc.gestalt.exceptions.AlreadyInitializedException;
import ovh.mythmc.gestalt.exceptions.NotInitializedException;
import ovh.mythmc.gestalt.features.FeaturePriority;
import ovh.mythmc.gestalt.features.IFeature;
import ovh.mythmc.gestalt.features.impl.ClassFeature;
import ovh.mythmc.gestalt.util.AnnotationUtil;

public class Gestalt {

    private final String serverVersion;

    private static Gestalt gestalt;

    public Gestalt(String serverVersion) {
        this.serverVersion = serverVersion;
    }

    public String getServerVersion() {
        return serverVersion;
    }

    public static boolean isGestaltInitialized() {
        return gestalt != null;
    }

    public static void set(final @NotNull Gestalt g) {
        if (isGestaltInitialized())
            throw new AlreadyInitializedException("Gestalt is already initialized! (is Gestalt properly shaded?)");

        gestalt = g;
    }

    public static Gestalt get() {
        if (gestalt == null)
            throw new NotInitializedException();

        return gestalt;
    }

    // Class, Enabled
    private final Map<Class<?>, Boolean> classMap = new HashMap<>();

    public void register(final @NotNull Class<?>... classes) {
        Arrays.stream(classes).forEach(clazz -> {
            if (!clazz.isAnnotationPresent(Feature.class))
                return;

            if (classMap.containsKey(clazz))
                return;

            AnnotationUtil.triggerAnnotatedMethod(clazz, FeatureInitialize.class);
            classMap.put(clazz, false);
        });
    }

    public void unregister(final @NotNull Class<?>... classes) {
        Arrays.stream(classes).forEach(clazz -> {
            if (!classMap.containsKey(clazz))
                return;

            AnnotationUtil.triggerAnnotatedMethod(clazz, FeatureShutdown.class);
            classMap.remove(clazz);
        });
    }

    public void unregisterAllFeatures() {
        for (int i = 0; i < classMap.keySet().size(); i++) {
            Class<?> clazz = classMap.keySet().stream().toList().get(i);
            unregister(clazz);
        }
    }

    public void enableFeature(final @NotNull Class<?> clazz) {
        if (classMap.get(clazz))
            return;

        if (FeatureConditionProcessor.canBeEnabled(clazz)) {
            classMap.put(clazz, true);
            AnnotationUtil.triggerAnnotatedMethod(clazz, FeatureEnable.class);
        }
    }

    public void disableFeature(final @NotNull Class<?> clazz) {
        if (classMap.get(clazz)) {
            classMap.put(clazz, false);
            AnnotationUtil.triggerAnnotatedMethod(clazz, FeatureDisable.class);
        }
    }

    public void enableAllFeatures() {
        getSortedByPriority().forEach(this::enableFeature);
    }

    public void enableAllFeatures(final @NotNull String key) {
        getSortedByPriority().stream().filter(clazz -> clazz.getAnnotation(Feature.class).key().equals(key)).forEach(this::enableFeature);
    }

    public void disableAllFeatures() {
        getSortedByPriority().forEach(this::disableFeature);
    }

    public void disableAllFeatures(final @NotNull String key) {
        getSortedByPriority().stream().filter(clazz -> clazz.getAnnotation(Feature.class).key().equals(key)).forEach(this::disableFeature);
    }

    public List<Class<?>> getByKey(final @NotNull String key) {
        return classMap.keySet().stream()
            .filter(clazz -> clazz.getAnnotation(Feature.class).key().equals(key))
            .toList();
    }

    public List<Class<?>> getByType(final @NotNull String type) {
        return classMap.keySet().stream()
            .filter(clazz -> clazz.getAnnotation(Feature.class).type().equals(type))
            .toList();
    }

    public List<Class<?>> getByPriority(final @NotNull FeaturePriority priority) {
        return classMap.keySet().stream()
            .filter(clazz -> clazz.getAnnotation(Feature.class).priority().equals(priority))
            .toList();
    }

    public List<Class<?>> getSortedByPriority() {
        List<Class<?>> classList = new ArrayList<>();
        getByPriority(FeaturePriority.HIGHEST).forEach(classList::add);
        getByPriority(FeaturePriority.HIGH).forEach(classList::add);
        getByPriority(FeaturePriority.NORMAL).forEach(classList::add);
        getByPriority(FeaturePriority.LOW).forEach(classList::add);
        getByPriority(FeaturePriority.LOWEST).forEach(classList::add);
        return classList;
    }

    /*
    public List<IFeature> getByKeyAndType(final @NotNull String key, final @NotNull String type) {
        return getByKey(key).stream().filter(feature -> feature.type().equals(type)).toList();
    }

    public List<IFeature> getByKey(final @NotNull String key) {
        return featureMap.keySet().stream().filter(feature -> feature.key().equals(key)).toList();
    }

    public List<IFeature> getByType(final @NotNull String type) {
        return featureMap.keySet().stream().filter(feature -> feature.type().equals(type)).toList();
    }    

    public List<IFeature> getByPriority(final @NotNull FeaturePriority priority) {
        return featureMap.keySet().stream().filter(feature -> feature.priority().equals(priority)).toList();
    }

    public List<IFeature> getSortedByPriority() {
        List<IFeature> featureList = new ArrayList<>();
        getByPriority(FeaturePriority.HIGHEST).forEach(featureList::add);
        getByPriority(FeaturePriority.HIGH).forEach(featureList::add);
        getByPriority(FeaturePriority.NORMAL).forEach(featureList::add);
        getByPriority(FeaturePriority.LOW).forEach(featureList::add);
        getByPriority(FeaturePriority.LOWEST).forEach(featureList::add);
        return featureList;
    }
         */

    public boolean isEnabled(final @NotNull Class<?> clazz) {
        return classMap.get(clazz);
    }

}
