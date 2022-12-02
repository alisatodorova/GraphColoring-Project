import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;


public class WelshPowell {

    public int[][] graph;
    public int[] degrees;
    public int n;

    public HashSet<Integer> C;
    public HashSet<Integer> V;

    public int[] order;

    public int[] colours;
    
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






    private int[] findOrdering() {

        int ordering[] = new int[n];
        // Init the element list
        List<Vertex> elements = new ArrayList<Vertex>();
        for (int i = 0; i < degrees.length; i++) {
            elements.add(new Vertex(i, degrees[i]));
        }

        // Sort and print
        Collections.sort(elements);

        for (int j=0; j<n; j++) {
            ordering[j] = elements.get(j).index;
        }

        return ordering;
    }

}
