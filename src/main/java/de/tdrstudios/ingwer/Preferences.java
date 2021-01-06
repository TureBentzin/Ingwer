package de.tdrstudios.ingwer;

import de.tdrstudios.ingwer.enums.StartType;
import de.tdrstudios.ingwer.identity.AccessType;
import de.tdrstudios.ingwer.identity.Identity;

public class Preferences {
    private Identity adminIdentity;
    private StartType startType;
    private char prefix;

    public  Preferences(Identity adminIdentity , StartType startType) {
        setPrefix('#');
        adminIdentity.setAccessType(AccessType.getbyLevel(10));
        setAdminIdentity(adminIdentity);
        setStartType(startType);
    }
    public Preferences(){}

    public char getPrefix() {
        return prefix;
    }

    public Identity getAdminIdentity() {
        return adminIdentity;
    }

    public StartType getStartType() {
        return startType;
    }

    public void setAdminIdentity(Identity adminIdentity) {
        this.adminIdentity = adminIdentity;
    }

    public void setPrefix(char prefix) {
        this.prefix = prefix;
    }

    public void setStartType(StartType startType) {
        this.startType = startType;
    }
}
