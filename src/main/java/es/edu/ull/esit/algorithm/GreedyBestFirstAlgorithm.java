package es.edu.ull.esit.algorithm;

import es.edu.ull.esit.Node;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Greedy Best-First Search pathfinding algorithm.
 * Prioritizes nodes that appear to be closer to the goal based on heuristic.
 * May not find the optimal path but is fast.
 */
public class GreedyBestFirstAlgorithm extends AbstractSearchAlgorithm {

    @Override
    public void search(Node start, Node end, int graphWidth, int graphHeight, int searchTime) {
        List<Node> openList = new ArrayList<>();
        Node[][] prev = new Node[graphWidth][graphHeight];
        openList.add(start);

        while (!openList.isEmpty()) {
            Node curNode = getLeastHeuristic(openList, end, start);
            openList.remove(curNode);

            if (curNode.isEnd()) {
                curNode.setColor(Color.MAGENTA);
                shortpath(prev, end, searchTime);
                return;
            }

            curNode.setColor(Color.ORANGE);
            try {
                Thread.sleep(searchTime);
            } catch (Exception e) {
                e.printStackTrace();
            }
            curNode.setColor(Color.BLUE);

            for (Node adjacent : curNode.getNeighbours()) {
                if (!adjacent.isSearched() && !openList.contains(adjacent)) {
                    prev[adjacent.getX()][adjacent.getY()] = curNode;
                    openList.add(adjacent);
                }
            }
        }
    }
}
