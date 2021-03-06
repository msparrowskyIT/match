package com.mwroblewski.builder;

import com.mwroblewski.aisd.graph.Graph;
import com.mwroblewski.common.AdjacencyType;
import com.mwroblewski.model.Board;
import com.mwroblewski.model.GraphState;

import java.util.HashMap;
import java.util.Map;

public class GraphBuilder {

    private static AdjacencyType[][] createAdjacencyMatrix(int boardsInRow) {
        AdjacencyType[][] adjacencyMatrix = new AdjacencyType[boardsInRow * boardsInRow][boardsInRow * boardsInRow];
        for (int i = 0; i < boardsInRow * boardsInRow; i++) {

            if (i >= 0 && i < boardsInRow)
                adjacencyMatrix[i][(boardsInRow - 1) * boardsInRow + (i%boardsInRow)] = AdjacencyType.TOP;
            else
                adjacencyMatrix[i][i - boardsInRow] = AdjacencyType.TOP;

            if (i >= (boardsInRow-1)*boardsInRow && i < boardsInRow * boardsInRow)
                adjacencyMatrix[i][i % boardsInRow] = AdjacencyType.BOTTOM;
            else
                adjacencyMatrix[i][i + boardsInRow] = AdjacencyType.BOTTOM;

//            if (i >= boardsInRow)
//                adjacencyMatrix[i][i - boardsInRow] = AdjacencyType.TOP;
//
//            if (i < boardsInRow * (boardsInRow - 1))
//                adjacencyMatrix[i][i + boardsInRow] = AdjacencyType.BOTTOM;

            if (i % boardsInRow == 0)
                adjacencyMatrix[i][i + boardsInRow - 1] = AdjacencyType.LEFT;
            else
                adjacencyMatrix[i][i - 1] = AdjacencyType.LEFT;

            if (i % boardsInRow == boardsInRow - 1)
                adjacencyMatrix[i][i - boardsInRow + 1] = AdjacencyType.RIGHT;
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
        int boardsNum = matchSize % 3 == 0 ? matchSize / 3 : matchSize / 3 + 1;
        return new Graph<Integer, GraphState, AdjacencyType>(createVertices(matchSize, boardsNum), createAdjacencyMatrix(boardsNum));
    }

}