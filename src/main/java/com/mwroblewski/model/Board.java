package com.mwroblewski.model;

import com.mwroblewski.utils.IUtils;
import lombok.*;

import java.util.Arrays;

@Getter
@Setter
public class Board {

    private int[][] grid;
    private int width;
    private int height;

    public Board(int[][] grid) {
        this.grid = grid;
        this.width = grid[0].length;
        this.height = grid.length;
    }

    private boolean isCorrectPoint(Point point) {
        return 0 <= point.getX() && point.getX() <= this.width
                && 0 <= point.getY() && point.getY() <= this.height;
    }

    public int getSize() {
        return this.width * this.height;
    }

    public boolean isFree(Point point) {
        return isCorrectPoint(point) && this.grid[point.getY()][point.getX()] == 0;
    }

    public boolean isSet(Point point) {
        return isCorrectPoint(point) && this.grid[point.getY()][point.getX()] == 1;
    }

    public boolean isFull() {
        for (int y = 0; y < this.height; y++)
            for (int x = 0; x < this.width; x++)
                if(this.grid[y][x] != 0)
                    return false;
        return true;
    }

    public boolean free(Point point) {
        if (isCorrectPoint(point) && this.isSet(point)) {
            this.grid[point.getY()][point.getX()] = 0;
            return true;
        } else
            return false;
    }

    public boolean set(Point point) {
        if (isCorrectPoint(point) && this.isFree(point)) {
            this.grid[point.getY()][point.getX()] = 1;
            return true;
        } else
            return false;
    }

    public Board getDeepCopy() {
        int[][] copy = new int[this.height][];
        for (int i = 0; i < this.height; i++)
            copy[i] = this.grid[i].clone();

        return new Board(copy);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Board board = (Board) o;
        return Arrays.deepEquals(grid, board.grid);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(grid);
    }
}