package de.bentzin.ingwer.features;

import de.bentzin.ingwer.Ingwer;
import de.bentzin.ingwer.logging.Logger;
import de.bentzin.ingwer.thow.ThrowType;
import de.bentzin.tools.register.Registerator;

public abstract class SimpleFeature implements Feature{

    private String name;
    private String description;
    private Logger logger;

    protected boolean valid;

    public SimpleFeature(String name, String description) {
        this.name = name;
        this.description = description;
        if(FeatureManager.getInstance().checkName(name)) {
            Ingwer.getFeatureManager().getLogger().adopt(name);
        }else {
            Ingwer.getFeatureManager().getLogger().error("ambiguous naming of: " + name + "!");
            valid = false;
            return;
        }
        logger.info("finished creating of: " + name);
        valid = true;

        try {
            Ingwer.getFeatureManager().register(this);
        } catch (Registerator.DuplicateEntryException e) {
            Ingwer.getIngwerThrower().accept(e, ThrowType.FEATURE);
        }
    }

    @Override
    public boolean load() {
        if(!valid) return false;
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
