package com.mwroblewski.service;

import com.mwroblewski.model.*;
import com.mwroblewski.aisd.tree.Node;
import com.mwroblewski.aisd.tree.Tree;

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

    public Tree<TreeState> getTreeWithSetBoard(Tree<TreeState> tree, Board board) {
        if(board == null)
            System.out.println("pop");
        for (int y = 0; y < board.getHeight(); y++)
            for (int x = 0; x < board.getWidth(); x++) {
                Point point = new Point(x,y);
                if(board.isSet(point)){
                    Node<TreeState> childNode = this.getChildNodeWithSetPoint(tree, point);
                    if(childNode == null)
                        return null;
                    else
                        tree = new Tree<>(childNode);
                }
            }
        return tree;
    }

    public TreeState getTreeStateWithSetBoard(Tree<TreeState> tree, Board board) {
        return this.getTreeWithSetBoard(tree, board).getRoot().getData();
    }

    public TreeState getStateWithSetPoint(Tree<TreeState> tree, Point point) {
        Node<TreeState> nodeWithCoveredPoint = getChildNodeWithSetPoint(tree, point);

        if (nodeWithCoveredPoint == null)
            return null;
        else
            return nodeWithCoveredPoint.getData();
    }

    public TreeState getStateWithSetInnerMotion(Tree<TreeState> tree, InnerMotion innerMotion) {
        Point[] points = innerMotion.getPoints();
        for (int i = 0; i < points.length; i++) {
            tree = new Tree<>(getChildNodeWithSetPoint(tree, points[i]));
        }

        if (tree.getRoot() != null)
            return tree.getRoot().getData();
        else
            return null;
    }

    public TreeState getStateWithSetOutterMotion(Tree<TreeState> tree, OutterMotion outterMotion) {
        return this.getStateWithSetPoint(tree, outterMotion.getPoint());
    }

}
