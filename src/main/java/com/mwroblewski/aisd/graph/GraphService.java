package com.mwroblewski.aisd.graph;

import com.mwroblewski.model.TreeState;

import java.util.HashSet;
import java.util.Set;

public class GraphService/*<K extends Integer, V extends TreeState, T extends AdjacencyType>*/ {

//    private BoardService boardService = new BoardService();
//
//    public Set<String> getSizes(int gameSize){
//        Set<String> sizes = new HashSet<>();
//        sizes.add("3x3");
//
//        int r = gameSize%3;
//        if(r != 0){
//            sizes.add("3x"+r);
//            sizes.add(r+"x3");
//            sizes.add(r+"x"+r);
//        }
//
//        return sizes;
//    }
//
//    public V getState(Graph<K, V, T> graph, Point point, int n) {
//        int size = n%3 == 0 ? n/3 : n/3 + 1;
//        int width = point.getX()/3;
//        int height = point.getY()/3;
//
//        int stateIndex = size*height + width;
//        return graph.getVertices().get(stateIndex);
//    }
//
//    public V getState(Graph<K, V, T> graph, int stateIndex, AdjacencyType type) {
//        AdjacencyType[] adjList = graph.getAdjacencyMatrix()[stateIndex];
//
//        int adjStateIndex = -1;
//        for(int i = 0; i < adjList.length; i++){
//            if(adjList[i].equals(type)){
//                adjStateIndex = i;
//                break;
//            }
//        }
//
//        return adjStateIndex != -1 ? graph.getVertices().get(adjStateIndex): null;
//    }
//
//    public void coverPoint(Graph<K, V, T> graph, Point point, int n) throws IncorrectPointException {
//        V state = this.getState(graph, point, n);
//
//        Point newPoint = new Point(point.getX()%3, point.getY()%3);
//        boardService.set(state.getBoard(), newPoint, true);
//    }
//
//    public void coverPoints(Graph<K, V, T> graph, int n, Point ... points) throws IncorrectPointException {
//        for(int i = 0; i < points.length; i++) {
//            this.coverPoint(graph, points[i], n);
//        }
//
//
//    }

}
