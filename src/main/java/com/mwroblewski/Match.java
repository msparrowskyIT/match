package com.mwroblewski;

import com.mwroblewski.aisd.graph.Graph;
import com.mwroblewski.aisd.heap.Heap;
import com.mwroblewski.aisd.tree.Tree;
import com.mwroblewski.builder.GraphBuilder;
import com.mwroblewski.builder.TreeBuilder;
import com.mwroblewski.common.AdjacencyType;
import com.mwroblewski.manager.MatchManager;
import com.mwroblewski.model.Point;
import com.mwroblewski.model.TreeState;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Match {

    public static void main(String[] args) {
        MatchManager m = new MatchManager();
        m.game();
    }

}
