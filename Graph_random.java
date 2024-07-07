import java.util.*;

class Graph {
    private final int[][] adjacencyMatrix;
    private final int vertices;
    final static int INF = -99999;
    private final Random random;

    public Graph(int vertices) {
        this.vertices = vertices;
        adjacencyMatrix = new int[vertices][vertices];
        random = new Random();

        // Initialize adjacencyMatrix with -INF except the diagonal
        for (int i = 0; i < vertices; i++) {
            for (int j = 0; j < vertices; j++) {
                if (i == j) {
                    adjacencyMatrix[i][j] = 0;
                } else {
                    adjacencyMatrix[i][j] = INF;
                }
            }
        }
    }

    public void addEdge(int source, int destination, int weight) {
        adjacencyMatrix[source][destination] = weight;
    }

    public List<Integer> findOptimalPathFromMultipleSources(List<Integer> startNodes) {
        List<Integer> stillToBuild = new ArrayList<>();
        List<Integer> alreadyBuiltNodes = new ArrayList<>();
        List<Integer> buildOrder = new ArrayList<>();
        List<Integer> reachableNodes = new ArrayList<>(startNodes);

        // Initialize stillToBuild with all nodes
        for (int i = 0; i < vertices; i++) {
            stillToBuild.add(i);
        }

        while (!stillToBuild.isEmpty()) {
            // Select a random node from reachableNodes
            int randomIndex = random.nextInt(reachableNodes.size());
            int nodeToBuild = reachableNodes.get(randomIndex);

            // Add the selected node to buildOrder and alreadyBuiltNodes
            buildOrder.add(nodeToBuild);
            alreadyBuiltNodes.add(nodeToBuild);
            stillToBuild.remove((Integer) nodeToBuild);
            reachableNodes.remove((Integer) nodeToBuild);

            // Update reachableNodes: nodes reachable from the current node plus still-to-build start nodes
            List<Integer> newReachableNodes = getReachableNodes(nodeToBuild, alreadyBuiltNodes);
            for (int node : newReachableNodes) {
                if (!reachableNodes.contains(node) && !alreadyBuiltNodes.contains(node)) {
                    reachableNodes.add(node);
                }
            }
            // Add start nodes that are still to be built
            for (int startNode : startNodes) {
                if (stillToBuild.contains(startNode) && !reachableNodes.contains(startNode)) {
                    reachableNodes.add(startNode);
                }
            }
        }

        return buildOrder;
    }

    private List<Integer> getReachableNodes(int node, List<Integer> alreadyBuiltNodes) {
        List<Integer> reachableNodes = new ArrayList<>();

        for (int i = 0; i < vertices; i++) {
            if (adjacencyMatrix[node][i] != INF && adjacencyMatrix[node][i] != 0 && !alreadyBuiltNodes.contains(i)) {
                reachableNodes.add(i);
            }
        }

        return reachableNodes;
    }

    public double calculateTotalWeight(List<Integer> buildOrder) {
        double totalWeight = 0.0;
        for (int i = 1; i < buildOrder.size(); i++) {
            int node = buildOrder.get(i);
            int weight = 0;
            for (int j = 0; j < vertices; j++) {
                if (adjacencyMatrix[j][node] != INF && adjacencyMatrix[j][node] != 0) {
                    weight = adjacencyMatrix[j][node];
                    break;
                }
            }
            totalWeight += weight / Math.pow(1 + 0.00, i);
        }
        return totalWeight;
    }

    // Print matrix with a given name
    void printMatrix(int[][] matrix, String matrixName) {
        System.out.println(matrixName + ":");

        // Print column headers
        System.out.print("      ");
        for (int i = 0; i < vertices; i++) {
            System.out.printf("%6d", i);
        }
        System.out.println();

        for (int i = 0; i < vertices; ++i) {
            // Print row header
            System.out.printf("%6d ", i);

            for (int j = 0; j < vertices; ++j) {
                if (matrix[i][j] == INF)
                    System.out.print(" INF ");
                else
                    System.out.printf("%6d", matrix[i][j]);
            }
            System.out.println();
        }
    }

    void printReachable() {
        // Print reachable points from each vertex with weights
        for (int i = 0; i < vertices; i++) {
            String msg = "";
            for (int j = 0; j < vertices; j++) {
                if (adjacencyMatrix[i][j] != 0 && adjacencyMatrix[i][j] != INF) {
                    if (!msg.isEmpty()) {
                        msg += ", ";
                    }
                    msg += j + " (weight: " + adjacencyMatrix[i][j] + ")";
                }
            }
            System.out.println(i + "--> [" + msg + "]");
        }
    }

