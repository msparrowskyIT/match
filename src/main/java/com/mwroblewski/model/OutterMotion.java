package com.mwroblewski.model;

import lombok.*;

@Getter
@Setter
public class OutterMotion {

    public enum Type {
        TOP, BOTTOM, LEFT, RIGHT
    }

    private Point point;
    private Type type;
    private Statistics statistics = new Statistics(0,0);

    public OutterMotion(Point point, Type type){
        this.point = point;
        this.type = type;
    }

}
