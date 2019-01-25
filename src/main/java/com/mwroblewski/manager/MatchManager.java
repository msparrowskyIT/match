package com.mwroblewski.manager;

import com.mwroblewski.aisd.graph.Graph;
import com.mwroblewski.aisd.heap.Heap;
import com.mwroblewski.aisd.tree.Tree;
import com.mwroblewski.builder.GraphBuilder;
import com.mwroblewski.builder.TreeBuilder;
import com.mwroblewski.common.AdjacencyType;
import com.mwroblewski.model.*;
import com.mwroblewski.service.*;

import java.util.*;

public class MatchManager {

    private MotionService motionService = new MotionService();
    private BoardService boardService = new BoardService();
    private ParseService parseService = new ParseService();
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
    private boolean rateStrategy = false;
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

    private void buildTrees() {
        Set<String> boardSizes = this.getBoardSizes(this.matchSize);
        Map<String, Tree<TreeState>> trees = new HashMap<>();

        boardSizes.forEach(s -> {
            trees.put(s, TreeBuilder.build(Integer.valueOf(s.substring(0, 1)), Integer.valueOf(s.substring(2))));
        });

        this.trees = trees;
    }

    private void sendPoints(Point[] points) {
        System.out.print(parseService.parsePoints(points));
    }

    private void setGraph(Point[] points) {
        for (Point point : points) {
            graphService.set(graph, point, boardsInRow);
        }

        if (this.rateStrategy)
            this.fillGraph();
    }

    private void setGraphWithRequest() {
        Point[] points = parseService.parsePoints();
        this.setGraph(points);

        System.out.print("ok");
    }

    private void setGraphWithResponse(Point[] points) {
        this.sendPoints(points);
        this.setGraph(points);
    }

    private void setAndFillGraphWithResponse(GraphState state, Tree<TreeState> tree,  InnerMotion motion) {
        Point[] points = this.preparePointsToSend(state, motion);
        this.setGraphWithResponse(points);
        this.fillGraphState(state, treeService.getTreeWithSetBoard(tree, state.getBoard()));
    }

    private void setAndFillGraphWithResponse(GraphState state, Tree<TreeState> tree,  OutterMotion motion) {
        Point[] points = this.preparePointsToSend(state, motion);
        this.setGraphWithResponse(points);
        this.fillGraphState(state, treeService.getTreeWithSetBoard(tree, state.getBoard()));
    }

    private Heap<GraphState> buildHeap(Comparator<GraphState> comparator) {
        Collection<GraphState> values = this.graph.getVertices().values();
        GraphState[] graphStates = values.toArray(new GraphState[values.size()]);

        return new Heap<>(graphStates, comparator, GraphState.class);
    }

    private InnerMotion getInnerMotionWithoutTrees(GraphState graphState) {
        Set<InnerMotion> motions = this.boardService.getAllInnerMotions(graphState.getBoard());
        if (motions.size() > 0)
            return motions.iterator().next();
        else
            return null;
    }

    private OutterMotion getOutterMotionWithoutTrees(GraphState graphState) {
        Set<OutterMotion> motions = this.boardService.getAllOutterMotions(graphState.getBoard());

        for (AdjacencyType adjType : AdjacencyType.values()) {
            List<OutterMotion> filterMotions = motionService.filterOutterMotions(motions, adjType);
            if (filterMotions.size() != 0) {
                GraphState adjGraphState = graphService.getState(this.graph, graphState.getGraphIndex(), adjType);
                // no adjBoard in adjacency matrix
                if (adjGraphState == null)
                    continue;

                Optional<OutterMotion> optMotion = filterMotions
                        .stream()
                        .filter(m -> {
                            Point adjPoint = boardService.getAdjacencyPoint(adjGraphState.getBoard(), m);
                            return adjGraphState.getBoard().isFree(adjPoint);
                        })
                        .findFirst();

                if (optMotion.isPresent())
                    return optMotion.get();
            }
        }

        return null;
    }

    private OutterMotion getOutterMotionWithTrees(GraphState graphState) {
        List<OutterMotion> motions = graphState.getOutterMotions();

        for (AdjacencyType adjType : AdjacencyType.values()) {
            List<OutterMotion> filterMotions = this.motionService.filterOutterMotions(motions, adjType);

            if (filterMotions.size() != 0) {
                GraphState adjGraphState = graphService.getState(this.graph, graphState.getGraphIndex(), adjType);
                // no adjBoard in adjacency matrix
                if (adjGraphState == null)
                    continue;

                Tree<TreeState> adjTree = this.trees.get(adjGraphState.getTreeKey());
                Optional<OutterMotion> optMotion = filterMotions
                        .stream()
                        .filter(m -> {
                            Point adjPoint = boardService.getAdjacencyPoint(adjGraphState.getBoard(), m);
                            // adjPoint is set in adjBoard
                            if (adjGraphState.getBoard().isSet(adjPoint))
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

                if (optMotion.isPresent())
                    return optMotion.get();
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
                this.setGraphWithResponse(points);
                return;
            } else {
                OutterMotion outterMotion = this.getOutterMotionWithoutTrees(graphState);
                if (outterMotion != null) {
                    Point[] points = this.preparePointsToSend(graphState, outterMotion);
                    this.setGraphWithResponse(points);
                    return;
                }
            }
            graphState = graphStateHeap.pop();
        }

        System.out.print("Losses :(\n");
        System.exit(1);
    }

    private boolean selectInnerMotion(GraphState graphState, InnerMotion motion) {
        double rate = motion.getStatistics().getRate();
        return graphState.getStatistics().getRate() < rate || rate == 1.0;
    }

    private boolean selectOutterMotion(GraphState graphState, OutterMotion motion) {
        double rate = motion.getStatistics().getRate();
        return graphState.getStatistics().getRate() < rate || rate == 1.0;
    }

    private void fillGraphState(GraphState graphState, Tree<TreeState> tree) {
        graphState.setTree(tree);
        TreeState treeState = tree.getRoot().getData();
        graphState.setInnerMotion(treeState.getInnerMotion());
        graphState.setOutterMotions(treeState.getOutterMotions());
        graphState.setStatistics(treeState.getStatistics());
    }

    private void fillGraph() {
        for (GraphState graphState : graph.getVertices().values()) {
            String treeKey = graphState.getTreeKey();
            Tree<TreeState> tree = this.treeService.getTreeWithSetBoard(trees.get(treeKey), graphState.getBoard());
            this.fillGraphState(graphState, tree);
        }
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
                this.setAndFillGraphWithResponse(graphState, tree, innerMotion);
                return;
            } else if (outterMotion != null && this.selectOutterMotion(graphState, outterMotion)) {
                this.setAndFillGraphWithResponse(graphState, tree, outterMotion);
                return;
            }
            graphState = graphStateHeap.pop();
        }

        this.motionWithoutTrees();
    }

    private void motion(){
        while (block) {}
        block = true;
        if(!rateStrategy)
            this.motionWithoutTrees();
        else
            this.motionWithTrees();
        block = false;
    }

    public void game() {
        this.buildGraph();

        new Thread() {
            public void run() {
                buildTrees();

                while (block) {}

                block = true;
                fillGraph();

                rateStrategy = true;
                block = false;
            }
        }.start();

//        Point[] points = this.parseService.parsePoints();
//        if(points != null)
//            this.setGraph(points);
//
//        while (true) {
//            this.motion();
//            this.setGraphWithRequest();
//        }

        while (true) {
            this.motion();
            graphService.showGraph(this.graph, boardsInRow);
//            this.setGraphWithRequest();
//            graphService.showGraph(this.graph, boardsInRow);
        }
    }

}