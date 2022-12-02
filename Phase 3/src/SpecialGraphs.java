
import java.util.*;

/**
 * class for testing the graphs on special cases
 */
public class SpecialGraphs {
     
    /**
     * Here we use BFS where we choose a root node
     * We colour it with colour 1
     * We then colour all it's neighbours with colour 2
     * We then colour all the neighbour's neighbours with colour 1 etc...
     * If we find a neighbour of a node already coloured with the same colour, it's not bipartite
     * We do it iteratively, not recursively
     * Bipartite graphs, graphs phase 3: 2, 4, 15
     * @param graph adj matrix
     * @param root root node, can just be 0
     * @return true / false
     */
    public boolean isBipartite(int[][] graph, int root) {
        int n = graph[0].length;
        int[] colorArr = new int[n];
        Arrays.fill(colorArr, -1);

        LinkedList<Integer> queue = new LinkedList<Integer>();
        
        //take node with highest degree to be root node
        queue.add(root);
        //set colour node 1 to 1
        colorArr[root] = 1;

        while (queue.size() != 0) {
            //takes first node in queue
            int currNode = queue.pop();
            int currNodeColour = colorArr[currNode];

            //assign its neighbours to have the other colour
            for (int i=0; i<n; i++) {
                //if the nodes are adjacent
                if (graph[currNode][i] == 1) {
                    //if the adjacent node to the current node has the same colour, it's false
                    if (colorArr[i] == currNodeColour)
                        return false;
                    else if (colorArr[i] == -1){
                        //0 if currNodeColour is 1, else 1.
                        colorArr[i] = 1 - currNodeColour;
                        queue.add(i);
                    }
                }

            }
        }

        return true;
    }


    /**
     * Tests for completeness of a graph
     * @param n number of nodes
     * @param m number of edges
     * @return true / false
     */
    public boolean isComplete(int n, int m) {
        if (m == (n*(n-1))/2) {
            return true;
        }
        
        else {
            return false;
        }
    }
}
