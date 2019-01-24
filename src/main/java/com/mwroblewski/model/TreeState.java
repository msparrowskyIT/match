package com.mwroblewski.model;

import lombok.*;

import java.util.*;

@Getter
@Setter
public class TreeState {

    private Board board;
    private List<InnerMotion> innerMotions = new ArrayList<>();
    private List<OutterMotion> outterMotions = new ArrayList<>();
    private Statistics statistics = new Statistics();

    public TreeState(Board board) {
        this.board = board;
    }
}
