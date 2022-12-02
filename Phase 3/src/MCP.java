import java.util.*;

/**
 * Created by Bas
 * ------------- Maximal Clique Problem ------------
 * For generating a good lower bound within a time limit
 * We are using an algorithm inspired by Exact Algorithms for Maximum Clique: A Computational Study
 * Patrick Prosser
 * It uses two sets: C and P
 * C is the set with the growing clique, P is the set of candidates
 * We select a vertex v from P, adding it C, so C' = C U {v}
 * Then we construct a new P' = P intsect neighbours{v}
 * By constructing a new P' , we assure that every node in P' is adjacent
 * to every node in C
 * If |P| = 0, the constructed clique is maximal (local maximum != global maximum !!)
 * If |P| = 0 and |C| > maxSize, the clique is the current maximum and replaces maxSize
 * if |P| + |C| < maxSize, we can end the search, as it'll never exceed it
 * We repeat this method until all nodes have been selected as first vertex v
 * Or, the time has run out. The time may be set arbitrarily 
 * 
 * --------- MCP smarter solution -------------
 * We can improve the aforementioned algorithm by thinking about how we order P
 * Namely, what we do is, we solve the graph with a feasible solution (upper bound)
 * This gives us k colour classes, where k is the number of colours used
 * We order the vertices in P in increasing order of colour,
 * eg. all vertices with colour 1 first, then colour 2...
 * This gives the advantage that if with choose a vertex i in P, 
 * then we know that all vertices before i can be coloured with colours 1,2,...,colour[i]
 * Therefore, the maximum clique in the subgraph of all nodes till i is colour[i]
 * If this clique is too small to beat the current champion clique, we can abandon it.
 * In essence, we perform the WelshPowell algorithm first
 */
public class MCP {
    int[] degrees; //degrees of vertices
    int[][] graph; //adjacency matrix
    int n; //number of nodes

    int maxSize; //max clique size
    long cpuTime; //we set a max on runtime
    ArrayList<Integer>[] colourClasses;

    /**
     * constructor
     * @param graph adj matrix
     * @param degrees array of degrees
     */
    public MCP(int[][] graph, int[] degrees) {
        this.graph = graph;
        this.degrees = degrees;
        this.n = graph[0].length;
    }

    public int search() {
        ArrayList<Integer> C = new ArrayList<>(n); //set C (see explanation)
        ArrayList<Integer> P = new ArrayList<>(n); //set P (see explanation)

        //orders the candidates by degree
        findOrdering(P);
        maxSize = 0;

        //initialize the array of lists corresponding to color classes
        colourClasses = new ArrayList[n];
        for (int i=0; i<n; i++) {
            colourClasses[i] = new ArrayList<Integer>();
        }
        int colour[] = new int[P.size()]; //stores the colour which each vertex is assigned

        cpuTime = System.currentTimeMillis();
        //builds cliques
        build(C, P);
        //returns largest found clique in time frame
        return maxSize;
    }

    /**
     * builds cliques for each vertex as a starting vertex
     * abandons the current build if it cannot beat the champion clique
     * @param C set of current clique
     * @param P candidates vertices
     */
    void build(ArrayList<Integer> C, ArrayList<Integer> P) {
        if (System.currentTimeMillis() - cpuTime > 35000) {System.out.println("time up mcp"); return;}

        int m = P.size(); //remaining candidate vertices
        int colour[] = new int[m]; //stores the colour which each vertec is assigned
        sortByColour(C, P, P, colour); //Now P is sorted by colour properly
        for (int i=(m - 1); i>=0; i--) { // from high to low because the order of P is reversed
            
            //if size of the currently made clique 
            //+ the largest possible clique in subgraph P
            //can't exceed maxSize, return
            if ((C.size() + colour[i]) <= maxSize) {return;}

            //add a new vertex from candidate set to clique
            int vertex = P.get(i);
            C.add(vertex);
            ArrayList<Integer> newCandidates = new ArrayList<Integer>();
            for (Integer p : P) {
                //add all neighbours of current vertex to new candidate set
                //as a clique is a complete subgraph
                if (graph[vertex][p] == 1) {
                    newCandidates.add(p);
                }
            }
            //if no more candidates, check if size of current clique > max
            //if so, save the current solution
            if (newCandidates.isEmpty() && C.size() > maxSize) {
                maxSize = C.size();
            }
            //If not empty, keep building the clique with the new candidates
            if (!newCandidates.isEmpty()) {build(C, newCandidates);}

            //if it didn't beat the champion clique, remove the vertex and restart
            C.remove((Integer) vertex);
            P.remove((Integer) vertex);
        }        
    }

    void findOrdering(ArrayList<Integer> P) {
        // Init the element list
        List<Vertex> elements = new ArrayList<Vertex>();
        //add all vertices as objects with index and degree
        for (int i = 0; i < degrees.length; i++) {
            elements.add(new Vertex(i, degrees[i]));
        }

        // Sort and print
        Collections.sort(elements);

        for (int j=0; j<n; j++) {
            P.add(elements.get(j).index);
        }
    }

    /**
     * 
     * @param C clique set C
     * @param Ord the ordered set of candidates
     * @param P new set of candidates
     * @param colour array of colours to be used
     */
    void sortByColour(ArrayList<Integer> C, ArrayList<Integer> Ord, ArrayList<Integer> P, int[] colour) {
        int c=0;
        int m = Ord.size();
        //resets all colour classes, as no nodes have been coloured yet
        for (int i=0; i<m; i++) colourClasses[i].clear();
        //assigns each node first possible colour (greedy colouring)
        for (int i=0; i<m; i++) {
            int k=0;
            int v = Ord.get(i);
            //try to assign vertex v colour k
            while (!allowedColour(v, colourClasses[k])) {
                k++;
            }
            //add vertex v to the colour class k, as it has colour k
            colourClasses[k].add(v);
            //if a new colour is used, update c
            if (k+1 > c) {
                c = k+1;
            }
        }
        //get the new ordering of P according to colour classes
        P.clear();
        int counter = 0;
        //go through the color classes, and add the elements to P
        //in this way, elements with color 1 are first, then 2, then 3...
        for (int k=0; k<c; k++) {
            for (int j=0; j<colourClasses[k].size(); j++) {
                Integer temp = colourClasses[k].get(j);
                P.add(temp);
                colour[counter] = k+1; // we use k+1 to represent the first colour as 1
                counter++;
            }
        }
    }

    /**
     * Checks if a vertex can be assigned a color by
     * looking at the corresponding class
     * @param v vertex being colored
     * @param cc color class v is trying to be assigned
     * @return boolean
     */
    boolean allowedColour(int v, ArrayList<Integer> cc) {
        //go through the color class, and check
        //if any vertex u in color class Cv is adjacent to v
        for (int i=0; i<cc.size(); i++) {
            int u = cc.get(i);
            if (graph[v][u] == 1) {
                return false;
            }
        }
        return true;
    }

}