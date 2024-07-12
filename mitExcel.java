import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
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

    public void loadEdgesFromExcel(String filePath) throws IOException {
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    // Skip header row
                    continue;
                }
                int source = (int) row.getCell(0).getNumericCellValue();
                int destination = (int) row.getCell(1).getNumericCellValue();
                int weight = (int) row.getCell(2).getNumericCellValue();
                addEdge(source, destination, weight);
            }
        }
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
            totalWeight += weight / Math.pow(1 + 0.05, i);
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

    public static void main(String[] args) throws InterruptedException, IOException {
        Graph g = new Graph(37);
        
        // Load edges from Excel file
        g.loadEdgesFromExcel("C:\\Users\\TomPC\\Desktop\\graph_data.xlsx");

        // Print the adjacency matrix before generating build orders
        g.printMatrix(g.adjacencyMatrix, "Adjacency Matrix");
        g.printReachable();

        List<Integer> startNodes = Arrays.asList(0, 1, 9);
        Set<List<Integer>> allBuildOrders = new HashSet<>();

        long before = System.currentTimeMillis();
        while (allBuildOrders.size() < 1000 && System.currentTimeMillis() < before + 10000) {
            List<Integer> buildOrder = g.findOptimalPathFromMultipleSources(startNodes);
            allBuildOrders.add(buildOrder);
        }

        System.out.println("All Build Orders:");
        int count = 1;
        for (List<Integer> buildOrder : allBuildOrders) {
            double totalWeight = g.calculateTotalWeight(buildOrder);
            String totalWeightRounded = String.format("%.2f", totalWeight);
            System.out.println(count + ": " + buildOrder + " (Total Weight: " + totalWeightRounded + ")");
            count++;
        }
    }
}
