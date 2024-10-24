package ovh.mythmc.gestalt.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import ovh.mythmc.gestalt.features.IFeature;

public final class AnnotationUtil {


    public static void triggerAnnotatedMethod(IFeature instance, Class<? extends Annotation> annotation) {
        /* 
        Class<?> cl = getAnnotatedClass(annotation);
        if (cl == null) return;
        */

        for (Method method : instance.getClass().getMethods()) {
            if (method.isAnnotationPresent(annotation)) {
                System.out.println("funciona");
                try {
                    method.invoke(instance);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /*
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
    */
    
}
