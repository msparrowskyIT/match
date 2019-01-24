package com.mwroblewski.service;

import com.mwroblewski.aisd.graph.Graph;
import com.mwroblewski.common.AdjacencyType;
import com.mwroblewski.model.*;

public class GraphService {

    private BoardService boardService = new BoardService();

    private GraphState getState(Graph<Integer, GraphState, AdjacencyType> graph, Point point, int boardNum) {
        int x = point.getX() / 3;
        int y = point.getY() / 3;

        int index = boardNum * y + x;
        return graph.getVertices().get(index);
    }

    private GraphState getState(Graph<Integer, GraphState, AdjacencyType> graph, int index, AdjacencyType type) {
        AdjacencyType[] adjacencyVector = graph.getAdjacencyMatrix()[index];
        for (int i = 0; i < adjacencyVector.length; i++)
            if (adjacencyVector[i].equals(type))
                return graph.getVertices().get(i);

        return null;
    }

    public boolean set(Graph<Integer, GraphState, AdjacencyType> graph, Point point, int boardNum) {
        GraphState graphState = this.getState(graph, point, boardNum);
        Point newPoint = new Point(point.getX() % 3, point.getY() % 3);
        Board newBoard = boardService.set(graphState.getBoard(), newPoint);
        if (newBoard != null) {
            graphState.setBoard(newBoard);
            return true;
        } else
            return false;
    }

    public boolean set(Graph<Integer, GraphState, AdjacencyType> graph, int index, InnerMotion innerMotion) {
        GraphState graphState = graph.getVertices().get(index);
        Board newBoard = boardService.set(graphState.getBoard(), innerMotion);
        if (newBoard != null) {
            graphState.setBoard(newBoard);
            return true;
        } else
            return false;
    }

    public boolean set(Graph<Integer, GraphState, AdjacencyType> graph, int index, OutterMotion outterMotion) {
        GraphState graphState = graph.getVertices().get(index);
        Board newBoard = boardService.set(graphState.getBoard(), outterMotion.getPoint());

        GraphState adjGraphState = this.getState(graph, index, outterMotion.getType());
        Board adjNewBoard = boardService.set(adjGraphState.getBoard(), outterMotion.getAdjPoint());


        if (newBoard != null && adjGraphState != null) {
            graphState.setBoard(newBoard);
            adjGraphState.setBoard(adjNewBoard);
            return true;
        } else
            return false;
    }

}