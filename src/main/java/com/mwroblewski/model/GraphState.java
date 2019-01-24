package com.mwroblewski.model;

import com.mwroblewski.aisd.tree.Tree;
import lombok.*;

@Getter
@Setter
public class GraphState extends TreeState {

    public enum AdjacencyType {
        TOP, BOTTOM, LEFT, RIGHT
    }

    private int index;
    private String key;
    private Tree<TreeState> tree;

    public GraphState(Board board, int index, String key) {
        super(board);
        this.index = index;
        this.key = key;
    }
}
