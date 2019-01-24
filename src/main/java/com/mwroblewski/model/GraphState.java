package com.mwroblewski.model;

import com.mwroblewski.aisd.tree.Tree;
import lombok.*;

@Getter
@Setter
public class GraphState extends TreeState {

    private int graphIndex;
    private String treeKey;
    private Tree<TreeState> tree;

    public GraphState(Board board, int graphIndex, String treeKey) {
        super(board);
        this.graphIndex = graphIndex;
        this.treeKey = treeKey;
    }
}
