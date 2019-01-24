package com.mwroblewski.builder;

import com.mwroblewski.aisd.graph.Graph;
import com.mwroblewski.common.AdjacencyType;
import com.mwroblewski.model.Board;
import com.mwroblewski.model.GraphState;
import com.mwroblewski.model.TreeState;

import java.util.HashMap;
import java.util.Map;

public class GraphBuilder {

    private static AdjacencyType[][] createAdjacencyMatrix(int boardNum) {
        AdjacencyType[][] adjacencyMatrix = new AdjacencyType[boardNum * boardNum][boardNum * boardNum];
        for (int i = 0; i < boardNum * boardNum; i++) {
            if (i >= boardNum)
                adjacencyMatrix[i][i - boardNum] = AdjacencyType.TOP;

            if (i < boardNum * (boardNum - 1))
                adjacencyMatrix[i][i + boardNum] = AdjacencyType.BOTTOM;

            if (i % boardNum == 0)
                adjacencyMatrix[i][i + boardNum - 1] = AdjacencyType.LEFT;
            else
                adjacencyMatrix[i][i - 1] = AdjacencyType.LEFT;

            if (i % boardNum == boardNum - 1)
                adjacencyMatrix[i][i - boardNum + 1] = AdjacencyType.RIGHT;
            else
                adjacencyMatrix[i][i + 1] = AdjacencyType.RIGHT;
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
                vertices.put(index, new GraphState(new Board(new int[height][width]), index, key));
                index++;
            }

        return vertices;
    }

    public static Graph<Integer, TreeState, AdjacencyType> build(int matchSize) {
        int boardNum = matchSize%3 == 0 ? matchSize/3 : matchSize/3 + 1;
        return new Graph<Integer, TreeState, AdjacencyType>(createVertices(matchSize, boardNum), createAdjacencyMatrix(boardNum));
    }

}