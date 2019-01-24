package com.mwroblewski.manager;

import com.mwroblewski.aisd.graph.Graph;
import com.mwroblewski.aisd.heap.Heap;
import com.mwroblewski.aisd.tree.Tree;
import com.mwroblewski.builder.GraphBuilder;
import com.mwroblewski.builder.TreeBuilder;
import com.mwroblewski.common.AdjacencyType;
import com.mwroblewski.model.*;
import com.mwroblewski.service.BoardService;
import com.mwroblewski.service.GraphService;
import com.mwroblewski.service.ParseService;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class MatchManager {

    private ParseService parseService = new ParseService();
    private BoardService boardService = new BoardService();
    private GraphService graphService = new GraphService();

    private int matchSize;
    private int boardsInRow;
    private Graph<Integer, GraphState, AdjacencyType> graph;
    private Map<String, Tree<TreeState>> trees;
    private Comparator<GraphState> ascGraphStateSizeComparator = new Comparator<GraphState>() {
        @Override
        public int compare(GraphState s1, GraphState s2) {
            int size1 = s1.getBoard().getSize();
            int size2 = s2.getBoard().getSize();

            return size1 > size2 ? -1 : (size1 < size2 ? 1 : 0);
        }
    };
    private Comparator<GraphState> descGraphStateRateComparator = new Comparator<GraphState>() {
        @Override
        public int compare(GraphState s1, GraphState s2) {
            double rate1 = s1.getStatistics().getRate();
            double rate2 = s2.getStatistics().getRate();

            return rate1 > rate2 ? 1 : (rate1 < rate2 ? -1 : 0);
        }
    };

    private Point[] preparePointsToSend(GraphState graphState, InnerMotion innerMotion) {
        Point[] points = new Point[2];
        int i = 0;
        for (Point point : innerMotion.getPoints()) {
            points[i++] = graphService.convertPointToGlobal(graphState, point, this.boardsInRow);
        }

        return points;
    }

    private Point[] preparePointsToSend(GraphState graphState, OutterMotion outterMotion) {
        Point[] points = new Point[2];
        points[0] = graphService.convertPointToGlobal(graphState, outterMotion.getPoint(), this.boardsInRow);

        GraphState adjGraphState = graphService.getState(this.graph, graphState.getGraphIndex(), outterMotion.getType());
        Point adjPoint = boardService.getAdjacencyPoint(adjGraphState.getBoard(), outterMotion);
        points[1] = graphService.convertPointToGlobal(adjGraphState, adjPoint, this.boardsInRow);

        return points;
    }

    private Set<String> getBoardSizes(int matchSize) {
        Set<String> boardSizes = new HashSet<>();
        boardSizes.add("3x3");

        int r = matchSize % 3;
        if (r != 0) {
            boardSizes.add("3x" + r);
            boardSizes.add(r + "x3");
            boardSizes.add(r + "x" + r);
        }

        return boardSizes;
    }

    private void buildGraph() {
        this.matchSize = parseService.parseMatchSize();
        this.boardsInRow = matchSize % 3 == 0 ? matchSize / 3 : matchSize / 3 + 1;
        this.graph = GraphBuilder.build(this.matchSize);

        System.out.print("ok");
    }

    private void sendPoints(Point[] points) {
        System.out.print(parseService.parsePoints(points));
    }

    private void setGraph(Point[] points) {
        for (Point point : points) {
            graphService.set(graph, point, boardsInRow);
        }
    }

    private void setGraphWithOk() {
        Point[] points = parseService.parsePoints();
        this.setGraph(points);

        System.out.print("ok");
    }

    private void setGraphWithPoints(Point[] points) {
        this.sendPoints(points);
        this.setGraph(points);
    }

    private Heap<GraphState> buildHeap(Comparator<GraphState> comparator) {
        Collection<GraphState> values = this.graph.getVertices().values();
        GraphState[] graphStates = values.toArray(new GraphState[values.size()]);

        return new Heap<>(graphStates, comparator, GraphState.class);
    }

    private InnerMotion getInnerMotionWithoutTrees(GraphState graphState) {
        Set<InnerMotion> innerMotions = this.boardService.getAllInnerMotions(graphState.getBoard());
        if (innerMotions.size() > 0)
            return innerMotions.iterator().next();
        else
            return null;
    }

    private OutterMotion getOutterMotionWithoutTrees(GraphState graphState) {
        for (AdjacencyType adjType : AdjacencyType.values()) {
            List<OutterMotion> outterMotions = this.boardService.getAllOutterMotions(graphState.getBoard())
                    .stream()
                    .filter(m -> adjType.equals(m.getType()))
                    .collect(Collectors.toList());

            if (outterMotions.size() != 0) {
                GraphState adjGraphState = graphService.getState(this.graph, graphState.getGraphIndex(), adjType);
                Optional<OutterMotion> optOutterMotion = outterMotions
                        .stream()
                        .filter(m -> {
                            Point adjPoint = boardService.getAdjacencyPoint(adjGraphState.getBoard(), m);
                            return adjGraphState.getBoard().isFree(adjPoint);
                        })
                        .findFirst();

                if (optOutterMotion.isPresent())
                    return optOutterMotion.get();
            }
        }

        return null;
    }

    private void motionWithoutTrees() {
        Heap<GraphState> graphStateHeap = this.buildHeap(ascGraphStateSizeComparator);
        GraphState graphState = graphStateHeap.pop();
        while (graphState != null) {
            InnerMotion innerMotion = this.getInnerMotionWithoutTrees(graphState);
            if (innerMotion != null) {
                Point[] points = this.preparePointsToSend(graphState, innerMotion);
                this.setGraphWithPoints(points);
                return;
            } else {
                OutterMotion outterMotion = this.getOutterMotionWithoutTrees(graphState);
                if (outterMotion != null) {
                    Point[] points = this.preparePointsToSend(graphState, outterMotion);
                    this.setGraphWithPoints(points);
                    return;
                }
            }
            graphState = graphStateHeap.pop();
        }

        System.out.print("Losses :(");
    }

    public void game() {
        this.buildGraph();
        
//        new Thread() {
//            public void run() {
//                buildTrees();
//            }
//        }.start();

        while (true) {
            this.setGraphWithOk();
            graphService.showGraph(this.graph, boardsInRow);
            this.motionWithoutTrees();
            graphService.showGraph(this.graph, boardsInRow);

        }

    }

    private void buildTrees() {
        Set<String> boardSizes = this.getBoardSizes(this.matchSize);
        Map<String, Tree<TreeState>> trees = new HashMap<>();

        ExecutorService executorService = Executors.newFixedThreadPool(boardSizes.size());
        List<Callable<Void>> tasks = new ArrayList<>();
        boardSizes.forEach(s -> {
            tasks.add(() -> {
                Tree<TreeState> tree = TreeBuilder.build(Integer.valueOf(s.substring(0, 1)), Integer.valueOf(s.substring(2)));
                trees.put(s, tree);
                return null;
            });
        });


        try {
            List<Future<Void>> futures = executorService.invokeAll(tasks);
            for (Future<Void> future : futures) {
                try {
                    future.get();

                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt(); // ignore/reset
                }
            }
        } catch (Exception err) {
            err.printStackTrace();
        }

        this.trees = trees;
    }


}