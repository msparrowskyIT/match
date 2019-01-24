package com.mwroblewski;

import com.mwroblewski.aisd.tree.Tree;
import com.mwroblewski.builder.TreeBuilder;
import com.mwroblewski.model.TreeState;

public class Match {

    public static void main(String[] args) {
        long s = System.currentTimeMillis();
        Tree<TreeState> t = TreeBuilder.build(5, 5);
        System.out.print(System.currentTimeMillis() - s);
    }

}
