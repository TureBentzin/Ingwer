package de.bentzin.ingwer.features.integrated;

import de.bentzin.ingwer.features.NewFeature;
import de.bentzin.ingwer.features.SimpleFeature;
import de.bentzin.ingwer.identity.permissions.IngwerPermission;

@NewFeature(author = "Ture Bentzin", version = "1.0")
public class WelcomeFeature extends SimpleFeature {
    public WelcomeFeature() {
        super("welcome","welcomes every ingwer user!");
    }

    @Override
    public IngwerPermission generalUsePermission() {
        return IngwerPermission.FEATURE_WELCOME;
    }

    @Override
    public void onEnable() {
        System.out.println("welcome....");
    }

    @Override
    public void onDisable() {

    }

    @Override
    public boolean onLoad() {
        return true;
    }
}
