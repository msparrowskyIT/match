package com.mwroblewski.aisd.graph;

import lombok.*;

import java.util.Map;

@Getter
@Setter
public class Graph<K, V, T> {

    private Map<K, V> vertices;
    private T[][] adjacencyMatrix;

    public Graph(Map<K,V> vertices, T[][] adjacencyMatrix){
        this.vertices = vertices;
        this.adjacencyMatrix = adjacencyMatrix;
    }

}
