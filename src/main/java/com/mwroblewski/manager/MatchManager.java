package com.mwroblewski.manager;

import java.util.*;

public class MatchManager {

    private Set<String> getBoardSizes(int matchSize){
        Set<String> boardSizes = new HashSet<>();
        boardSizes.add("3x3");

        int r = matchSize%3;
        if(r != 0){
            boardSizes.add("3x"+r);
            boardSizes.add(r+"x3");
            boardSizes.add(r+"x"+r);
        }

        return boardSizes;
    }

//    private GraphService graphService = new GraphService();
//    private BoardService boardService = new BoardService();
//
//    private Graph<Integer, TreeState, AdjacencyType> graph;
//    private Map<String, Tree<TreeState>> trees;
//    private int n;
//
//    private String parseMotion(InnerMotion motion) {
//        StringBuilder sb = new StringBuilder();
//        Point[] points = motion.getPoints();
//        for(int i = 0; i < points.length; i++) {
//            if (i != 0)
//                sb.append(",");
//
//            sb.append("{" + points[i].getX() + ";" + points[i].getY() + "}");
//        }
//
//        return sb.toString();
//    }
//
//    private List<TreeState> sortStates(){
//        List<TreeState> treeStates = new ArrayList<>(this.graph.getVertices().values());
//        treeStates.sort((state1, state2) -> {
//            int size1 = state1.getBoard().getWidth() * state1.getBoard().getHeight();
//            int size2 = state2.getBoard().getWidth() * state2.getBoard().getHeight();
//
//            return size1 > size2 ? 1 : (size1 < size2 ? -1 : 0);
//        });
//
//        return treeStates;
//    }
//
//    private InnerMotion getFirstInnerMotion(                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               )
//
//    private String innerMotion() {
//        if (trees == null) {
//            List<TreeState> treeStates = this.sortStates();
//
//
//
//
//        }
//
//        return " ";
//    }
//
//
//    private Point[] parsePoints(String str) {
//        String[] splitStr = str.split(",");
//        Point[] points = new Point[splitStr.length];
//
//        for (int i = 0; i < splitStr.length; i++) {
//            String pointStr = splitStr[i];
//            int graphKey = pointStr.indexOf(";");
//            int x = Integer.valueOf(splitStr[i].substring(1, graphKey));
//            int y = Integer.valueOf(splitStr[i].substring(graphKey + 1, pointStr.length() - 1));
//
//            points[i] = new Point(x, y);
//        }
//        return points;
//    }
//
//    private void buildGraph() {
//        Scanner scanner = new Scanner(System.in);
//
//        this.n = Integer.valueOf(scanner.next());
//        this.graph = GraphBuilder.build(this.n);
//
//        System.out.print("ok");
//    }
//
//    private void buildTrees() {
//        Set<String> boardSizes = graphService.getSizes(this.n);
//        Map<String, Tree<TreeState>> trees = new HashMap<>();
//
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
//    }
//
//    private void coverGraph() throws IncorrectPointException {
//        Scanner scanner = new Scanner(System.in);
//
//        Point[] points = this.parsePoints(scanner.next());
//        this.graphService.coverPoints(this.graph, this.n, points);
//
//        System.out.print("ok");
//    }
//
//    public void game() throws IncorrectPointException {
//        this.buildGraph();
////        this.coverGraph();
//        this.innerMotion();
//
////        new Thread() {
////            public void run() {
////                buildTrees();
////            }
////        }.start();
//
//
//    }
//
//
//}
//
//
//    private InnerMotion getHorizontalInnerMotion(List<TreeState> treeStates){
////        Optional<InnerMotion> optInnerMotion = treeStates
////                .stream()
////                .map(state -> boardService.getHorizontalMotions(state.getBoard()))
////                .filter(motions -> motions.size() != 0)
////                .map(motions -> motions.iterator().next())
////                .findFirst();
////
////        if (optInnerMotion.isPresent())
////            return optInnerMotion.get();
////        else
////            return null;
////    }
////
////    private InnerMotion getVerticalInnerMotion(List<TreeState> treeStates){
////        Optional<InnerMotion> optInnerMotion = treeStates
////                .stream()
////                .map(state -> boardService.getVerticalMotions(state.getBoard()))
////                .filter(motions -> motions.size() != 0)
////                .map(motions -> motions.iterator().next())
////                .findFirst();
////
////        if (optInnerMotion.isPresent())
////            return optInnerMotion.get();
////        else
////            return null;
//    }
}