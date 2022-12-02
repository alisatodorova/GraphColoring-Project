/**
 * Recursive Largest First algorithm (based on greedy)
 * 3 sets:  U -> uncoloured vertices
 *          C -> Next colour class to be constructed
 *          W -> set of uncolored vertices with a neighbour in C
 *          Let Cv be the colour class that has v as its first vertex
 * 
 * Algorithm:
 * Algorithm RLF
 *   Input A graph G.
  *  Output A coloring of the vertices of G.
   * k  0.
    // while G contains uncolored vertices do
    // Let U be the set of uncolored vertices. Set k  k + 1.
    // Choose a vertex v ∈ U with largest value AU (v).
    // Construct Cv and assign color k to all vertices in Cv.
    // end while

// Construction of a colour class Cv:
// Input A set U of uncolored vertices and a vertex v ∈ U.
// Output A stable set Cv that contains v.
// Initialize W as the set of vertices in U adjacent to v.
// Remove v and all its neighbors from U and set Cv {v}.
// while U 6= ∅ do
// Select a vertex u ∈ U with largest value AW (u). In case of ties, choose one
// with smallest value AU (u).
// Move u from U to Cv, and move all neighbors w ∈ U of u to W.
// end while
 */

import java.util.*;

public class RLF {

    public int[] colours;
    public ColEdge[] edges;

    public HashSet<Integer> W;
    public HashSet<Integer> U;
    public HashSet<Integer> Cv;

    public HashMap<Integer, HashSet<Integer>> neighboursSet;

    public int[] degreesTot;
    public int[] degreesU;
    public int[] degreesW;

    public int[][] graph;
    public int n;

    public RLF(int[][] graph, int[] degrees, ColEdge[] edges) {
        this.graph = graph;
        degreesTot = Arrays.copyOf(degrees, degrees.length);
        //all nodes are in set U initially, set W is empty
        degreesU = Arrays.copyOf(degrees, degrees.length);
        this.edges = edges;

        n = graph[0].length;
        degreesW = new int[n];
        colours = new int[n];
        Arrays.fill(colours, -1);

        W = new HashSet<>();
        U = new HashSet<>();
        Cv = new HashSet<>();
        neighboursSet = new HashMap<>();
    }

    public int solve() {
        init();
        int c = 0;
        while (!U.isEmpty()){
            c++;
            constructCv(c);
            U.addAll(W);
            W.clear();
            Cv.clear();
            updateDegUW();
        }

        return c;
    }

    private void init() {
        for (int i=0; i<n; i++) {
            neighboursSet.put(i, new HashSet<Integer>());
            U.add(i);
        }
        for (int j=0; j<edges.length; j++) {
            neighboursSet.get(edges[j].u).add(edges[j].v);
            neighboursSet.get(edges[j].v).add(edges[j].u);
        }
    }

    private void constructCv(int c) {
        int firstNode = maxDegU();
        // System.out.println("First node coloured for C" + c + ": " + firstNode);
        colours[firstNode] = c;

        while (!U.isEmpty()) {
            int nextVertex = findNext();
            // System.out.println("Next: " + nextVertex);
            colours[nextVertex] = c;
            updateSets(nextVertex);
        }

    }

    //Updates sets U, W and Cv after a node is coloured
    private void updateSets(int node) {
        U.remove(node);
        Cv.add(node);
        HashSet<Integer> neighboursMain = neighboursSet.get(node);

        //for each neighbour in U of node moved to Cv, decrement degreesU
        for (Integer currNeighbour : neighboursMain) {
            if (U.contains(currNeighbour)) {
                degreesU[currNeighbour]--;
                U.remove(currNeighbour);
                W.add(currNeighbour);
                //Also, for each neighbour: find its neighbours 
                //for all those neighbours, decrement degreesU, increment degreesW
                HashSet<Integer> neighboursSec = neighboursSet.get(currNeighbour);
                for (Integer neighbourSec : neighboursSec) {
                    degreesU[neighbourSec]--;
                    degreesW[neighbourSec]++;
                }
            }
        }
    }

    private void updateDegUW() {
        for (int i=0; i<n; i++) {
            degreesU[i] = degreesW[i];
            degreesW[i] = 0;
        }
    }

    //Finds node in u with highest degree of neighbours in W
    //if two are equal, find the one with the lowest deg of neighbours in U
    private int findNext() {
        int degHigh = -1;
        int vertexHigh = -1;
        for (Integer vertex : U) {
            if (degreesW[vertex] > degHigh) {
                vertexHigh = vertex;
            }
            else if (degreesW[vertex] == degHigh) {
                vertexHigh = minDegU(vertex, vertexHigh);
            }
        }

        return vertexHigh;
    }

    //Finds node in u with highest degree of neighbours in U
    private int maxDegU() {
        int degHigh = -1;
        int vertexHigh = -1;
        for (Integer vertex : U) {
            if (degreesW[vertex] > degHigh) {
                vertexHigh = vertex;
            }
        }
        return vertexHigh;
    }

    //Finds node in u with highest degree of neighbours in U
    private int minDegU(int n1, int n2) {
        if (degreesU[n1] > degreesU[n2]) {
            return n2;
        }
        else if (degreesU[n2] > degreesU[n1]) {
            return n1;
        }
        else {
            return n1;
        }
    }
}
