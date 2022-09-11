package de.bentzin.ingwer.features;

import de.bentzin.ingwer.Ingwer;
import de.bentzin.ingwer.logging.Logger;

public abstract class SimpleFeature implements Feature {

    protected boolean valid;
    private final String name;
    private final String description;
    private Logger logger;

    public SimpleFeature(String name, String description) {
        this.name = name;
        this.description = description;
        if (!FeatureManager.getInstance().checkName(name)) {
            logger = Ingwer.getFeatureManager().getLogger().adopt(name);
        } else {
            Ingwer.getFeatureManager().getLogger().error("ambiguous naming of: " + name + "!");
            valid = false;
            return;
        }
        logger.info("finished creating of: " + name + "-feature");
        valid = true;

       /* try {
            Ingwer.getFeatureManager().register(this);
        } catch (Registerator.DuplicateEntryException e) {
            Ingwer.getIngwerThrower().accept(e, ThrowType.FEATURE);
        }
        */
    }

    @Override
    public boolean load() {
        if (!valid) return false;
        return onLoad();
    }

    public abstract boolean onLoad();

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + "-feature";
    }


}
