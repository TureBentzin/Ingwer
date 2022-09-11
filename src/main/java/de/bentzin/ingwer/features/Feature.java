package de.bentzin.ingwer.features;

import de.bentzin.ingwer.identity.permissions.IngwerPermission;
import de.bentzin.ingwer.logging.Logger;

public interface Feature {
    String getName();
    default String getFeatureName()  {
        return getName() + "-feature";
    }
    IngwerPermission generalUsePermission();
    String getDescription();
    Logger getLogger();

    boolean load();

    void onEnable();
    void onDisable();

}
