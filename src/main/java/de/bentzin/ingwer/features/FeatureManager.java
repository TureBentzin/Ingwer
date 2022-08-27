package de.bentzin.ingwer.features;

import de.bentzin.ingwer.Ingwer;
import de.bentzin.ingwer.logging.Logger;
import de.bentzin.tools.register.Registerator;
import org.jetbrains.annotations.NotNull;

public final class FeatureManager extends Registerator<Feature>{

    public static FeatureManager getInstance() {
        return Ingwer.getFeatureManager();
    }

    private Logger logger;

    private Registerator<Feature> featureRegisterator;

    public Logger getLogger() {
        return logger;
    }

    public FeatureManager() {
        this.logger = Ingwer.getLogger().adopt("Features");
        featureRegisterator = new Registerator<>();
    }

    public boolean checkName(String newName) {
        return true;
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
}
