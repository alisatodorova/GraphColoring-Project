
/**
 * Creates an adjacency matrix (2D matrix) 
 * where matrix[x][y] is 1 if there is an edge between x and y
 * it is 0 if there is no edge between x and y
 */
public class AdjMatrixCreator {
    public int[][] create(ColEdge[] e, int n) {
        
        int[][] graphMatrix = new int[n][n];

        for (ColEdge edge : e) {
            graphMatrix[(edge.u)][(edge.v)] = 1;
            graphMatrix[(edge.v)][(edge.u)] = 1;
        } 

        return graphMatrix;
    }
}
