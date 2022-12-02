
import java.util.*;

public class DSaturBacktrack {

    // degrees of saturation
    public int[] saturation, colorsArray, degrees;
    // graph in a form of adjacency matrix;
    public int[][] G;

    // graph in a form of adjacency list; list of colors adjacent to a vertex;
    public ArrayList<Integer>[] GList, adjColors;

    // number of nodes and maximum color allowed
    public int n, max_color, coloured;
    // final coloring
    public int[] finalColoring;

    /**
     * constructor
     * @param G graph in adj matrix
     * @param degrees array of degrees
     */
    public DSaturBacktrack(int[][] G, int[] degrees) {
        //number of nodes
        n = G[0].length;

        this.G = G;
        this.degrees = degrees;
        
        //initialize arrays
        saturation = new int[n];
        colorsArray = new int[n];
        GList = new ArrayList[n];
        adjColors = new ArrayList[n];

        //create the adjacency list of a matrix: array index v has list with object u iff v adjacent to u
        for (int v=0; v<n; v++) {
            GList[v] = new ArrayList<>();
            for (int u=0; u<n; u++) {
                if (G[v][u] == 1) {
                    GList[v].add(u);
                }
            }
        }
    }

    /**
     * Finds the highest ranked vertex: the one with the highest saturation degree
     * ties are broken by degrees
     * @return vertex
     */
    private int getHighestRankVertex() {
        //looks for uncoloured node with highest saturation
        int maxSat = -1;
        int maxVertex = -1;
        for (int i=0; i<n; i++) {
            if (colorsArray[i] == -1) {
                //if it unseats champion, replace it
                if (saturation[i] > maxSat) {
                    maxSat = saturation[i];
                    maxVertex = i;
                }
                //if saturation is equal, return one with highest degree
                else if (saturation[i] == maxSat) {
                    if (degrees[i] > degrees[maxVertex]) {
                        maxVertex = i;
                    }
                }
            }
        }
        return maxVertex;
    }

    // //update of saturation degrees for colour assignment
    // private void updateSat(int v, int c) {
    //     ArrayList<Integer> neighboursV = GList[v];

    //     //for each neighbour of v, call it w, 
    //     //check if the new colour assigned to v (c) is already in the adjColors array for w
    //     for (Integer w : neighboursV) {
    //         //if that colour wasnt there yet, add it and increase saturation for w
    //         if (!adjColors[w].contains((Integer) c)) {
    //             adjColors[w].add((Integer) c);
    //             saturation[w]++;
    //         }
    //     }
    // }

    // //update of saturation degrees for colour removal
    // private void updateSat(int v, int originalC, int remove) {
    //     //list of neighbours
    //     ArrayList<Integer> neighboursV = GList[v];

    //     //for each neighbour w, check if the removal of this colour impacts the saturation degree
    //     //by looking at the neighbours of w, nw, to see if any other neighbour has this colour
    //     //if another neighbour has this colour, it won't change saturation degree

    //     for (Integer w : neighboursV) {
    //         boolean changeSat = true;

    //         ArrayList<Integer> neighboursW = GList[w];
    //         for (Integer nw : neighboursW) {
    //             if (colorsArray[nw] == originalC) {
    //                 changeSat = false;
    //                 break;
    //             }
    //         }

    //         //if there is no other neighbour with colour originalC, decrement saturation degree
    //         //and remove the colour from its adjacent colours
    //         if (changeSat) {
    //             saturation[w]--;
    //             adjColors[w].remove((Integer) originalC);
    //         }
    //     }
        
    // }

    /**
     * Assigns a color to a vertex if possible, else it backtracks
     * This function is recursively called until all vertices
     * have been assigned a color
     * @param currV vertex being colored
     * @return true if it can be colored
     */
    private boolean assignColor(int currV) {
         
        //the counter coloured is equal to number of nodes
        //so all nodes are coloured
        if (coloured == n) {
            return true;
        }

        //Assign the lowest possible color to every edge
        //the lowest possible color is 1, as 0 is used for default.
        for (int c = 1; c <= max_color; c++)
        {
            //Checks if this color can be assigned to this edge without clashes
            if (!adjColors[currV].contains(c)) {
                //if so, color it with this color c
                colorsArray[currV] = c;
                coloured++;

                //for all neighbours of currV, add c to adjColours
                ArrayList<Integer> Nv = GList[currV];

                //this arraylist is for seeing which neighbours of currV
                //need their saturation degree to be updated
                ArrayList<Integer> newSat = new ArrayList<>();

                //if the neighbour of currV Nv did not have this color c
                //adjacent to it before, its saturation degree should be updated
                for (Integer n : Nv) {
                    if (!adjColors[n].contains(c)) {
                        saturation[n]++;
                        newSat.add(n);
                        adjColors[n].add(c);
                    }
                }
                
                //recur the method on the next node with highest saturation
                //This will branch out, with the final call returning true
                //if every node is assigned a color.
                if (assignColor(getHighestRankVertex())) {
                    return true;
                }

                //here, the recursion has failed, so we reset the color of currV to -1
                //for all neighbours of currV, remove c from adjColours iff it was in newSat
                for (Integer n : newSat) {
                    adjColors[n].remove((Integer) c);
                    saturation[n]--;
                }
                colorsArray[currV] = -1;
                coloured--;

            }
        }

        return false;
    }

    /**
     * Runs backtracking algorithm and returns its result
     * @return True if successful, False otherwise 
     */ 
    public boolean solve(int max) {
        for (int i=0; i<n; i++) {
            adjColors[i] = new ArrayList<>(max);
        }
        Arrays.fill(colorsArray, -1);
        max_color = max;
        coloured = 0;
        int nextV = getHighestRankVertex();
        boolean solved = assignColor(nextV);   
        return solved;
    }
}

    