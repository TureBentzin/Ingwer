package de.bentzin.ingwer.features.integrated;

import de.bentzin.ingwer.features.SimpleFeature;
import de.bentzin.ingwer.identity.permissions.IngwerPermission;

public class InternalFeature  extends SimpleFeature {
    public InternalFeature() {
        super("internal","internal");
    }

    @Override
    public IngwerPermission generalUsePermission() {
        return null;
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public boolean onLoad() {
        return false;
    }
}