    public static void main(String[] args) {
        Graph g = new Graph(37);

        g.addEdge(0, 1, 200);
        g.addEdge(0, 2, 300);
        g.addEdge(0, 3, 500);
        g.addEdge(0, 4, 700);
        g.addEdge(0, 18, 1400);
        g.addEdge(0, 17, 1500);
        g.addEdge(0, 16, 1900);
        g.addEdge(0, 15, 2000);
        g.addEdge(0, 14, 2100);
        g.addEdge(1, 0, 100);
        g.addEdge(2, 0, 100);
        g.addEdge(3, 0, 100);
        g.addEdge(4, 0, 100);
        g.addEdge(18, 0, 100);
        g.addEdge(17, 0, 100);
        g.addEdge(16, 0, 100);
        g.addEdge(15, 0, 100);
        g.addEdge(14, 0, 100);
        g.addEdge(2, 3, 500);
        g.addEdge(3, 2, 300);
        g.addEdge(1, 2, 300);
        g.addEdge(2, 1, 200);
        g.addEdge(1, 10, 2600);
        g.addEdge(10, 1, 200);
        g.addEdge(10, 6, 2500);
        g.addEdge(6, 10, 2600);
        g.addEdge(10, 11, 2700);
        g.addEdge(11, 10, 2600);
        g.addEdge(11, 12, 2800);
        g.addEdge(12, 11, 2700);
        g.addEdge(12, 13, 2900);
        g.addEdge(13, 12, 2800);
        g.addEdge(6, 7, 2300);
        g.addEdge(7, 6, 2500);
        g.addEdge(7, 8, 2200);
        g.addEdge(8, 7, 2300);
        g.addEdge(7, 9, 2400);
        g.addEdge(9, 7, 2400);
        g.addEdge(6, 5, 600);
        g.addEdge(5, 6, 2500);
        g.addEdge(5, 4, 700);
        g.addEdge(4, 5, 600);
        g.addEdge(4, 19, 1200);
        g.addEdge(19, 4, 700);
        g.addEdge(19, 18, 1400);
        g.addEdge(18, 19, 1200);
        g.addEdge(18, 23, 1600);
        g.addEdge(23, 18, 1400);
        g.addEdge(23, 24, 1700);
        g.addEdge(24, 23, 1600);
        g.addEdge(19, 20, 1100);
        g.addEdge(20, 29, 1200);
        g.addEdge(20, 21, 800);
        g.addEdge(21, 20, 1100);
        g.addEdge(20, 22, 1300);
        g.addEdge(22, 20, 1100);
        g.addEdge(22, 18, 1400);
        g.addEdge(18, 22, 1300);
        g.addEdge(22, 26, 900);
        g.addEdge(22, 25, 1000);
        g.addEdge(26, 22, 1300);
        g.addEdge(25, 22, 1300);
        g.addEdge(14, 27, 3000);
        g.addEdge(27, 14, 2100);
        g.addEdge(27, 29, 1800);
        g.addEdge(29, 25, 3000);
        g.addEdge(13, 17, 3000);
        g.addEdge(27, 13, 2900);
        g.addEdge(27, 28, 3100);
        g.addEdge(28, 27, 3000);
        g.addEdge(28, 30, 3200);
        g.addEdge(30, 28, 3100);
        g.addEdge(28, 31, 3300);
        g.addEdge(31, 28, 3100);
        g.addEdge(27, 32, 3400);
        g.addEdge(32, 27, 3000);
        g.addEdge(31, 32, 3400);
        g.addEdge(32, 31, 3300);
        g.addEdge(32, 33, 3500);
        g.addEdge(33, 32, 3400);
        g.addEdge(32, 34, 3600);
        g.addEdge(34, 32, 3400);
        g.addEdge(34, 35, 3700);
        g.addEdge(35, 34, 3600);
        g.addEdge(35, 36, 400);
        g.addEdge(36, 35, 3700);

        // Print the adjacency matrix before generating build orders
        g.printMatrix(g.adjacencyMatrix, "Adjacency Matrix");
        g.printReachable();

        List<Integer> startNodes = Arrays.asList(0, 17, 2, 16, 14, 18, 27, 23, 4, 22, 19, 32, 28, 25, 20, 21, 5, 34, 30, 6);
        List<List<Integer>> allBuildOrders = new ArrayList<>();

        for (int i = 0; i < 1000; i++) {
            List<Integer> buildOrder = g.findOptimalPathFromMultipleSources(startNodes);
            allBuildOrders.add(buildOrder);
        }

        System.out.println("All Build Orders:");
        for (int i = 0; i < allBuildOrders.size(); i++) {
            List<Integer> buildOrder = allBuildOrders.get(i);
            double totalWeight = g.calculateTotalWeight(buildOrder);
            System.out.println((i + 1) + ": " + buildOrder + " (Total Weight: " + totalWeight + ")");
        }
    }
}
