package com.mwroblewski.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
public class InnerMotion {

    public enum Type {
        HORIZONTAL, VERTICAL
    }

    private Point[] points;
    private Type type;
    private Statistics statistics = new Statistics();

    public InnerMotion(Point[] points) {
        this.points = points;
        this.type = this.setType(points);
    }

    public InnerMotion(Point[] points, Type type) {
        this(points);
        this.type = type;
    }

    private InnerMotion.Type setType(Point... points) {
        if (points.length == 1)
            return null;

        boolean isHorizontal = true;
        boolean isVertical = true;
        for (int i = 0; i < points.length - 1; i++) {
            if (isHorizontal && points[i].getX() != points[i + 1].getX())
                isHorizontal = false;
            if (isVertical && points[i].getY() != points[i + 1].getY())
                isVertical = false;
            if (points[i].equals(points[i + 1]))
                return null;
        }

        return isHorizontal ? InnerMotion.Type.HORIZONTAL : (isVertical ? InnerMotion.Type.VERTICAL : null);
    }

}