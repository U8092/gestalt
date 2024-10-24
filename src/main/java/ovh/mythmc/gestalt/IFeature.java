package ovh.mythmc.gestalt;

public interface IFeature {

    String key();

    String type();

    default FeaturePriority priority() { return FeaturePriority.NORMAL; }

    default void initialize() { 
    }

    default void enable() {
    }

    default void disable() {
    }

    default void shutdown() {
    }

}
