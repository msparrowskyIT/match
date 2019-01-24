package com.mwroblewski.service;

import com.mwroblewski.model.Point;

import java.util.Scanner;

public class ParseService {

    public int parseMatchSize() {
        return Integer.valueOf(new Scanner(System.in).next());
    }

    public Point[] parsePoints() {
        Scanner sc = new Scanner(System.in);
        String[] strPoints = sc.next().split(",");

        Point[] points = new Point[strPoints.length];
        for (int i = 0; i < strPoints.length; i++) {
            String strPoint = strPoints[i];
            int splitIndex = strPoint.indexOf(";");
            int x = Integer.valueOf(strPoint.substring(1, splitIndex));
            int y = Integer.valueOf(strPoint.substring(splitIndex + 1, strPoint.length() - 1));
            points[i] = new Point(x, y);
        }

        return points;
    }

    public String parsePoints(Point[] points) {
        StringBuilder sb = new StringBuilder();
        for (Point point : points)
            sb.append("{" + point.getX() + ";" + point.getY() + "},");

        return sb.substring(0, sb.length() - 1);
    }

}
