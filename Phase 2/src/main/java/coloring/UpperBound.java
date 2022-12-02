package coloring;

import java.io.*;
import java.util.*;


public class UpperBound
{

    public final static boolean DEBUG = true;

    public final static String COMMENT = "//";

    /**
     * Checks the validity of assigning a specific color to adjacent nodes
     * by going through each endpoint in the edges array.
     * If one endpoint is connected to the already colored node,
     * then this color is not available for the adjacent node.
     * @param e Edges array
     * @param node Node which already has a color
     * @param color Color of the node
     * @return A boolean array, which contains the validity of all colors
     */
    private static boolean[] adjacentColors(ColEdge[] e, int node, int[] colors)
    {
        //Array of booleans to see which colors are available
        boolean[] availableColors = new boolean[colors.length];

        //All colors are available at first
        Arrays.fill(availableColors, true);

        for (int i = 0; i < e.length; i++)
        {
            //Checks one endpoint, connected to the node
            if (e[i].u == node)
            {
                //If the adjacent node has no color, ignore it
                if (colors[e[i].v] != -1)
                {
                    //and the adjacent node can't have this color
                    availableColors[colors[e[i].v]] = false;
                }
            }

            //Checks the other endpoint, connected to the node
            if (e[i].v == node)
            {
                //If the adjacent node has no color, ignore it
                if (colors[e[i].u] != -1)
                {
                    availableColors[colors[e[i].u]] = false;
                }
            }
        }

        return availableColors;
    }

    /**
     * Driver method.
     * Assigns color 0 to first node and initially a default one for the rest.
     * Then assigns available colors to the adjacent nodes.
     * Prints the color of each node and the upper bound
     * @param e Array of edges
     * @param n Total number of nodes
     */
    public static int[] solve(ColEdge[] e, int n)
    {
        int[] colors = new int[n];

        for(int i = 0; i < colors.length; i++)
        {
            //The first vertex gets color 0
            if (i == 0)
            {
                colors[i] = 0;
            }
            else
            {
                //The other vertices get a default value
                colors[i] = -1;
            }
        }

        for(int j = 1; j < n; j++)
        {
            //Stores available colors for adjacent nodes
            boolean[] availableColors = adjacentColors(e, j, colors);

            for (int cr = 0; cr < n; cr++)
            {
                if (availableColors[cr])
                {
                    //The color is assigned to the node
                    colors[j] = cr;

                    //When the first available color is found,
                    //the method stops and then starts again.
                    break;
                }
            }
        }

        //Print the result
        for (int l = 0; l < n; l++)
        {
            System.out.println("Vertex " + l + " has color " + colors[l]);
        }

        int maxCol = 0;

        //Find the upper bound
        for (int x = 0; x < colors.length; x++)
        {
            //The upper bound is the biggest value from the colors
            if (colors[x] > maxCol)
            {
                maxCol = colors[x];
            }
        }

        System.out.println("The found upper bound is: " + (maxCol + 1));
        return colors;
    }

}
