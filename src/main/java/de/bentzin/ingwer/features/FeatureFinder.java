package de.bentzin.ingwer.features;

import de.bentzin.ingwer.Ingwer;
import de.bentzin.ingwer.features.integrated.InternalFeature;
import de.bentzin.ingwer.logging.Logger;
import de.bentzin.ingwer.logging.SystemLogger;
import de.bentzin.ingwer.preferences.Preferences;
import de.bentzin.ingwer.thow.IngwerThrower;
import de.bentzin.ingwer.thow.ThrowType;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import javax.swing.plaf.basic.BasicTreeUI;
import java.lang.reflect.InvocationTargetException;

public final class FeatureFinder {
    private Logger logger;

    public FeatureFinder(@NotNull FeatureManager manager){
       logger = manager.getLogger().adopt("finder");
    }

    public FeatureFinder(Logger logger){
       this.logger = logger;
    }

    public void find() {
        Reflections ref = new Reflections("");
        for (Class<?> cl : ref.getTypesAnnotatedWith(NewFeature.class)) {
            logger.debug("found: " + cl.getSimpleName());
            NewFeature newFeature = cl.getAnnotation(NewFeature.class);
            try {
                    Feature feature1 = (Feature) cl.getConstructor().newInstance();
                    feature1.load();
                } catch (Exception e) {
                    logger.waring("Error accorded while loading suspected Feature: "
                            + cl.getCanonicalName() + " v. "+ newFeature.version() +  " by: "+ newFeature + " >> " + e.getMessage());
                    IngwerThrower.acceptS(e, ThrowType.FEATURE);
                }

        }
    }


    public static void main(String[] args) {
        Ingwer.start(Preferences.getDefaults(null,null, null));
        FeatureFinder featureFinder = new FeatureFinder(new SystemLogger("Finder"));
        featureFinder.find();
    }
}
