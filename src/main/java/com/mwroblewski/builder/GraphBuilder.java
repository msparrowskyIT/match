package com.mwroblewski.builder;

import com.mwroblewski.aisd.graph.Graph;
import com.mwroblewski.common.AdjacencyType;
import com.mwroblewski.model.Board;
import com.mwroblewski.model.GraphState;

import java.util.HashMap;
import java.util.Map;

public class GraphBuilder {

    private static AdjacencyType[][] createAdjacencyMatrix(int boardsNum) {
        AdjacencyType[][] adjacencyMatrix = new AdjacencyType[boardsNum * boardsNum][boardsNum * boardsNum];
        for (int i = 0; i < boardsNum * boardsNum; i++) {
            if (i >= boardsNum)
                adjacencyMatrix[i][i - boardsNum] = AdjacencyType.TOP;

            if (i < boardsNum * (boardsNum - 1))
                adjacencyMatrix[i][i + boardsNum] = AdjacencyType.BOTTOM;

            if (i % boardsNum == 0)
                adjacencyMatrix[i][i + boardsNum - 1] = AdjacencyType.LEFT;
            else
                adjacencyMatrix[i][i - 1] = AdjacencyType.LEFT;

            if (i % boardsNum == boardsNum - 1)
                adjacencyMatrix[i][i - boardsNum + 1] = AdjacencyType.RIGHT;
            else
                adjacencyMatrix[i][i + 1] = AdjacencyType.RIGHT;
        }

        return adjacencyMatrix;
    }

    private static Map<Integer, GraphState> createVertices(int matchSize, int boardsNum) {
        Map<Integer, GraphState> vertices = new HashMap<>();
        int index = 0;
        for (int i = 0; i < boardsNum; i++)
            for (int j = 0; j < boardsNum; j++) {
                int width = (j + 1) * 3 <= matchSize ? 3 : matchSize - j * 3;
                int height = (i + 1) * 3 <= matchSize ? 3 : matchSize - i * 3;
                String key = width + "x" + height;
                vertices.put(index, new GraphState(new Board(new int[height][width]), index, key));
                index++;
            }

        return vertices;
    }

    public static Graph<Integer, GraphState, AdjacencyType> build(int matchSize) {
        int boardsNum = matchSize%3 == 0 ? matchSize/3 : matchSize/3 + 1;
        return new Graph<Integer, GraphState, AdjacencyType>(createVertices(matchSize, boardsNum), createAdjacencyMatrix(boardsNum));
    }

}