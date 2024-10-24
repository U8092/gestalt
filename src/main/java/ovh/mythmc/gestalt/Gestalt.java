package ovh.mythmc.gestalt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.conditions.FeatureConditionProcessor;
import ovh.mythmc.gestalt.exceptions.AlreadyInitializedException;
import ovh.mythmc.gestalt.exceptions.NotInitializedException;
import ovh.mythmc.gestalt.features.FeaturePriority;
import ovh.mythmc.gestalt.features.IFeature;
import ovh.mythmc.gestalt.features.impl.ClassFeature;

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

    private final Map<IFeature, Boolean> featureMap = new HashMap<>();

    public void register(final @NotNull IFeature... features) {
        Arrays.stream(features).forEach(feature -> {
            if (!featureMap.containsKey(feature)) {
                featureMap.put(feature, false);
                feature.initialize();
            }
        });
    }

    public void register(final @NotNull Class<?>... classes) {
        Arrays.stream(classes).forEach(cl -> {
            if (!cl.isAnnotationPresent(Feature.class))
                return;

            ClassFeature feature = new ClassFeature(cl, cl.getAnnotation(Feature.class));
            register(feature);
        });
    }

    public void unregister(final @NotNull IFeature... features) {
        Arrays.stream(features).forEach(feature -> {
            featureMap.remove(feature);
            feature.shutdown();
        });
    }

    public void unregister(final @NotNull Class<?>... classes) {
        Arrays.stream(classes).forEach(cl -> {
            if (!cl.isAnnotationPresent(Feature.class))
                return;

            ClassFeature feature = new ClassFeature(cl, cl.getAnnotation(Feature.class)); // temporary
            unregister(feature);
        });
    }

    public void unregisterAllFeatures() {
        for (int i = 0; i < featureMap.keySet().size(); i++) {
            IFeature feature = featureMap.keySet().stream().toList().get(i);
            if (!featureMap.get(feature))
                continue;

            unregister(feature);
        }
    }

    public void enableFeature(final @NotNull IFeature feature) {
        if (FeatureConditionProcessor.canBeEnabled(feature.getClass())) {
            featureMap.put(feature, true);
            feature.enable();
        }
    }

    public void disableFeature(final @NotNull IFeature feature) {
        if (featureMap.get(feature)) {
            featureMap.put(feature, false);
            feature.disable();
        }
    }

    public void enableAllFeatures() {
        getSortedByPriority().forEach(this::enableFeature);
    }

    public void enableAllFeatures(final @NotNull String key) {
        getSortedByPriority().stream().filter(feature -> feature.key().equals(key)).forEach(this::enableFeature);
    }

    public void disableAllFeatures() {
        getSortedByPriority().forEach(this::disableFeature);
    }

    public void disableAllFeatures(final @NotNull String key) {
        getSortedByPriority().stream().filter(feature -> feature.key().equals(key)).forEach(this::disableFeature);
    }

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

    public boolean isEnabled(final @NotNull IFeature feature) {
        return featureMap.get(feature);
    }

}
