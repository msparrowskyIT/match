package com.mwroblewski.aisd.tree;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;

@Getter
@Setter
public class Node<T> {

    private T data;
    private Node parent;
    private LinkedList<Node<T>> children;

    public Node(Node parent, T data) {
        this.parent = parent;
        this.data = data;
        children = new LinkedList<>();
    }

    public Node(T data) {
        this(null, data);
    }

    public Node<T> addChild(Node<T> child) {
        child.setParent(this);
        children.add(child);

        return child;
    }

    public Node<T> addChild(T data) {
        Node<T> child = new Node(this, data);
        children.add(child);

        return child;
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    public Node getLeftMostChild() {
        if (children.isEmpty())
            return null;
        return children.get(0);
    }

    public Node getRightSibling() {
        if (this.parent != null) {
            LinkedList<Node<T>> childrenParent = parent.getChildren();
            int position = childrenParent.indexOf(this);
            if (childrenParent.size() > position+1)
                return childrenParent.get(position+1);
        }
        return null;
    }

    public Node removeChild(int i) {
        return children.remove(i);
    }

    public void removeChildren() {
        children.clear();
    }


    public String toString() {
        return data.toString();
    }

}