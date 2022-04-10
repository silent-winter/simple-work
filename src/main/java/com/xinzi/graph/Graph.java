package com.xinzi.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: xinzi
 * @Date: 2022/01/24/20:35
 */
public class Graph {

    private final boolean[][] adjMat;

    private final boolean[] visited;

    // In the adjacency matrix representation of a Graph,
    // if adjMat[i][j] == true, then you can move from
    // node i to node j.
    // i.e. j is a neighbour of i.

    // The constructor builds the adjacency matrix,
    // with initially all values false.
    public Graph(int size) {
        adjMat = new boolean[size][size];
        for (int i = 0; i < adjMat.length; i++) {
            for (int j = 0; j < adjMat.length; j++) {
                adjMat[i][j] = i == j;
            }
        }
        visited = new boolean[size];
        resetVisited();
    }

    // Add a transition to the adjacency matrix,
    // i.e. a neighbour relation between two nodes.
    public void add(int from, int to) {
        adjMat[from][to] = true;
    }


    // Carry out uninformed search of the Graph,
    // from the start node to goal node.
    public boolean search(int start, int goal, boolean dp) {
        // The frontier is an ArrayList of Paths.
        List<Path> frontier = new ArrayList<>();

        // Initially the frontier contains just the Path
        // containing only the start node.
        Path firstPath = new Path(start);
        frontier.add(firstPath);

        // Search until the goal is found,
        // or the frontier is empty.
        while (!frontier.isEmpty()) {
            // *** TO-DO ***
            // CARRY OUT DEPTH-FIRST OR BREADTH-FIRST SEARCH

            // bfs->每次取队首节点，dfs->每次取栈顶节点
            // dp=true：dfs，dp=false：bfs
            int index = dp ? frontier.size() - 1 : 0;
            Path currPath = frontier.get(index);
            frontier.remove(index);
            int lastNode = currPath.getLastNode();
            if (visited[lastNode]) {
                // 已经访问过的节点，直接跳过
                continue;
            }
            System.out.println("Inspect node: " + lastNode);
            visited[lastNode] = true;
            if (lastNode == goal) {
                // 找到目标节点
                System.out.println("Found goal node!");
                System.out.println(currPath);
                resetVisited();
                return true;
            }
            for (int j = 0; j < adjMat.length; j++) {
                if (adjMat[lastNode][j] && lastNode != j) {
                    // 找到一个连通节点，加入队列
                    frontier.add(new Path(currPath, j));
                }
            }
        }
        resetVisited();
        return false;
    }


    private void resetVisited() {
        Arrays.fill(visited, false);
    }


    public static void main(String[] args) {
        // Create a Graph containing 7 nodes
        Graph g = new Graph(7);

        // Add edges to the Graph
        g.add(0, 1);
        g.add(0, 2);
        g.add(1, 5);
        g.add(1, 6);
        g.add(2, 3);
        g.add(3, 4);

        // select a search type
        boolean depthFirst = false;

        // start searching
        boolean canFind = g.search(0, 4, depthFirst);
        System.out.println("==========================");
        g.search(0, 4, true);
    }

}
