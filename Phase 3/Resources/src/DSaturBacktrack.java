
import java.util.*;

public class DSaturBacktrack {

    // degrees of saturation
    public int[] saturation, colorsArray, degrees;
    // graph in a form of adjacency matrix;
    public int[][] G;

    // graph in a form of adjacency list, list of colors adjacent to a vertex;
    public ArrayList<Integer>[] GList, adjColors;

    // number of nodes and maximum color allowed
    public int n, max_color, coloured;
    // final coloring
    public int[] finalColoring;

    /**
     * Creates an object for one of backtracking optimizations
     * @param n_nodes number of nodes in the graph
     * @param max_color number of colors allowed
     * @param e graph in a form of edge list
     */
    public DSaturBacktrack(int[][] G, int[] degrees) {
        n = G[0].length;
        this.G = G;
        this.degrees = degrees;
        
        saturation = new int[n];
        colorsArray = new int[n];
        GList = new ArrayList[n];
        adjColors = new ArrayList[n];

        for (int v=0; v<n; v++) {
            GList[v] = new ArrayList<>();
            for (int u=0; u<n; u++) {
                if (G[v][u] == 1) {
                    GList[v].add(u);
                }
            }
        }
    }

    private int getHighestRankVertex() {
        //looks for uncoloured node with highest saturation
        int maxSat = -1;
        int maxVertex = -1;
        for (int i=0; i<n; i++) {
            if (colorsArray[i] == -1) {
                // System.out.println("considering " + i);
                if (saturation[i] > maxSat) {
                    maxSat = saturation[i];
                    maxVertex = i;
                    // System.out.println("vertex " + i + " is new max");
                }
                //if saturation is equal, return one with highest degree
                else if (saturation[i] == maxSat) {
                    if (degrees[i] > degrees[maxVertex]) {
                        maxVertex = i;
                        // System.out.println("vertex " + i + " is new max");
                    }
                }
            }
        }
        // System.out.println("vertex " + maxVertex + " is max with sat deg of " + maxSat);
        return maxVertex;
    }

    //update of saturation degrees for colour assignment
    private void updateSat(int v, int c) {
        ArrayList<Integer> neighboursV = GList[v];

        //for each neighbour of v, call it w, 
        //check if the new colour assigned to v (c) is already in the adjColors array for w
        for (Integer w : neighboursV) {
            //if that colour wasnt there yet, add it and increase saturation for w
            if (!adjColors[w].contains((Integer) c)) {
                adjColors[w].add((Integer) c);
                saturation[w]++;
            }
        }
    }

    //update of saturation degrees for colour removal
    private void updateSat(int v, int originalC, int remove) {
        //list of neighbours
        ArrayList<Integer> neighboursV = GList[v];

        //for each neighbour w, check if the removal of this colour impacts the saturation degree
        //by looking at the neighbours of w, nw, to see if any other neighbour has this colour
        //if another neighbour has this colour, it won't change saturation degree

        for (Integer w : neighboursV) {
            boolean changeSat = true;

            ArrayList<Integer> neighboursW = GList[w];
            for (Integer nw : neighboursW) {
                if (colorsArray[nw] == originalC) {
                    changeSat = false;
                    break;
                }
            }

            //if there is no other neighbour with colour originalC, decrement saturation degree
            //and remove the colour from its adjacent colours
            if (changeSat) {
                saturation[w]--;
                adjColors[w].remove((Integer) originalC);
            }
        }
        
    }

    private boolean assignColor(int currV) {
         //The currV index being assigned to is larger than the last,
        //so all nodes have been assigned colors.
        if (coloured == n) {
            return true;
        }

        //Assign the lowest possible color to every edge
        //the lowest possible color is 1, as 0 is used for default.
        for (int c = 1; c <= max_color; c++)
        {
            //Checks if this color can be assigned to this edge without clashes
            if (!adjColors[currV].contains(c)) {
                colorsArray[currV] = c;
                coloured++;

                //for all neighbours of currV, add c to adjColours
                ArrayList<Integer> Nv = GList[currV];
                ArrayList<Integer> newSat = new ArrayList<>();

                for (Integer n : Nv) {
                    if (!adjColors[n].contains(c)) {
                        newSat.add(n);
                        adjColors[n].add(c);
                    }
                }
                
                //If so, recur the method on the next node
                //This will branch out, with the final call returning true
                //if every node is assigned a color.
                if (assignColor(getHighestRankVertex())) {
                    return true;
                }

                //for all neighbours of currV, remove c from adjColours iff it was in newSat
                for (Integer n : newSat) {
                    adjColors[n].remove((Integer) c);
                }
                colorsArray[currV] = -1;
                coloured--;

            }
        }

        return false;
    }

    /**
     * Runs backtracking algorithm and returns its result
     * @return True is successful, False otherwise 
     */ 
    public boolean solve(int max) {
        for (int i=0; i<n; i++) {
            adjColors[i] = new ArrayList<>(max);
        }
        Arrays.fill(colorsArray, -1);
        max_color = max;
        coloured = 0;
        int nextV = getHighestRankVertex();
        System.out.println("First vertex: " + nextV);
        boolean solved = assignColor(nextV);   
        if (solved) {
            System.out.println("Solved for " + max);
        }
        else if (!solved) {
            System.out.println("Could not solve for " + max);
        }

        return solved;
    }
}

    