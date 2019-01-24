package com.mwroblewski.model;

import lombok.*;

import java.util.*;

@Getter
@Setter
public class TreeState {

    private Board board;
    private List<InnerMotion> innerMotions;
    private List<OutterMotion> outterMotions;
    private Statistics statistics = new Statistics(0,0);

    public TreeState(Board board) {
        this.board = board;
    }
}
