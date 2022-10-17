package de.bentzin.ingwer.command.node;

import de.bentzin.ingwer.command.ext.NonFinalPermissiond;

/**
 * @author Ture Bentzin
 * 17.10.2022
 */
public interface PermissionedNode<T> extends Node<T>, NonFinalPermissiond<PermissionedNode<T>> {
}
