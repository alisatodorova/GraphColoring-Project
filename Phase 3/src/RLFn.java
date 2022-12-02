import java.util.*;

public class RLFn {

    public int[] colours;
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

    //P is the percentage of vertices for which we create a trial class
    public double P;
    public int M;

    //all trial color classes for an iteration
    public ArrayList<CC_Construct> possibleClasses;

    /**
     * Constructor
     * @param graph adj matrix
     * @param degrees array of degrees
     * @param edges
     */
    public RLFn(int[][] graph, int[] degrees, ColEdge[] edges) {
        this.graph = graph;
        degreesTot = Arrays.copyOf(degrees, degrees.length);
        //all nodes are in set U initially, set W is empty
        degreesU = Arrays.copyOf(degrees, degrees.length);
        this.edges = edges;

        n = graph[0].length;
        degreesW = new int[n];

        //all vertices have color 1
        colours = new int[n];
        Arrays.fill(colours, -1);

        possibleClasses = new ArrayList<>();

        //initialize the sets
        W = new HashSet<>();
        U = new HashSet<>();
        Cv = new HashSet<>();

        neighboursSet = new HashMap<>();
    }

    /**
     * Solves the graph using M trial classes for each iteration
     * and choosing the best color class for each iteration
     * @param P the percentage of vertices to create a trial class for
     * @return number of colors used
     */
    public int solve(double P) {

        //M is the number of vertices for which to create a trial class
        M = (int) (P*n);

        //initialize sets
        init();
        int c = 0;
        //while U != empty means there are uncolored vertices
        while (!U.isEmpty()) {
            //increment color
            c++;

            //find the M vertices with highest degree of neighbours in U
            int[] topM = orderByDegU();
            //initialize 
            int[] residualEdges = new int[topM.length];

            //get copies of set U and the degrees to reset it later
            HashSet<Integer> UTemp = new HashSet<>(U);       
            int[] degreesUTemp = Arrays.copyOf(degreesU, degreesU.length);
            possibleClasses.clear();
           
            int count = 0;

            //for each vertex in the topM, create a trial color class
            for (int firstV : topM) {

                //initialize temporary sets of the current state
                U = new HashSet<>(UTemp);
                W = new HashSet<>();
                Cv = new HashSet<>();
                degreesU = Arrays.copyOf(degreesUTemp, n);
                degreesW = new int[n];

                //Construct the classs
                constructCv(c, firstV);

                //saves the state of the residual graph (uncolored nodes and their degrees)
                //in the possible classes list
                possibleClasses.add(new CC_Construct(W, degreesW));

                //stores the edges in the residual graph after creating this trial class
                //counts every edge twice, but doesn't matter
                residualEdges[count] = 0;
                for (Integer uv  : W) {
                    residualEdges[count] = residualEdges[count] + degreesW[uv];
                }

                count++;
            }
            

            //finds which trial class left the smallest residual graph (least edges)
            int minEdges = residualEdges[0];
            int minConstructIndex = 0;
            for (int i=0; i<residualEdges.length; i++) {
                if (residualEdges[i] < minEdges) {
                    minEdges = residualEdges[i];
                    minConstructIndex = i;
                }
            }

            //gets the state of the residual graph from the best trial class
            CC_Construct minConstruct = possibleClasses.get(minConstructIndex);

            //updates the actual sets to reflect the new state of the graph
            U = new HashSet<>(minConstruct.U);
            W = new HashSet<>();
            Cv = new HashSet<>();
            degreesU = Arrays.copyOf(minConstruct.degreesU, minConstruct.degreesU.length);
            degreesW = new int[n];
            
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
     * Construct a color class for color c and an initial vertex
     * @param c color
     * @param firstV first vertex in class
     */
    private void constructCv(int c, int firstV) {
        int firstNode = firstV;
        colours[firstNode] = c;
        updateSets(firstNode);
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

    //Finds first M nodes in U with highest degree of neighbours in U
    private int[] orderByDegU() {
        int ordering[];
        //if there are less than M uncolored nodes
        //we take simply each uncolored node
        if (U.size() < M) {
            ordering = new int[U.size()];
        }
        else {
            ordering = new int[M];
        }

        // Init the element list
        List<VertexU> elements = new ArrayList<VertexU>(U.size());
        
        int counter = 0;
        //add all M vertices to a list as an object with: index, degU
        for (int uv : U) {
            elements.add(new VertexU(uv, degreesU[counter]));
            counter++;
        }

        // Sort the list by degU
        Collections.sort(elements);

        for (int j=0; j<ordering.length; j++) {
            ordering[j] = elements.get(j).index;
        }

        //return the ordered array of size M
        return ordering;
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

//Class to store the information of a residual graph
//after creating a color class
class CC_Construct {
    HashSet<Integer> U;
    int[] degreesU;

    public CC_Construct(HashSet<Integer> U, int[] degreesU) {
        this.U = U;
        this.degreesU = degreesU;
    }
}