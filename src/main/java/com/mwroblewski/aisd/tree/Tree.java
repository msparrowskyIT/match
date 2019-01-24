package com.mwroblewski.aisd.tree;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Tree<T> {

    private Node<T> root;

    public Tree() {
        this.root = null;
    }

    public Tree(Node<T> root) {
        this.root = root;
    }

}