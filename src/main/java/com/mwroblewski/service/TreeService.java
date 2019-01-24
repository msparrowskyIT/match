package com.mwroblewski.service;

import com.mwroblewski.model.InnerMotion;
import com.mwroblewski.model.OutterMotion;
import com.mwroblewski.model.Point;
import com.mwroblewski.aisd.tree.Node;
import com.mwroblewski.aisd.tree.Tree;
import com.mwroblewski.model.TreeState;

import java.util.Optional;

public class TreeService {

    private Node<TreeState> getChildNodeWithSetPoint(Tree<TreeState> tree, Point point) {
        if (tree.getRoot() == null)
            return null;

        Optional<Node<TreeState>> optionalChildNode = tree.getRoot().getChildren()
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

    public com.mwroblewski.model.TreeState getStateWithSetPoint(Tree<TreeState> tree, Point point) {
        Node<TreeState> nodeWithCoveredPoint = getChildNodeWithSetPoint(tree, point);

        if (nodeWithCoveredPoint == null)
            return null;
        else
            return nodeWithCoveredPoint.getData();
    }

    public com.mwroblewski.model.TreeState getStateWithSetInnerMotion(Tree<TreeState> tree, InnerMotion innerMotion) {
        Point[] points = innerMotion.getPoints();
        for (int i = 0; i < points.length; i++) {
            tree = new Tree<>(getChildNodeWithSetPoint(tree, points[i]));
        }

        if (tree.getRoot() != null)
            return tree.getRoot().getData();
        else
            return null;
    }

    public com.mwroblewski.model.TreeState getStateWithSetOutterMotion(Tree<TreeState> tree, OutterMotion outterMotion) {
        return this.getStateWithSetPoint(tree, outterMotion.getPoint());
    }

}
