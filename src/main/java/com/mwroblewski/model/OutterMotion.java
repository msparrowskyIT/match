package com.mwroblewski.model;

import com.mwroblewski.common.AdjacencyType;
import lombok.*;

@Getter
@Setter
public class OutterMotion {

    private Point point;
    private AdjacencyType type;
    private Statistics statistics = new Statistics();

    public OutterMotion(Point point, AdjacencyType type){
        this.point = point;
        this.type = type;
    }

}
