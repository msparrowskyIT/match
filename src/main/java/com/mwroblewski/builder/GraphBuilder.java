package com.mwroblewski.builder;

import com.mwroblewski.aisd.graph.Graph;
import com.mwroblewski.model.Board;
import com.mwroblewski.model.TreeState;

import java.util.HashMap;
import java.util.Map;

public class GraphBuilder {

//    private static AdjacencyType[][] createAdjacencyMatrix(int n) {
//        int boardNum = n%3 == 0 ? n/3 : n/3 + 1;
//
//        AdjacencyType[][] adjacencyMatrix = new AdjacencyType[boardNum * boardNum][boardNum * boardNum];
//        for (int i = 0; i < boardNum * boardNum; i++) {
//            if (i >= boardNum)
//                adjacencyMatrix[i][i - boardNum] = AdjacencyType.TOP;
//
//            if (i < boardNum * (boardNum - 1))
//                adjacencyMatrix[i][i + boardNum] = AdjacencyType.BOTTOM;
//
//            if (i % boardNum == 0)
//                adjacencyMatrix[i][i + boardNum - 1] = AdjacencyType.LEFT;
//            else
//                adjacencyMatrix[i][i - 1] = AdjacencyType.LEFT;
//
//            if (i % boardNum == boardNum - 1)
//                adjacencyMatrix[i][i - boardNum + 1] = AdjacencyType.RIGHT;
//            else
//                adjacencyMatrix[i][i + 1] = AdjacencyType.RIGHT;
//        }
//
//        return adjacencyMatrix;
//    }
//
//    private static Map<Integer, TreeState> createVertices(int n) {
//        int size = n%3 == 0 ? n/3 : n/3 + 1;
//
//        Map<Integer, TreeState> vertices = new HashMap<>();
//        int k = 0;
//        for (int i = 0; i < size; i++)
//            for (int j = 0; j < size; j++) {
//                int width = (j + 1) * 3 <= n ? 3 : n - j * 3;
//                int height = (i + 1) * 3 <= n ? 3 : n - i * 3;
//
//                vertices.put(k++, new TreeState(new Board(new int[height][width])));
//            }
//
//        return vertices;
//    }
//
//    public static Graph<Integer, TreeState, AdjacencyType> build(int n) {
//        return new Graph<Integer, TreeState, AdjacencyType>(createVertices(n), createAdjacencyMatrix(n));
//    }

}