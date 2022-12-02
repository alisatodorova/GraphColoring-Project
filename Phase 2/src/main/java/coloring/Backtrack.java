package coloring;

import java.io.*;
import java.util.*;

class BacktrackSolution {
  int cn;
  int[] correctColours;
}

public class Backtrack
{

    public final static boolean DEBUG = true;

    public final static String COMMENT = "//";

    public Backtrack() {

    }

    /**
     * Recursive function assigning colors to a node.
     * It loops through all colors to findest the lowest possible color
     * Recursively calls itself on the next node
     * Base case is the last node, returning true if all nodes have been assigned
     * @param current The current node being assigned a color
     * @param m The maximum number of colors to be used
     * @param colorsArray The array corresponding to assigned colors
     * @param e Array of edges
     * @param n Total number of nodes
     * @return Boolean of solving the coloring problems with m colots
     */
    private static boolean assignColor(int current, int m, int[] colorsArray, ColEdge[] e, int n)
    {
        //The current index being assigned to is larger than the last,
        //so all nodes have been assigned colors.
        if (current == n)
        {
            return true;
        }

        //Assign the lowest possible color to every edge
        //the lowest possible color is 1, as 0 is used for default.
        for (int c = 1; c <= m; c++)
        {

            //Checks if this color can be assigned to this edge without clashes
            if (checkColor(current, c, colorsArray, e))
            {
                colorsArray[current] = c;
                //If so, recur the method on the next node
                //This will branch out, with the final call returning true
                //if every node is assigned a color.
                if (assignColor(current + 1, m, colorsArray, e, n))
                {
                    return true;
                }
            }
            colorsArray[current] = -1;
        }

        return false;
    }

    /**
     * Checks the validity of assigning a specific color to a node
     * by looking for adjacent nodes through the edges array.
     * If an adjacent node is found, it compares its color
     * to the color trying to be assigned
     * @param node Node to be assigned a color
     * @param color Color to be assigned
     * @param colorsArray Array of colors for all nodes
     * @param e Edges array
     * @return Boolean of whether a color may be assigned
     */
    private static boolean checkColor(int node, int color, int[] colorsArray, ColEdge[] e)
    {
        // Run through edges arr
        // If u = index, check which vertex is v -> check color of v
        // If v = index, check which vertex is u -> check color of u
        //
        for (int i = 0; i < e.length; i++)
        {
            //If one side of an edge is the node in question,
            //the other side is adjacent
            if (e[i].u == node)
            {

                if (colorsArray[e[i].v] == color)
                {
                    return false;
                }
            }

            if (e[i].v == node)
            {
                if (colorsArray[e[i].u] == color)
                {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Driver method, initiates the colorsArray with default values.
     * Calls the assign color method in a loop, incrementing the maximum
     * number of colors to used until a solution is found
     * Prints the color of each node and the chromatic number
     * @param e Array of edges
     * @param n Total number of nodes
     * @return Boolean, should always be true.
     */
    public static BacktrackSolution solve(ColEdge[] e, int n)
    {
        BacktrackSolution returnObj = new BacktrackSolution();
        //initiate colorsArray
        int[] colorsArray = new int[n];
        for (int i = 0; i < n; i++)
        {
            colorsArray[i] = -1;
        }

        //Tries to solve the problem with an increasing number of colors
        for (int max = 1; max <= n; max++)
        {
            if (assignColor(0, max, colorsArray, e, n))
            {
                //If solved, prints nodes and chromatic number
                // for (int j = 0; j < n; j++)
                // {
                //     System.out.println("Node " + j + "has color " + colorsArray[j]);
                // }
                System.out.println("Chromatic number: " + max);
                returnObj.cn = max;
                returnObj.correctColours = colorsArray;
                return returnObj;
            }
            //If not solved, resets colorsArray
            for (int i = 0; i < n; i++)
            {
                colorsArray[i] = -1;
            }
        }
        //Should not run
        System.out.println("Could not solve");
        return null;
    }

}
