package com.mwroblewski.model;

import lombok.*;

@Getter
@Setter
public class GraphState extends TreeState {

    public enum AdjacencyType {
        TOP, BOTTOM, LEFT, RIGHT
    }


    public GraphState(Board board) {
        super(board);
    }
}
