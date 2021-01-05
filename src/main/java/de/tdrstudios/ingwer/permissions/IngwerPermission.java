package de.tdrstudios.ingwer.permissions;

import java.security.Permission;

public class IngwerPermission {

    private String id = "null.null";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private IngwerPermission[] childs;

    public void setChilds(IngwerPermission[] childs) {
        this.childs = childs;
    }

    public IngwerPermission[] getChilds() {
        return childs;
    }

    public void addChield(IngwerPermission ingwerPermission) {
        IngwerPermission[] permissions = getChilds();
        boolean end = false;
        for (IngwerPermission perm : permissions) {
            if (perm != null) {

            }else {
                if(!end)
                perm = ingwerPermission;
            }
        }
    }
}

