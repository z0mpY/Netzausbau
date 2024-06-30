import java.util.*;

class Graph {
    private final int[][] adjacencyMatrix;
    private final int vertices;
    final static int INF = -99999;

    public Graph(int vertices) {
        this.vertices = vertices;
        adjacencyMatrix = new int[vertices][vertices];

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
        adjacencyMatrix[destination][source] = weight;
    }

    public void findOptimalPathFromMultipleSources(List<Integer> startNodes) {
        int[][] distanceMatrix = new int[vertices][vertices];
        int[][] stepsMatrix = new int[vertices][vertices];
        String[][] pathMatrix = new String[vertices][vertices];
        int[][] averageMatrix = new int[vertices][vertices];

        for (int i = 0; i < vertices; i++) {
            boolean[] visited = new boolean[vertices];
            dfs(i, i, visited, 0, 0, distanceMatrix, stepsMatrix, pathMatrix, String.valueOf(i));
        }

        // Calculate average distance per step
        for (int i = 0; i < vertices; i++) {
            for (int j = 0; j < vertices; j++) {
                if (stepsMatrix[i][j] != 0) {
                    averageMatrix[i][j] = distanceMatrix[i][j] / stepsMatrix[i][j];
                }
            }
        }

        printMatrix(adjacencyMatrix, "Adjacency Matrix");
        printMatrix(distanceMatrix, "Distance Matrix");
        printMatrix(stepsMatrix, "Steps Matrix");
        printMatrix(averageMatrix, "Average Matrix");
        printPathMatrix(pathMatrix, "Path Matrix");

        Set<Integer> visitedNodes = new HashSet<>(startNodes);
        List<Integer> currentNodes = new ArrayList<>(startNodes);
        List<String> completePath = new ArrayList<>();

        while (true) {
            int maxAverage = Integer.MIN_VALUE;
            String maxAveragePath = null;
            int nextNode = -1;

            for (int currentNode : currentNodes) {
                for (int i = 0; i < vertices; i++) {
                    if (!visitedNodes.contains(i) && averageMatrix[currentNode][i] > maxAverage) {
                        maxAverage = averageMatrix[currentNode][i];
                        maxAveragePath = pathMatrix[currentNode][i];
                        nextNode = i;
                    }
                }
            }

            if (nextNode == -1) break;

            visitedNodes.add(nextNode);
            currentNodes.add(nextNode);

            if (maxAveragePath != null) {
                String[] nodes = maxAveragePath.split(" -> ");
                for (String node : nodes) {
                    if (!completePath.contains(node)) {
                        completePath.add(node);
                    }
                }
            }
        }

        System.out.println("Complete path: " + String.join(" -> ", completePath));
    }

    private void dfs(int start, int current, boolean[] visited, int currentDistance, int currentSteps, int[][] distanceMatrix, int[][] stepsMatrix, String[][] pathMatrix, String path) {
        visited[current] = true;
        for (int i = 0; i < vertices; i++) {
            if (adjacencyMatrix[current][i] != INF && !visited[i]) {
                int newDistance = currentDistance + adjacencyMatrix[current][i];
                int newSteps = currentSteps + 1;
                String newPath = path + " -> " + i;

                if (newDistance > distanceMatrix[start][i]) {
                    distanceMatrix[start][i] = newDistance;
                    stepsMatrix[start][i] = newSteps;
                    pathMatrix[start][i] = newPath;
                }

                dfs(start, i, visited, newDistance, newSteps, distanceMatrix, stepsMatrix, pathMatrix, newPath);
            }
        }
        visited[current] = false;
    }

    // Print matrix with a given name
    void printMatrix(int matrix[][], String matrixName) {
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

    // Print path matrix
    void printPathMatrix(String matrix[][], String matrixName) {
        System.out.println(matrixName + ":");

        // Print column headers
        System.out.print("      ");
        for (int i = 0; i < vertices; i++) {
            System.out.printf("%24d", i);
        }
        System.out.println();

        for (int i = 0; i < vertices; ++i) {
            // Print row header
            System.out.printf("%d ", i);

            for (int j = 0; j < vertices; ++j) {
                if (matrix[i][j] == null)
                    System.out.printf("%27s", "None");
                else
                    System.out.printf("%27s", matrix[i][j]);
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

        g.addEdge(0, 1, 149);
        g.addEdge(0, 2, 146);
        g.addEdge(0, 3, 145);
        g.addEdge(0, 4, 144);
        g.addEdge(0, 18, 143);
        g.addEdge(0, 17, 142);
        g.addEdge(0, 16, 141);
        g.addEdge(0, 15, 140);
        g.addEdge(0, 14, 139);
        g.addEdge(0, 11, 138);
        g.addEdge(1, 10, 137);
        g.addEdge(1, 2, 136);
        g.addEdge(2, 3, 135);
        g.addEdge(4, 5, 134);
        g.addEdge(4, 19, 133);
        g.addEdge(5, 6, 132);
        g.addEdge(5, 7, 131);
        g.addEdge(6, 7, 130);
        g.addEdge(7, 8, 129);
        g.addEdge(7, 9, 128);
        g.addEdge(7, 11, 127);
        g.addEdge(10, 11, 126);
        g.addEdge(11, 12, 125);
        g.addEdge(12, 13, 124);
        g.addEdge(13, 27, 123);
        g.addEdge(14, 15, 122);
        g.addEdge(14, 27, 121);
        g.addEdge(15, 16, 120);
        g.addEdge(18, 19, 119);
        g.addEdge(18, 22, 118);
        g.addEdge(18, 23, 117);
        g.addEdge(19, 20, 116);
        g.addEdge(20, 21, 115);
        g.addEdge(20, 22, 114);
        g.addEdge(22, 26, 113);
        g.addEdge(22, 25, 112);
        g.addEdge(23, 24, 111);
        g.addEdge(27, 29, 110);
        g.addEdge(27, 28, 109);
        g.addEdge(27, 32, 108);
        g.addEdge(28, 30, 107);
        g.addEdge(28, 31, 106);
        g.addEdge(31, 32, 105);
        g.addEdge(32, 33, 104);
        g.addEdge(32, 34, 103);
        g.addEdge(34, 35, 102);
        g.addEdge(35, 36, 101);

        g.printReachable();

        List<Integer> startNodes = Arrays.asList(0, 1, 2);
        g.findOptimalPathFromMultipleSources(startNodes);
    }
}
