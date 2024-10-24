package ovh.mythmc.gestalt.features.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.jetbrains.annotations.NotNull;

import ovh.mythmc.gestalt.annotations.Feature;
import ovh.mythmc.gestalt.annotations.status.FeatureDisable;
import ovh.mythmc.gestalt.annotations.status.FeatureEnable;
import ovh.mythmc.gestalt.annotations.status.FeatureInitialize;
import ovh.mythmc.gestalt.annotations.status.FeatureShutdown;
import ovh.mythmc.gestalt.features.FeaturePriority;
import ovh.mythmc.gestalt.features.IFeature;

public final class ClassFeature implements IFeature {

    private final Class<?> clazz;

    private final Feature annotation;

    public ClassFeature(Class<?> clazz, @NotNull Feature annotation) {
        this.clazz = clazz;
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
        triggerAnnotatedMethod(FeatureInitialize.class);
    }

    @Override
    public void enable() {
        triggerAnnotatedMethod(FeatureEnable.class);
    }

    @Override
    public void disable() {
        triggerAnnotatedMethod(FeatureDisable.class);
    }

    @Override
    public void shutdown() {
        triggerAnnotatedMethod(FeatureShutdown.class);
    }

    protected void triggerAnnotatedMethod(Class<? extends Annotation> annotation) {
        Class<?> cl = getAnnotatedClass(annotation);
        if (cl == null) return;

        for (Method method : cl.getMethods()) {
            if (method.isAnnotationPresent(FeatureInitialize.class)) {
                try {
                    method.invoke(this);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected Class<?> getAnnotatedClass(Class<? extends Annotation> annotation) {
        Class<?> annotatedClass = null;

        Class<?> cl = clazz;
        while (cl != null) {
            if (!cl.isAnnotationPresent(annotation)) {
                cl = cl.getSuperclass();
            }

            annotatedClass = cl;
        }

        return annotatedClass;
    }
    
}
