package com.mwroblewski.model;

import com.mwroblewski.aisd.tree.Tree;
import lombok.*;

@Getter
@Setter
public class GraphState extends TreeState {

    private int graphKey;
    private String treeKey;
    private Tree<TreeState> tree;

    public GraphState(Board board, int graphKey, String treeKey) {
        super(board);
        this.graphKey = graphKey;
        this.treeKey = treeKey;
    }
}
