package de.bentzin.ingwer.command.node.preset;

import de.bentzin.ingwer.command.ext.CommandData;
import de.bentzin.ingwer.command.node.NodeTrace;

import java.util.function.Predicate;

public class CoordinateNode extends NumberNode{

    //TODO not yet implemented

    public CoordinateNode(String name) {
        super(name);
    }

    public CoordinateNode(String name, Predicate<Integer> condition) {
        super(name, condition);
    }

    @Override
    public void execute(CommandData commandData, NodeTrace nodeTrace, Integer integer) {

    }
}
