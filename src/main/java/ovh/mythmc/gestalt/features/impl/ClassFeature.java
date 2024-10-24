package ovh.mythmc.gestalt.features.impl;

import org.jetbrains.annotations.NotNull;

import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.status.FeatureDisable;
import ovh.mythmc.gestalt.annotations.status.FeatureEnable;
import ovh.mythmc.gestalt.annotations.status.FeatureInitialize;
import ovh.mythmc.gestalt.annotations.status.FeatureShutdown;
import ovh.mythmc.gestalt.features.FeaturePriority;
import ovh.mythmc.gestalt.features.IFeature;
import ovh.mythmc.gestalt.util.AnnotationUtil;

public final class ClassFeature implements IFeature {

    //private final Class<?> clazz;

    private final Feature annotation; // Todo: move to method?

    public ClassFeature(Class<?> clazz, @NotNull Feature annotation) {
        //this.clazz = clazz;
        this.annotation = annotation;
    }

    @Override
    public String key() {
        return annotation.key();
    }

    @Override
    public String type() {
        return annotation.type();
    }

    @Override
    public FeaturePriority priority() {
        return annotation.priority();
    }

    @Override
    public void initialize() {
        AnnotationUtil.triggerAnnotatedMethod(this, FeatureInitialize.class);
    }

    @Override
    public void enable() {
        AnnotationUtil.triggerAnnotatedMethod(this, FeatureEnable.class);
    }

    @Override
    public void disable() {
        AnnotationUtil.triggerAnnotatedMethod(this, FeatureDisable.class);
    }

    @Override
    public void shutdown() {
        AnnotationUtil.triggerAnnotatedMethod(this, FeatureShutdown.class);
    }
    
}
