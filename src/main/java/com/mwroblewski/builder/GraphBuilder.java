package com.mwroblewski.builder;

import com.mwroblewski.aisd.graph.Graph;
import com.mwroblewski.model.Board;
import com.mwroblewski.model.GraphState;
import com.mwroblewski.model.TreeState;

import java.util.HashMap;
import java.util.Map;

public class GraphBuilder {

    private static GraphState.AdjacencyType[][] createAdjacencyMatrix(int boardNum) {
        GraphState.AdjacencyType[][] adjacencyMatrix = new GraphState.AdjacencyType[boardNum * boardNum][boardNum * boardNum];
        for (int i = 0; i < boardNum * boardNum; i++) {
            if (i >= boardNum)
                adjacencyMatrix[i][i - boardNum] = GraphState.AdjacencyType.TOP;

            if (i < boardNum * (boardNum - 1))
                adjacencyMatrix[i][i + boardNum] = GraphState.AdjacencyType.BOTTOM;

            if (i % boardNum == 0)
                adjacencyMatrix[i][i + boardNum - 1] = GraphState.AdjacencyType.LEFT;
            else
                adjacencyMatrix[i][i - 1] = GraphState.AdjacencyType.LEFT;

            if (i % boardNum == boardNum - 1)
                adjacencyMatrix[i][i - boardNum + 1] = GraphState.AdjacencyType.RIGHT;
            else
                adjacencyMatrix[i][i + 1] = GraphState.AdjacencyType.RIGHT;
        }

        return adjacencyMatrix;
    }

    private static Map<Integer, TreeState> createVertices(int matchSize, int boardNum) {
        Map<Integer, TreeState> vertices = new HashMap<>();
        int index = 0;
        for (int i = 0; i < boardNum; i++)
            for (int j = 0; j < boardNum; j++) {
                int width = (j + 1) * 3 <= matchSize ? 3 : matchSize - j * 3;
                int height = (i + 1) * 3 <= matchSize ? 3 : matchSize - i * 3;
                String key = width + "x" + height;
                vertices.put(index++, new GraphState(new Board(new int[height][width]), index, key));
            }

        return vertices;
    }

    public static Graph<Integer, TreeState, GraphState.AdjacencyType> build(int matchSize) {
        int boardNum = matchSize%3 == 0 ? matchSize/3 : matchSize/3 + 1;
        return new Graph<Integer, TreeState, GraphState.AdjacencyType>(createVertices(matchSize, boardNum), createAdjacencyMatrix(boardNum));
    }

}