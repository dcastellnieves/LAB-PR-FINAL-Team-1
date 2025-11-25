package es.edu.ull.esit.algorithm;

import es.edu.ull.esit.Node;
import java.awt.Color;
import java.util.List;

/**
 * Abstract base class for pathfinding algorithms.
 * Provides common helper methods used by various search algorithms.
 */
public abstract class AbstractSearchAlgorithm implements SearchAlgorithm {

    /**
     * Reconstructs and displays the shortest path from start to end.
     * Backtracks from the end node using the previous node array.
     *
     * @param prev       2D array storing the previous node for each position
     * @param end        The target/end node
     * @param searchTime The delay time in milliseconds for visualization
     */
    protected void shortpath(Node[][] prev, Node end, int searchTime) {
        Node pathConstructor = end;
        while (pathConstructor != null) {
            pathConstructor = prev[pathConstructor.getX()][pathConstructor.getY()];

            if (pathConstructor != null) {
                pathConstructor.setColor(Color.ORANGE);
            }
            try {
                Thread.sleep(searchTime);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Selects the node with the lowest heuristic cost from a list.
     * Calculates both the heuristic distance to the end and distance from the start.
     *
     * @param nodes The list of nodes to evaluate
     * @param end   The target/end node
     * @param start The starting node
     * @return The node with the lowest total heuristic cost
     */
    protected Node getLeastHeuristic(List<Node> nodes, Node end, Node start) {
        if (!nodes.isEmpty()) {
            Node leastH = nodes.get(0);
            for (int i = 1; i < nodes.size(); i++) {
                // h-cost: heuristic distance to end
                double h1 = Node.distance(nodes.get(i), end);
                // g-cost: actual distance from start
                double g1 = nodes.get(i).getgCost();

                // h-cost: heuristic distance to end
                double h2 = Node.distance(leastH, end);
                // g-cost: actual distance from start
                double g2 = leastH.getgCost();

                // f = g + h
                if (g1 + h1 < g2 + h2) {
                    leastH = nodes.get(i);
                }
            }
            return leastH;
        }
        return null;
    }
}
