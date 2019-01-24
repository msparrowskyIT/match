package com.mwroblewski.builder;

import com.mwroblewski.model.*;
import com.mwroblewski.service.BoardService;
import com.mwroblewski.service.TreeService;
import com.mwroblewski.aisd.tree.Node;
import com.mwroblewski.aisd.tree.Tree;

import java.util.*;

public class TreeBuilder {

    private static BoardService boardService = new BoardService();
    private static TreeService treeService = new TreeService();

    private static Set<Board> getSinglePointBoards(int width, int height) {
        Set<Board> onePointBoards = new HashSet<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Board board = new Board(new int[height][width]);
                if (board.set(new Point(x, y)))
                    onePointBoards.add(board);
            }
        }

        return onePointBoards;
    }

    private static Map<Integer, Set<Board>> getAllBoards(int width, int height) {
        Map<Integer, Set<Board>> allBoards = new HashMap<>();

        Board emptyBoard = new Board(new int[height][width]);
        allBoards.put(0, new HashSet(Arrays.asList(emptyBoard)));

        Set<Board> singlePointBoards = getSinglePointBoards(width, height);
        allBoards.put(1, singlePointBoards);

        for (int i = 2; i <= width * height; i++) {
            Set<Board> multiPointsBoards = new HashSet<>();

            allBoards.get(i - 1).forEach(prevLevelBoard -> {
                singlePointBoards.forEach(singlePointBoard -> {
                    Board multiPointsBoard = boardService.merge(prevLevelBoard, singlePointBoard);
                    if (multiPointsBoard != null)
                        multiPointsBoards.add(multiPointsBoard);
                });
            });
            allBoards.put(i, multiPointsBoards);
        }

        return allBoards;
    }

    private static void setChildren(Node<TreeState> parentNode, Map<Integer, Set<Board>> allBoards, int level) {
        Set<Board> childrenBoards = allBoards.get(level);

        if (childrenBoards == null)
            return;

        Board parentBoard = parentNode.getData().getBoard();
        for (Board childBoard : childrenBoards) {
            if (boardService.isParentAndChild(parentBoard, childBoard)) {
                Node<TreeState> childNode = new Node<>(parentNode, new TreeState(childBoard));
                parentNode.addChild(childNode);
                setChildren(childNode, allBoards, level + 1);
            }
        }
    }

    private static Tree<TreeState> init(int width, int height) {
        Map<Integer, Set<Board>> boards = getAllBoards(width, height);

        Board empty = (Board) boards.get(0).iterator().next();
        Node<TreeState> root = new Node<>(new TreeState(empty));
        Tree<TreeState> tree = new Tree<>(root);

        setChildren(root, boards, 1);

        return tree;
    }

    private static List<InnerMotion> sortInnerMotions(Set<InnerMotion> innerMotions) {
        List<InnerMotion> innerMotionsList = new ArrayList<>(innerMotions);
        innerMotionsList.sort((m1, m2) -> {
            double rate1 = m1.getStatistics().getRate();
            double rate2 = m2.getStatistics().getRate();
            return rate1 > rate2 ? -1 : (rate1 < rate2 ? 1 : 0);
        });

        return innerMotionsList;
    }

    private static void setInnerMotions(Tree<TreeState> tree, Set<InnerMotion> innerMotions) {
        TreeState parentState = tree.getRoot().getData();
        innerMotions.forEach(innerMotion -> {
            TreeState childState = treeService.getStateWithSetInnerMotion(tree, innerMotion);
            parentState.getStatistics().addStatistics(childState.getStatistics(), true);
            innerMotion.getStatistics().addStatistics(childState.getStatistics(), true);
        });

        parentState.setInnerMotions(sortInnerMotions(innerMotions));
    }

    private static List<OutterMotion> sortOutterMotions(Set<OutterMotion> outterMotions) {
        List<OutterMotion> outterMotionList = new ArrayList<>(outterMotions);
        outterMotionList.sort((m1, m2) -> {
            double rate1 = m1.getStatistics().getRate();
            double rate2 = m2.getStatistics().getRate();
            return rate1 > rate2 ? -1 : (rate1 < rate2 ? 1 : 0);
        });

        return outterMotionList;

    }

    private static void setOutterMotions(Tree<TreeState> tree, Set<OutterMotion> outterMotions) {
        TreeState parentState = tree.getRoot().getData();
        outterMotions.forEach(outterMotion -> {
            TreeState childState = treeService.getStateWithSetOutterMotion(tree, outterMotion);
            outterMotion.getStatistics().addStatistics(childState.getStatistics(), true);
        });

        parentState.setOutterMotions(sortOutterMotions(outterMotions));
    }

    private static void setStatistic(Node<TreeState> node) {
        TreeState treeState = node.getData();
        if (node.isLeaf()) {
            treeState.setStatistics(new Statistics(0, 1));
            return;
        }

        node.getChildren().forEach(childNode -> {
            setStatistic(childNode);
        });

        Set<InnerMotion> innerMotions = boardService.getAllInnerMotions(treeState.getBoard());
        if (innerMotions.size() == 0)
            treeState.getStatistics().addStatistics(0, 1, false);
        else
            setInnerMotions(new Tree<>(node), innerMotions);

        Set<OutterMotion> outterMotions = boardService.getAllOutterMotions(treeState.getBoard());
        if (!(outterMotions.size() == 0))
            setOutterMotions(new Tree<>(node), outterMotions);
    }

    public static Tree<TreeState> build(int width, int height) {
        Tree<TreeState> tree = init(width, height);
        setStatistic(tree.getRoot());

        return tree;
    }

}
