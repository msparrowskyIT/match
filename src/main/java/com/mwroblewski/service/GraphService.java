package com.mwroblewski.service;

import com.mwroblewski.aisd.graph.Graph;
import com.mwroblewski.common.AdjacencyType;
import com.mwroblewski.model.*;

public class GraphService {

    private BoardService boardService = new BoardService();

    public GraphState getState(Graph<Integer, GraphState, AdjacencyType> graph, Point point, int boardsInRow) {
        int x = point.getX() / 3;
        int y = point.getY() / 3;

        int index = boardsInRow * y + x;
        return graph.getVertices().get(index);
    }

    public GraphState getState(Graph<Integer, GraphState, AdjacencyType> graph, int graphIndex, AdjacencyType type) {
        AdjacencyType[] adjacencyVector = graph.getAdjacencyMatrix()[graphIndex];
        for (int i = 0; i < adjacencyVector.length; i++)
            if (adjacencyVector[i] != null && adjacencyVector[i].equals(type))
                return graph.getVertices().get(i);

        return null;
    }

    public boolean set(Graph<Integer, GraphState, AdjacencyType> graph, Point point, int boardsInRow) {
        GraphState graphState = this.getState(graph, point, boardsInRow);
        Point newPoint = new Point(point.getX() % 3, point.getY() % 3);
        Board newBoard = boardService.set(graphState.getBoard(), newPoint);
        if (newBoard != null) {
            graphState.setBoard(newBoard);
            return true;
        } else
            return false;
    }

    public boolean set(Graph<Integer, GraphState, AdjacencyType> graph, int graphIndex, InnerMotion innerMotion) {
        GraphState graphState = graph.getVertices().get(graphIndex);
        Board newBoard = boardService.set(graphState.getBoard(), innerMotion);
        if (newBoard != null) {
            graphState.setBoard(newBoard);
            return true;
        } else
            return false;
    }

    public boolean set(Graph<Integer, GraphState, AdjacencyType> graph, int graphIndex, OutterMotion outterMotion) {
        GraphState graphState = graph.getVertices().get(graphIndex);
        Board newBoard = boardService.set(graphState.getBoard(), outterMotion.getPoint());

        GraphState adjGraphState = this.getState(graph, graphIndex, outterMotion.getType());
        Board adjBoard = adjGraphState.getBoard();
        Board adjNewBoard = boardService.set(adjBoard, boardService.getAdjacencyPoint(adjBoard, outterMotion));


        if (newBoard != null && adjGraphState != null) {
            graphState.setBoard(newBoard);
            adjGraphState.setBoard(adjNewBoard);
            return true;
        } else
            return false;
    }

    public Point convertPointToGlobal(GraphState graphState, Point point, int boardsInRow) {
        int graphIndex = graphState.getGraphIndex();
        int x = graphIndex % boardsInRow;
        int y = graphIndex / boardsInRow;

        return new Point(x * 3 + point.getX(), y * 3 + point.getY());
    }

    public void showGraph(Graph<Integer, GraphState, AdjacencyType> graph, int boardsInRow) {
        System.out.println();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < boardsInRow; i++) {
            int height = graph.getVertices().get(i * boardsInRow).getBoard().getHeight();
            for (int k = 0; k < height; k++) {
                for (int j = i * boardsInRow; j < (i + 1) * boardsInRow; j++) {
                    Board board = graph.getVertices().get(j).getBoard();
                    int width = board.getWidth();
                    for (int l = 0; l < width; l++) {
                        sb.append(" " + board.getGrid()[k][l]);
                    }
                }
                sb.append("\n");
            }
        }
        System.out.println(sb.toString());
    }
}