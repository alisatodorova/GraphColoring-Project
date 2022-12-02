import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;


public class WelshPowell {

    //adjacency matrix
    public int[][] graph;
    //array of degrees
    public int[] degrees;
    //number of nodes
    public int n;

    //all colored nodes
    public HashSet<Integer> C;
    //all candidate nodes
    public HashSet<Integer> V;

    //the ordering of vertices by degrees
    public int[] order;

    //assigned colors
    public int[] colours;
    
    /**
     * Constructor
     * @param graph adj matrix
     * @param degrees array of degrees
     */
    public WelshPowell(int[][] graph, int[] degrees) {
        this.graph = graph;
        this.degrees = degrees;
        n = graph[0].length;

        colours = new int[n];
        C = new HashSet<>();
        V = new HashSet<>();
    }

    public int solve() {
        order = findOrdering();
        for (int i=0; i<n; i++) {
            Integer temp = i;
            V.add(temp);
        }
        int c = 0;
        int nextVertex;
        int startVertex;

        while (C.size() != n) {
            c++;
            startVertex = findFirst();
            colour(startVertex, c);
            while (!V.isEmpty()) {
                nextVertex = findNext();
                colour(nextVertex, c);
            }
            for (int i=0; i<n; i++) {
                if (colours[i] == 0) {
                    V.add(i);
                }
            }
        }

        return c;
    }

    /**
     * Colours a node with certain color and updates sets
     * @param vertex v being colored
     * @param c color
     */
    private void colour(int vertex, int c) {
        colours[vertex] = c;
        C.add(vertex);
        V.remove(vertex);

        //remove all adjacent vertices from V
        for (int i=0; i<n; i++) {
            if (graph[vertex][i] == 1) {
                V.remove(i);
            }
        }
    }

    /**
     * Finds first vertex in ordering that hasn't been colored
     * and is a candidate
     * @return vertex
     */
    private int findNext() {
        int next = -1;
        for (int i=0; i<order.length; i++) {
            //if not coloured and still allowed (in V)
            if (colours[order[i]] == 0 && V.contains(order[i])) {
                next = order[i];
                break;
            }
        }

        return next;
    }

    /**
     * Finds first vertex in an iteration
     * @return vertex
     */
    private int findFirst() {
        int next = -1;
        for (int i=0; i<order.length; i++) {
            //if not coloured
            if (colours[order[i]] == 0) {
                next = order[i];
                break;
            }
        }

        return next;
    }

    /**
     * orders the vertices in an array by degrees
     * Does this using a new object and sorting that object
     * @return array of vertices ordered by degree
     */
    private int[] findOrdering() {

        int ordering[] = new int[n];
        // Init the element list
        List<Vertex> elements = new ArrayList<Vertex>();
        for (int i = 0; i < degrees.length; i++) {
            elements.add(new Vertex(i, degrees[i]));
        }

        // Sort by comparator (sorts by degree)
        Collections.sort(elements);

        //We are interested in the index of the node, not the degree
        for (int j=0; j<n; j++) {
            ordering[j] = elements.get(j).index;
        }

        return ordering;
    }

}
