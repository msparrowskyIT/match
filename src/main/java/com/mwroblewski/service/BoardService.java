package com.mwroblewski.service;

import com.mwroblewski.common.AdjacencyType;
import com.mwroblewski.model.Board;
import com.mwroblewski.model.InnerMotion;
import com.mwroblewski.model.OutterMotion;
import com.mwroblewski.model.Point;
import com.mwroblewski.utils.Utils;

import java.util.*;

public class BoardService {

    private boolean isCorrectSize(Board b1, Board b2) {
        return b1.getWidth() == b2.getWidth() && b1.getHeight() == b2.getHeight();
    }

    private boolean isCorrectContent(Board b1, Board b2) {
        for (int y = 0; y < b1.getHeight(); y++)
            for (int x = 0; x < b1.getWidth(); x++)
                if (b1.isSet(new Point(x, y)) && b2.isSet(new Point(x, y)))
                    return false;
        return true;
    }

    public Board free(Board board, Point point) {
        Board copy = board.getDeepCopy();
        if (copy.free(point))
            return copy;
        else
            return null;
    }

    public Board free(Board board, InnerMotion motion) {
        Board copy = board.getDeepCopy();

        Point[] points = motion.getPoints();
        for (int i = 0; i < points.length; i++) {
            if (!copy.free(points[i]))
                return null;
        }

        return copy;
    }

    public Board set(Board board, Point point) {
        Board copy = board.getDeepCopy();
        if (copy.set(point))
            return copy;
        else
            return null;
    }

    public Board set(Board board, InnerMotion motion) {
        Board copy = board.getDeepCopy();

        Point[] points = motion.getPoints();
        for (int i = 0; i < points.length; i++) {
            if (!copy.set(points[i]))
                return null;
        }

        return copy;
    }

    public boolean isParentAndChild(Board parent, Board child) {
        if (this.isCorrectSize(parent, child)) {
            for (int y = 0; y < parent.getHeight(); y++)
                for (int x = 0; x < parent.getWidth(); x++)
                    if (parent.isSet(new Point(x, y)) && child.isFree(new Point(x, y)))
                        return false;
            return true;
        } else
            return false;
    }

    public Board merge(Board b1, Board b2) {
        if (isCorrectSize(b1, b2) && isCorrectContent(b1, b2)) {
            Board copy = b1.getDeepCopy();
            for (int y = 0; y < copy.getHeight(); y++)
                for (int x = 0; x < copy.getWidth(); x++)
                    if (b2.getGrid()[y][x] == 1)
                        copy.set(new Point(x, y));
            return copy;
        } else
            return null;
    }

    private void addInnerMotion(int x, int y, InnerMotion.Type type, Set<InnerMotion> innerMotions) {
        Point[] points;
        if (type.equals(InnerMotion.Type.HORIZONTAL))
            points = new Point[]{new Point(x, y), new Point(x + 1, y)};
        else
            points = new Point[]{new Point(x, y), new Point(x, y + 1)};

        innerMotions.add(new InnerMotion(points, type));
    }

    private Set<InnerMotion> getHorizontalInnerMotions(Board board) {
        Set<InnerMotion> motions = new HashSet<>();
        for (int y = 0; y < board.getHeight(); y++)
            for (int x = 0; x < board.getWidth() - 1; x++)
                if (board.getGrid()[y][x] == 0 && board.getGrid()[y][x + 1] == 0)
                    this.addInnerMotion(x, y, InnerMotion.Type.HORIZONTAL, motions);

        return motions;
    }

    private Set<InnerMotion> getVerticalInnerMotions(Board board) {
        Set<InnerMotion> innerMotions = new HashSet<>();
        for (int x = 0; x < board.getWidth(); x++)
            for (int y = 0; y < board.getHeight() - 1; y++)
                if (board.getGrid()[y][x] == 0 && board.getGrid()[y + 1][x] == 0)
                    this.addInnerMotion(x, y, InnerMotion.Type.VERTICAL, innerMotions);

        return innerMotions;
    }

    public Set<InnerMotion> getAllInnerMotions(Board board) {
        Set<InnerMotion> innerMotions = new HashSet<>();

        innerMotions.addAll(this.getHorizontalInnerMotions(board));
        innerMotions.addAll(this.getVerticalInnerMotions(board));

        return innerMotions;
    }

    public Set<OutterMotion> getAllOutterMotions(Board board) {
        Set<OutterMotion> outterMotions = new HashSet<>();

        for (int x = 0; x < board.getWidth(); x++) {
            if (board.getGrid()[0][x] == 0)
                outterMotions.add(new OutterMotion(new Point(x, 0), AdjacencyType.TOP));
            if (board.getGrid()[board.getHeight() - 1][x] == 0)
                outterMotions.add(new OutterMotion(new Point(x, board.getHeight() - 1), AdjacencyType.BOTTOM));
        }

        for (int y = 0; y < board.getHeight(); y++) {
            if (board.getGrid()[y][0] == 0)
                outterMotions.add(new OutterMotion(new Point(0, y), AdjacencyType.LEFT));
            if (board.getGrid()[y][board.getWidth() - 1] == 0)
                outterMotions.add(new OutterMotion(new Point(board.getWidth() - 1, y), AdjacencyType.RIGHT));
        }

        return outterMotions;
    }

    public Point getAdjacencyPoint(Board adjBoard, OutterMotion outterMotion) {
        Point point = outterMotion.getPoint();
        AdjacencyType type = outterMotion.getType();

        if (type.equals(AdjacencyType.TOP))
            return new Point(point.getX(), adjBoard.getHeight() - 1);
        else if (type.equals(AdjacencyType.BOTTOM))
            return new Point(point.getX(), 0);
        else if (type.equals(AdjacencyType.LEFT))
            return new Point(adjBoard.getWidth() - 1, point.getY());
        else
            return new Point(0, point.getY());
    }

}