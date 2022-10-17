package de.bentzin.ingwer.command.ext;

import de.bentzin.ingwer.identity.permissions.IngwerPermission;

/**
 * @author Ture Bentzin
 * 17.10.2022
 * @see de.bentzin.ingwer.command.node.Node
 */
public interface NonFinalPermissiond<T extends NonFinalPermissiond<T>> extends Permissioned {
    /**
     * @param ingwerPermission new permission
     * @return implementation of this to support builder structure
     */
    T permission(IngwerPermission ingwerPermission);
}
