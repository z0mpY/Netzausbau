package demo;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.*;
import java.io.*;
import java.util.*;

class Edge {
    String source;
    String destination;
    int weight;

    Edge(String source, String destination, int weight) {
        this.source = source;
        this.destination = destination;
        this.weight = weight;
    }
}

class Graph {
    private final int[][] adjacencyMatrix;
    private final Map<String, Integer> nodeIndexMap;
    private final int vertices;
    final static int INF = -99999;
    private final Random random;

    public Graph(int vertices) {
        this.vertices = vertices;
        adjacencyMatrix = new int[vertices][vertices];
        nodeIndexMap = new HashMap<>();
        random = new Random();

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

    public void addEdge(String source, String destination, int weight) {
        int sourceIndex = nodeIndexMap.computeIfAbsent(source, k -> nodeIndexMap.size());
        int destIndex = nodeIndexMap.computeIfAbsent(destination, k -> nodeIndexMap.size());
        adjacencyMatrix[sourceIndex][destIndex] = weight;
    }

    public List<String> findOptimalPathFromMultipleSources(List<String> startNodes) {
        List<String> stillToBuild = new ArrayList<>(nodeIndexMap.keySet());
        List<String> alreadyBuiltNodes = new ArrayList<>();
        List<String> buildOrder = new ArrayList<>();
        List<String> reachableNodes = new ArrayList<>(startNodes);

        while (!stillToBuild.isEmpty()) {
            int randomIndex = random.nextInt(reachableNodes.size());
            String nodeToBuild = reachableNodes.get(randomIndex);

            buildOrder.add(nodeToBuild);
            alreadyBuiltNodes.add(nodeToBuild);
            stillToBuild.remove(nodeToBuild);
            reachableNodes.remove(nodeToBuild);

            List<String> newReachableNodes = getReachableNodes(nodeToBuild, alreadyBuiltNodes);
            for (String node : newReachableNodes) {
                if (!reachableNodes.contains(node) && !alreadyBuiltNodes.contains(node)) {
                    reachableNodes.add(node);
                }
            }
            for (String startNode : startNodes) {
                if (stillToBuild.contains(startNode) && !reachableNodes.contains(startNode)) {
                    reachableNodes.add(startNode);
                }
            }
        }

        return buildOrder;
    }

    private List<String> getReachableNodes(String node, List<String> alreadyBuiltNodes) {
        List<String> reachableNodes = new ArrayList<>();

        Integer nodeIndexInteger = nodeIndexMap.get(node);
        if (nodeIndexInteger == null) {
            System.err.println("Node '" + node + "' not found in nodeIndexMap.");
            return reachableNodes;
        }

        int nodeIndex = nodeIndexInteger.intValue();
        for (int i = 0; i < vertices; i++) {
            if (adjacencyMatrix[nodeIndex][i] != INF && adjacencyMatrix[nodeIndex][i] != 0 && !alreadyBuiltNodes.contains(getNodeName(i))) {
                reachableNodes.add(getNodeName(i));
            }
        }

        return reachableNodes;
    }

    public double calculateTotalWeight(List<String> buildOrder) {
        double totalWeight = 0.0;
        for (int i = 1; i < buildOrder.size(); i++) {
            String node = buildOrder.get(i);
            int weight = 0;
            int nodeIndex = nodeIndexMap.get(node);
            for (int j = 0; j < vertices; j++) {
                if (adjacencyMatrix[j][nodeIndex] != INF && adjacencyMatrix[j][nodeIndex] != 0) {
                    weight = adjacencyMatrix[j][nodeIndex];
                    break;
                }
            }
            totalWeight += weight / Math.pow(1 + 0.05, i);
        }
        return totalWeight;
    }

    public static List<Edge> readEdgesFromExcel(String filePath) throws IOException {
        List<Edge> edges = new ArrayList<>();
        FileInputStream fis = new FileInputStream(filePath);
        Workbook workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheetAt(1);  // Second sheet should be the Edges sheet
    
        for (Row row : sheet) {
            if (row.getRowNum() == 0) {
                continue;
            }
    
            Cell sourceCell = row.getCell(0);
            Cell destinationCell = row.getCell(1);
            Cell weightCell = row.getCell(2);
    
            String source;
            String destination;
            int weight;
    
            if (sourceCell == null) {
                throw new IllegalStateException("Source cell is null for row: " + row.getRowNum());
            }
            switch (sourceCell.getCellType()) {
                case STRING:
                    source = sourceCell.getStringCellValue();
                    break;
                case NUMERIC:
                    source = String.valueOf((int) sourceCell.getNumericCellValue());
                    break;
                case BLANK:
                    throw new IllegalStateException("Source cell is blank for row: " + row.getRowNum());
                default:
                    throw new IllegalStateException("Unexpected cell type encountered for source cell in row: " + row.getRowNum());
            }
    
            if (destinationCell == null) {
                throw new IllegalStateException("Destination cell is null for row: " + row.getRowNum());
            }
            switch (destinationCell.getCellType()) {
                case STRING:
                    destination = destinationCell.getStringCellValue();
                    break;
                case NUMERIC:
                    destination = String.valueOf((int) destinationCell.getNumericCellValue());
                    break;
                case BLANK:
                    throw new IllegalStateException("Destination cell is blank for row: " + row.getRowNum());
                default:
                    throw new IllegalStateException("Unexpected cell type encountered for destination cell in row: " + row.getRowNum());
            }
    
            if (weightCell == null || weightCell.getCellType() != CellType.NUMERIC) {
                throw new IllegalStateException("Weight should be numeric for row: " + row.getRowNum());
            }
            weight = (int) weightCell.getNumericCellValue();
    
            edges.add(new Edge(source, destination, weight));
        }
    
        workbook.close();
        fis.close();
        return edges;
    }

    public static List<String> readStartNodesFromExcel(String filePath) throws IOException {
        List<String> startNodes = new ArrayList<>();
        FileInputStream fis = new FileInputStream(filePath);
        Workbook workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheetAt(0);  // First sheet should be the Einstellungen sheet

        if (sheet == null) {
            throw new IllegalStateException("Sheet 'Einstellungen' not found in the Excel file.");
        }

        for (Row row : sheet) {
            Cell nodeCell = row.getCell(0);
            String node;

            if (nodeCell == null) {
                continue;  // Skip empty rows
            }
            switch (nodeCell.getCellType()) {
                case STRING:
                    node = nodeCell.getStringCellValue();
                    break;
                case NUMERIC:
                    node = String.valueOf((int) nodeCell.getNumericCellValue());
                    break;
                case BLANK:
                    continue;  // Skip blank cells
                default:
                    throw new IllegalStateException("Unexpected cell type encountered for node cell in row: " + row.getRowNum());
            }

            startNodes.add(node);
        }

        workbook.close();
        fis.close();
        return startNodes;
    }

    public static void writeBuildOrdersToExcel(Set<List<String>> buildOrders, Map<List<String>, Double> buildOrderWeights, String filePath) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Build Orders");

        CellStyle decimalStyle = workbook.createCellStyle();
        decimalStyle.setDataFormat(workbook.createDataFormat().getFormat("#0.00"));

        int rowIndex = 0;
        for (List<String> order : buildOrders) {
            Row row = sheet.createRow(rowIndex);
            
            Cell cellOrder = row.createCell(0);
            cellOrder.setCellValue(order.toString());
            
            Cell cellWeight = row.createCell(1);
            cellWeight.setCellValue(buildOrderWeights.get(order));
            cellWeight.setCellStyle(decimalStyle);
            
            rowIndex++;
        }

        FileOutputStream fos = new FileOutputStream(filePath);
        workbook.write(fos);
        workbook.close();
        fos.close();
    }

