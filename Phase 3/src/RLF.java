import java.util.*;

/**
 * Class for a solver using RLF 
 */
public class RLF {

    //assigned colours
    public int[] colours;
    //edges
    public ColEdge[] edges;

    //The three sets to keep track of
    //further explanation to be found in report
    public HashSet<Integer> W;
    public HashSet<Integer> U;
    public HashSet<Integer> Cv;

    //Hashmap with key: vertex(v), and value: neighbours (v)
    public HashMap<Integer, HashSet<Integer>> neighboursSet;

    //degrees of vertices to be kept track of
    public int[] degreesTot;
    public int[] degreesU;
    public int[] degreesW;

    public int[][] graph;
    public int n;

    /**
     * Constructor
     * @param graph adj matrix
     * @param degrees array of degrees
     * @param edges
     */
    public RLF(int[][] graph, int[] degrees, ColEdge[] edges) {
        this.graph = graph;
        degreesTot = Arrays.copyOf(degrees, degrees.length);
        //all nodes are in set U initially, set W is empty
        degreesU = Arrays.copyOf(degrees, degrees.length);
        this.edges = edges;

        n = graph[0].length;
        degreesW = new int[n];
        //all vertices have color -1
        colours = new int[n];
        Arrays.fill(colours, -1);

        //initialize the sets
        W = new HashSet<>();
        U = new HashSet<>();
        Cv = new HashSet<>();
        neighboursSet = new HashMap<>();
    }

    /**
     * Solve the graph
     * @return number of colors
     */
    public int solve() {
        //initialize sets
        init();
        int c = 0;
        //while U != empty means there are uncolored vertices
        while (!U.isEmpty()){
            //increment color
            c++;
            //construct new color class
            constructCv(c);
            //update sets
            U.addAll(W);
            W.clear();
            Cv.clear();
            //update degrees
            updateDegUW();
        }

        return c;
    }

    /**
     * initialize set U and the neighbour set
     */
    private void init() {
        //add all vertices to U and neighbour set
        for (int i=0; i<n; i++) {
            neighboursSet.put(i, new HashSet<Integer>());
            U.add(i);
        }
        //for each edge, update the neighbour set
        //update it for both u and v
        for (int j=0; j<edges.length; j++) {
            neighboursSet.get(edges[j].u).add(edges[j].v);
            neighboursSet.get(edges[j].v).add(edges[j].u);
        }
    }

    /**
     * Construct a color class for color c
     * @param c color
     */
    private void constructCv(int c) {
        //find initial node for color class
        int firstNode = maxDegU();
        colours[firstNode] = c;

        //construct until there are no candidates (U is empty)
        while (!U.isEmpty()) {
            int nextVertex = findNext();
            colours[nextVertex] = c;
            updateSets(nextVertex);
        }

    }

    //Updates sets U, W and Cv after a node is coloured
    private void updateSets(int node) {
        //colored node moves from U to Cv
        U.remove(node);
        Cv.add(node);

        //for each neighbour in U of node moved to Cv, decrement degreesU
        HashSet<Integer> neighboursMain = neighboursSet.get(node);
        for (Integer currNeighbour : neighboursMain) {
            if (U.contains(currNeighbour)) {
                degreesU[currNeighbour]--;
                //move all neighbours from U to W
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

    /**
     * Reset the degreesU and W arrays after an iteration
     * all uncolored vertices move from W to U, so switch those
     * W is empty so set everything to 0
     */
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
    //return vertex
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

    /**
     * Compares the degreeU of two vertices
     */
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
