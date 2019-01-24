package com.mwroblewski.model;

import com.mwroblewski.common.AdjacencyType;
import lombok.*;

@Getter
@Setter
public class OutterMotion {

    private Point point;
    private Point adjPoint;
    private AdjacencyType type;
    private Statistics statistics = new Statistics();

    public OutterMotion(Point point, Point adjPoint, AdjacencyType type){
        this.point = point;
        this.adjPoint = adjPoint;
        this.type = type;
    }

}
