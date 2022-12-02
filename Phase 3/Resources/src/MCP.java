import java.util.*;

/**
 * Created by Bas
 * ------------- Maximal Clique Problem ------------
 * For generating a good lower bound within a time limit
 * We are using an algorithm from Exact Algorithms for Maximum Clique: A Computational Study
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
 * then we know that all colours before i can be coloured with colours 1,2,...,colour[i]
 * Therefore, the maximum clique in the subgraph of all nodes till i is colour[i]
 * If this clique is too small to beat the current champion clique, we can abandon it.
 */
public class MCP {
    int[] degrees; //degrees of vertices
    int[][] graph; //adjacency matrix
    int n; //number of nodes

    int maxSize; //max clique size
    long cpuTime;
    ArrayList<Integer>[] colourClasses;

    public MCP(int[][] graph, int[] degrees) {
        this.graph = graph;
        this.degrees = degrees;
        this.n = graph[0].length;
    }

    public int search() {
        ArrayList<Integer> C = new ArrayList<>(n); //set C (see explanation)
        ArrayList<Integer> P = new ArrayList<>(n); //set P (see explanation)
        findOrdering(P);
        maxSize = 0;

        colourClasses = new ArrayList[n];
        for (int i=0; i<n; i++) {
            colourClasses[i] = new ArrayList<Integer>();
        }
        int colour[] = new int[P.size()]; //stores the colour which each vertec is assigned

        sortByColour(C, P, P, colour);
        cpuTime = System.currentTimeMillis();
        build(C, P);
        return maxSize;
    }

    void build(ArrayList<Integer> C, ArrayList<Integer> P) {
        if (System.currentTimeMillis() - cpuTime > 2000) {return;}

        int m = P.size(); //remaining candidate vertices
        int colour[] = new int[m]; //stores the colour which each vertec is assigned
        sortByColour(C, P, P, colour); //Now P is sorted by colour properly
        for (int i=(m - 1); i>=0; i--) { // from high to lows
            //if size of the currently made clique 
            //+ the largest possible clique in subgraph P
            //can't exceed maxSize, return
            if ((C.size() + colour[i]) <= maxSize) {return;}
            int vertex = P.get(i);
            C.add(vertex);
            ArrayList<Integer> newCandidates = new ArrayList<Integer>();
            for (Integer p : P) {
                //add all neighbours of current vertex to new candidate set
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

            C.remove((Integer) vertex);
            P.remove((Integer) vertex);
        }        
    }

    void findOrdering(ArrayList<Integer> P) {
        // Init the element list
        List<Vertex> elements = new ArrayList<Vertex>();
        for (int i = 0; i < degrees.length; i++) {
            elements.add(new Vertex(i, degrees[i]));
        }

        // Sort and print
        Collections.sort(elements);

        for (int j=0; j<n; j++) {
            P.add(elements.get(j).index);
        }
    }

    void sortByColour(ArrayList<Integer> C, ArrayList<Integer> Ord, ArrayList<Integer> P, int[] colour) {
        int c=0;
        int m = Ord.size();
        //resets all colour classes, as no nodes have been coloured yet
        for (int i=0; i<m; i++) colourClasses[i].clear();
        //assigns each node first possible colour (greedy colouring)
        for (int i=0; i<m; i++) {
            int k=0;
            int v = Ord.get(i);
            while (!allowedColour(v, colourClasses[k])) {
                k++;
            }
            //checks if new colours are used
            colourClasses[k].add(v);
            if (k+1 > c) {
                c = k+1;
            }
        }
        //get the new ordering of P according to colour classes
        P.clear();
        int counter = 0;
        for (int k=0; k<c; k++) {
            for (int j=0; j<colourClasses[k].size(); j++) {
                Integer temp = colourClasses[k].get(j);
                P.add(temp);
                colour[counter] = k+1; // we use k+1 to represent the first colour as 1
                counter++;
            }
        }
    }

    boolean allowedColour(int v, ArrayList<Integer> cc) {
        for (int i=0; i<cc.size(); i++) {
            int u = cc.get(i);
            if (graph[v][u] == 1) {
                return false;
            }
        }
        return true;
    }

}