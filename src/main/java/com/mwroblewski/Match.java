package com.mwroblewski;

import com.mwroblewski.aisd.graph.Graph;
import com.mwroblewski.aisd.tree.Tree;
import com.mwroblewski.builder.GraphBuilder;
import com.mwroblewski.builder.TreeBuilder;
import com.mwroblewski.common.AdjacencyType;
import com.mwroblewski.model.TreeState;

public class Match {

    public static void main(String[] args) {
        long s = System.currentTimeMillis();
        Tree<TreeState> t = TreeBuilder.build(1, 1);
//        Graph<Integer, TreeState, AdjacencyType> i = GraphBuilder.build(4);
        System.out.print(System.currentTimeMillis() - s);
    }

}
