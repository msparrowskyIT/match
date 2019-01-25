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
import com.mwroblewski.service.TreeService;

import java.util.*;
import java.util.stream.Collectors;

public class MatchManager {

    private ParseService parseService = new ParseService();
    private BoardService boardService = new BoardService();
    private TreeService treeService = new TreeService();
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
    private boolean block = false;

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

        if (this.trees.size() > 0)
            this.fillGraph();
    }

    private void setGraphRequest() {
        Point[] points = parseService.parsePoints();
        this.setGraph(points);

        System.out.print("ok");
    }

    private void setGraphResponse(Point[] points) {
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

                if(adjGraphState == null)
                    continue;

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

    private OutterMotion getOutterMotionWithTrees(GraphState graphState) {
        for (AdjacencyType adjType : AdjacencyType.values()) {
            List<OutterMotion> outterMotions = graphState.getOutterMotions()
                    .stream()
                    .filter(m -> adjType.equals(m.getType()))
                    .collect(Collectors.toList());

            if (outterMotions.size() != 0) {
                GraphState adjGraphState = graphService.getState(this.graph, graphState.getGraphIndex(), adjType);

                if(adjGraphState == null)
                    continue;

                Tree<TreeState> adjTree = this.trees.get(adjGraphState.getTreeKey());
                Optional<OutterMotion> optOutterMotion = outterMotions
                        .stream()
                        .filter(m -> {
                            Point adjPoint = boardService.getAdjacencyPoint(adjGraphState.getBoard(), m);
                            if(adjGraphState.getBoard().isSet(adjPoint))
                                return false;

                            Board adjBoard = boardService.set(adjGraphState.getBoard(), adjPoint);
                            double adjRate = treeService.getTreeStateWithSetBoard(adjTree, adjBoard).getStatistics().getRate();
                            m.setAdjRate(adjRate);

                            return adjGraphState.getBoard().isFree(adjPoint) && adjRate >= adjGraphState.getStatistics().getRate();
                        })
                        .max((m1, m2) -> {
                            double adjRate1 = m1.getAdjRate();
                            double adjRate2 = m2.getAdjRate();

                            return adjRate1 > adjRate2 ? 1 : (adjRate1 < adjRate2 ? -1 : 0);
                        });

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
                this.setGraphResponse(points);
                return;
            } else {
                OutterMotion outterMotion = this.getOutterMotionWithoutTrees(graphState);
                if (outterMotion != null) {
                    Point[] points = this.preparePointsToSend(graphState, outterMotion);
                    this.setGraphResponse(points);
                    return;
                }
            }
            graphState = graphStateHeap.pop();
        }

        System.out.print("Losses :(\n");
        System.exit(1);
    }

    private boolean selectInnerMotion(GraphState graphState, InnerMotion innerMotion) {
        double motionRate = innerMotion.getStatistics().getRate();
        return graphState.getStatistics().getRate() < motionRate || motionRate == 1.0;
    }

    private boolean selectOutterMotion(GraphState graphState, OutterMotion outterMotion) {
        double motionRate = outterMotion.getStatistics().getRate();
        return graphState.getStatistics().getRate() < motionRate || motionRate == 1.0;
    }

    private void motionWithTrees() {
        Heap<GraphState> graphStateHeap = this.buildHeap(descGraphStateRateComparator);
        GraphState graphState = graphStateHeap.pop();
        while (graphState != null) {
            InnerMotion innerMotion = graphState.getInnerMotion();
            OutterMotion outterMotion = this.getOutterMotionWithTrees(graphState);
            Tree<TreeState> tree = this.trees.get(graphState.getTreeKey());

            if (innerMotion != null && outterMotion != null)
                if (innerMotion.getStatistics().getRate() >= outterMotion.getStatistics().getRate())
                    outterMotion = null;
                else
                    innerMotion = null;

            if (innerMotion != null && this.selectInnerMotion(graphState, innerMotion)) {
                Point[] points = this.preparePointsToSend(graphState, innerMotion);
                this.setGraphResponse(points);
                this.fillGraphState(graphState, treeService.getTreeWithSetBoard(tree, graphState.getBoard()));
                return;
            } else if (outterMotion != null && this.selectOutterMotion(graphState, outterMotion)) {
                Point[] points = this.preparePointsToSend(graphState, outterMotion);
                this.setGraphResponse(points);
                this.fillGraphState(graphState, treeService.getTreeWithSetBoard(tree, graphState.getBoard()));
                return;
            }
            graphState = graphStateHeap.pop();
        }

        this.motionWithoutTrees();
    }

    private void fillGraphState(GraphState graphState, Tree<TreeState> tree) {
        graphState.setTree(tree);
        TreeState treeState = tree.getRoot().getData();
        graphState.setInnerMotion(treeState.getInnerMotion());
        graphState.setOutterMotions(treeState.getOutterMotions());
        graphState.setStatistics(treeState.getStatistics());
    }

    // {6;0},{6;1},{3;3},{3;4},{0;0},{0;1},{1;1},{1;2}
    private void fillGraph() {
        for (GraphState graphState : graph.getVertices().values()) {
            String treeKey = graphState.getTreeKey();
            Tree<TreeState> tree = this.treeService.getTreeWithSetBoard(trees.get(treeKey), graphState.getBoard());
            this.fillGraphState(graphState, tree);
        }
    }

    public void game() {
//        this.buildGraph();

//        new Thread() {
//            public void run() {
//                buildTrees();
//            }
//        }.start();

//        while (true) {
//            this.setGraphRequest();
//            graphService.showGraph(this.graph, boardsInRow);
//            this.motionWithoutTrees();
//            graphService.showGraph(this.graph, boardsInRow);
//
//        }
        this.buildGraph();
        this.buildTrees();
        this.fillGraph();
        while (true) {
            this.motionWithTrees();
            graphService.showGraph(this.graph, boardsInRow);
//            this.setGraphRequest();
//            graphService.showGraph(this.graph, boardsInRow);
        }
    }

    private void buildTrees() {
        Set<String> boardSizes = this.getBoardSizes(this.matchSize);
        Map<String, Tree<TreeState>> trees = new HashMap<>();

        boardSizes.forEach(s -> {
            trees.put(s, TreeBuilder.build(Integer.valueOf(s.substring(0, 1)), Integer.valueOf(s.substring(2))));
        });

        this.trees = trees;

//        ExecutorService executorService = Executors.newFixedThreadPool(boardSizes.size());
//        List<Callable<Void>> tasks = new ArrayList<>();
//        boardSizes.forEach(s -> {
//            tasks.add(() -> {
//                Tree<TreeState> tree = TreeBuilder.build(Integer.valueOf(s.substring(0, 1)), Integer.valueOf(s.substring(2)));
//                trees.put(s, tree);
//                return null;
//            });
//        });
//
//
//        try {
//            List<Future<Void>> futures = executorService.invokeAll(tasks);
//            for (Future<Void> future : futures) {
//                try {
//                    future.get();
//
//                } catch (InterruptedException ie) {
//                    Thread.currentThread().interrupt(); // ignore/reset
//                }
//            }
//        } catch (Exception err) {
//            err.printStackTrace();
//        }
//
//        this.trees = trees;
    }


}