package de.bentzin.ingwer.features;

import de.bentzin.ingwer.Ingwer;
import de.bentzin.ingwer.logging.Logger;
import de.bentzin.ingwer.logging.SystemLogger;
import de.bentzin.tools.register.Registerator;
import org.jetbrains.annotations.Async;
import org.jetbrains.annotations.NotNull;
import org.scannotation.AnnotationDB;
import org.scannotation.ClasspathUrlFinder;
import org.scannotation.WarUrlFinder;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Set;


public final class FeatureManager extends Registerator<Feature>{

    private FeatureFinder featureFinder = new FeatureFinder();

    public static FeatureManager getInstance() {
        return Ingwer.getFeatureManager();
    }

    private Logger logger;

    public Logger getLogger() {
        return logger;
    }

    public FeatureManager() {
        this.logger = Ingwer.getLogger().adopt("Features");
    }

    /**
     *
     * @param newName
     * @return if name is already taken
     */
    public boolean checkName(String newName) {
        for (Feature feature : this) {
            if(feature.getName().equalsIgnoreCase(newName)) {return true;}
        }
        return false;
    }

    @Override
    public Feature register(@NotNull Feature object) throws DuplicateEntryException {
        getLogger().info("loading " + object.getName() + "!");
        if(object.load()) {
            getLogger().info("enabling " + object.getName() + "!");
            object.onEnable();
            return super.register(object);
        }
        getLogger().waring("failed to load " + object.getName() + "!");
        return object;
    }

    @Override
    public Feature unregister(@NotNull Feature object) throws NoSuchEntryException {
        if(getIndex().contains(object))
             object.onDisable();
        else
            logger.waring("failed to unregister: " + object + "!");
        return super.unregister(object);
    }

    public void scan() {
        Logger slog = Ingwer.getLogger().adopt("Scannotation");
        slog.info("searching classpath!");
        URL[] classPaths = ClasspathUrlFinder.findClassPaths();
        slog.info("indexed classpath! -> " + classPaths.length + " entry's!");
        AnnotationDB annotationDB = new AnnotationDB();


    }

    public static void main(String[] args) {
        Logger slog = new SystemLogger("SCANNOTATION");
        slog.info("searching classpath!");
        URL[] classPaths = ClasspathUrlFinder.findClassPaths();
        slog.info("indexed classpath! -> " + classPaths.length + " entry's!");
        AnnotationDB annotationDB = new AnnotationDB();

        URL classBase = ClasspathUrlFinder.findClassBase(FeatureManager.class);
        slog.info("classBase: " + classBase);

        try {
                annotationDB.scanArchives(classBase);
            Map<String, Set<String>> classIndex = annotationDB.getClassIndex();
            slog.info("classIndex: " + classIndex);
        } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Map<String, Set<String>> annotationIndex = annotationDB.getAnnotationIndex();
            slog.info(annotationIndex.toString());


    }

    public void findFeatures() {
        featureFinder.find();
    }
}