    private String getNodeName(int index) {
        for (Map.Entry<String, Integer> entry : nodeIndexMap.entrySet()) {
            if (entry.getValue() == index) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Excel Drop Reader");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 300);

            JLabel label = new JLabel("Drag & Drop an Excel file here", SwingConstants.CENTER);
            frame.add(label);

            new DropTarget(label, new DropTargetListener() {
                public void dragEnter(DropTargetDragEvent dtde) {}
                public void dragOver(DropTargetDragEvent dtde) {}
                public void dropActionChanged(DropTargetDragEvent dtde) {}
                public void dragExit(DropTargetEvent dte) {}

                @Override
                public void drop(DropTargetDropEvent dtde) {
                    try {
                        dtde.acceptDrop(dtde.getDropAction());
                        List<File> droppedFiles = (List<File>) dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                        for (File file : droppedFiles) {
                            if (file.getName().endsWith(".xlsx")) {
                                processExcelFile(file);
                            } else {
                                JOptionPane.showMessageDialog(null, "Please drop an Excel file.");
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            frame.setVisible(true);
        });
    }

    private static void processExcelFile(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            Workbook workbook = new XSSFWorkbook(fis);
            
            // Ensure correct sheet order
            Sheet einstellungenSheet = workbook.getSheetAt(0); // First sheet should be "Einstellungen"
            Sheet edgesSheet = workbook.getSheetAt(1); // Second sheet should be "Edges"
            
            if (!einstellungenSheet.getSheetName().equals("Einstellungen")) {
                throw new IllegalStateException("First sheet is not 'Einstellungen'. Please reorder the sheets.");
            }
            
            if (!edgesSheet.getSheetName().equals("Graph")) {
                throw new IllegalStateException("Second sheet is not 'Graph'. Please reorder the sheets.");
            }
            
            List<String> startNodes = readStartNodesFromExcel(file.getAbsolutePath());
            List<Edge> edges = readEdgesFromExcel(file.getAbsolutePath());

            Set<String> uniqueNodes = new HashSet<>();
            for (Edge edge : edges) {
                uniqueNodes.add(edge.source);
                uniqueNodes.add(edge.destination);
            }

            // Create graph with the number of unique nodes
            Graph g = new Graph(uniqueNodes.size());

            for (Edge edge : edges) {
                g.addEdge(edge.source, edge.destination, edge.weight);
            }

            Set<List<String>> allBuildOrders = new HashSet<>();
            Map<List<String>, Double> buildOrderWeights = new HashMap<>();

            long before = System.currentTimeMillis();
            while (allBuildOrders.size() < 1000 && System.currentTimeMillis() < before + 10000) {
                List<String> buildOrder = g.findOptimalPathFromMultipleSources(startNodes);
                double totalWeight = g.calculateTotalWeight(buildOrder);
                allBuildOrders.add(buildOrder);
                buildOrderWeights.put(buildOrder, totalWeight);
            }

            String outputExcelFilePath = file.getParent() + "/build_orders.xlsx";
            writeBuildOrdersToExcel(allBuildOrders, buildOrderWeights, outputExcelFilePath);

            JOptionPane.showMessageDialog(null, "Build orders with weights have been written to: " + outputExcelFilePath);

            workbook.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
