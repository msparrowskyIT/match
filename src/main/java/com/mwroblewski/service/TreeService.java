package com.mwroblewski.service;

import com.mwroblewski.model.InnerMotion;
import com.mwroblewski.model.OutterMotion;
import com.mwroblewski.model.Point;
import com.mwroblewski.model.TreeState;
import com.mwroblewski.aisd.tree.Node;
import com.mwroblewski.aisd.tree.Tree;

import java.util.Optional;

public class TreeService<T extends TreeState> {

    private Node<T> getChildNodeWithSetPoint(Tree<T> tree, Point point) {
        if (tree.getRoot() == null)
            return null;

        Optional<Node<T>> optionalChildNode = tree.getRoot().getChildren()
                .stream()
                .filter(childNode -> {
                    return childNode.getData().getBoard().isSet(point);
                })
                .findFirst();

        if (optionalChildNode.isPresent())
            return optionalChildNode.get();
        else
            return null;
    }

    public TreeState getStateWithSetPoint(Tree<T> tree, Point point) {
        Node<T> nodeWithCoveredPoint = getChildNodeWithSetPoint(tree, point);

        if (nodeWithCoveredPoint == null)
            return null;
        else
            return nodeWithCoveredPoint.getData();
    }

    public TreeState getStateWithSetInnerMotion(Tree<T> tree, InnerMotion innerMotion) {
        Point[] points = innerMotion.getPoints();
        for (int i = 0; i < points.length; i++) {
            tree = new Tree<>(getChildNodeWithSetPoint(tree, points[i]));
        }

        if (tree.getRoot() != null)
            return tree.getRoot().getData();
        else
            return null;
    }

    public TreeState getStateWithSetOutterMotion(Tree<T> tree, OutterMotion outterMotion) {
        return this.getStateWithSetPoint(tree, outterMotion.getPoint());
    }

}
