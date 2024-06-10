class Graph {
    private final int[][] adjacencyMatrix;
    private final int vertices;
    final static int INF = 9999;

    public Graph(int vertices) {
        this.vertices = vertices;
        adjacencyMatrix = new int[vertices][vertices];

        // Initialize adjacencyMatrix with INF except the diagonal
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

    void floydWarshall() {
        int matrix[][] = new int[vertices][vertices];
        int steps[][] = new int[vertices][vertices];
        int i, j, k;

        for (i = 0; i < vertices; i++) {
            for (j = 0; j < vertices; j++) {
                matrix[i][j] = adjacencyMatrix[i][j];
                if (i != j && matrix[i][j] != INF) {
                    steps[i][j] = 1;
                } else {
                    steps[i][j] = 0;
                }
            }
        }

        // Adding vertices individually
        for (k = 0; k < vertices; k++) {
            for (i = 0; i < vertices; i++) {
                for (j = 0; j < vertices; j++) {
                    if (matrix[i][k] + matrix[k][j] < matrix[i][j]) {
                        matrix[i][j] = matrix[i][k] + matrix[k][j];
                        steps[i][j] = steps[i][k] + steps[k][j] + 1;
                    }
                }
            }
        }
        printMatrix(matrix, "Distance Matrix");
        printMatrix(steps, "Steps Matrix");
    }

    void printMatrix(int matrix[][], String matrixName) {
        System.out.println(matrixName + ":");

        // Print column headers
        System.out.print("      ");
        for (int i = 0; i < vertices; i++) {
            System.out.printf("%5d", i);
        }
        System.out.println();

        for (int i = 0; i < vertices; ++i) {
            // Print row header
            System.out.printf("%5d ", i);

            for (int j = 0; j < vertices; ++j) {
                if (matrix[i][j] == INF)
                    System.out.print(" INF ");
                else
                    System.out.printf("%5d", matrix[i][j]);
            }
            System.out.println();
        }
    }

    void printGraph() {
        for (int row = 0; row < adjacencyMatrix.length; row++) {
            for (int col = 0; col < adjacencyMatrix[row].length; col++) {
                System.out.printf("%5d", adjacencyMatrix[row][col]);
            }
            System.out.println();
        }

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

        g.printGraph();
        g.floydWarshall();
    }
}
