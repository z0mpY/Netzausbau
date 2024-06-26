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

    public void findLongestPaths() {
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

        printMatrix(adjacencyMatrix,"Adjacency Matrix");
        printMatrix(distanceMatrix, "Distance Matrix");
        printMatrix(stepsMatrix, "Steps Matrix");
        printMatrix(averageMatrix, "Average Matrix");
        printPathMatrix(pathMatrix, "Path Matrix");
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
        Graph g = new Graph(5);

        g.addEdge(0, 1, 550);
        g.addEdge(0, 2, 100);
        g.addEdge(0, 3, 500);
        g.addEdge(2, 4, 1000);
        g.addEdge(1, 3, 800);
        g.addEdge(1, 2, 100);

        g.printReachable();
        g.findLongestPaths();
    }
}
