package de.bentzin.ingwer.features;

import de.bentzin.ingwer.Ingwer;
import de.bentzin.ingwer.logging.Logger;
import de.bentzin.ingwer.logging.SystemLogger;
import de.bentzin.ingwer.preferences.Preferences;
import de.bentzin.ingwer.thow.IngwerThrower;
import de.bentzin.ingwer.thow.ThrowType;
import de.bentzin.tools.register.Registerator;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

/**
 * @author Ture Bentzin
 * @implNote internal
 * @see FeatureManager,Feature,SimpleFeature,NewFeature
 */
@ApiStatus.Internal
public final class FeatureFinder {
    private Logger logger;

    public FeatureFinder(@NotNull FeatureManager manager){
       logger = manager.getLogger().adopt("finder");
       logger.info("successfully initialized: " + this.getClass().getSimpleName());
    }

    public FeatureFinder(Logger logger){
       this.logger = logger;
    }

    public void find() {
        logger.info("searching for new features...");
        Reflections ref = new Reflections("de.");
        for (Class<?> cl : ref.getTypesAnnotatedWith(NewFeature.class)) {
            logger.debug("found: " + cl.getSimpleName());
            NewFeature newFeature = cl.getAnnotation(NewFeature.class);
            try {
                    Feature feature1 = (Feature) cl.getConstructor().newInstance();
                    logger.info("initialized feature: " + feature1.getName() + " v." + newFeature.version() +  " by: "+ newFeature.author());
                    try {
                        FeatureManager.getInstance().register(feature1); //register feature
                    }catch (Registerator.DuplicateEntryException e) {
                        logger.debug("Feature is already registered! - skip <> " + feature1.getName());
                    }
                } catch (Exception e) {
                    logger.waring("Error accorded while loading suspected Feature: "
                            + cl.getCanonicalName() + " v. "+ newFeature.version() +  " by: "+ newFeature.author() + " >> " + e.getMessage());
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
