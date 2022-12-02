import java.io.*;
import java.util.*;

class ColEdge
{
    int u;
    int v;
}

public class Backtrack
{

    public final static boolean DEBUG = true;

    public final static String COMMENT = "//";

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
            if (e[i].u - 1 == node)
            {

                if (colorsArray[e[i].v - 1] == color)
                {
                    return false;
                }
            }

            if (e[i].v - 1 == node)
            {
                if (colorsArray[e[i].u - 1] == color)
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
    private static boolean backTrack(ColEdge[] e, int n)
    {

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
                for (int j = 0; j < n; j++)
                {
                    System.out.println("Node " + j + "has color " + colorsArray[j]);
                }
                System.out.println("Chromatic number: " + max);
                return true;
            }
            //If not solved, resets colorsArray
            for (int i = 0; i < n; i++)
            {
                colorsArray[i] = -1;
            }
        }
        //Should not run
        System.out.println("Could not solve");
        return false;
    }

    public static void main( String args[] )
    {
        if( args.length < 1 )
        {
            System.out.println("Error! No filename specified.");
            System.exit(0);
        }


        String inputfile = args[0];

        boolean seen[] = null;

        //! n is the number of vertices in the graph
        int n = -1;

        //! m is the number of edges in the graph
        int m = -1;

        //! e will contain the edges of the graph
        ColEdge e[] = null;

        try
        {
            FileReader fr = new FileReader(inputfile);
            BufferedReader br = new BufferedReader(fr);

            String record = new String();

            //! THe first few lines of the file are allowed to be comments, staring with a // symbol.
            //! These comments are only allowed at the top of the file.

            //! -----------------------------------------
            while ((record = br.readLine()) != null)
            {
                if( record.startsWith("//") ) continue;
                break; // Saw a line that did not start with a comment -- time to start reading the data in!
            }

            if( record.startsWith("VERTICES = ") )
            {
                n = Integer.parseInt( record.substring(11) );
                if(DEBUG) System.out.println(COMMENT + " Number of vertices = " + n);
            }

            seen = new boolean[n + 1];

            record = br.readLine();

            if( record.startsWith("EDGES = ") )
            {
                m = Integer.parseInt( record.substring(8) );
                if(DEBUG) System.out.println(COMMENT + " Expected number of edges = " + m);
            }

            e = new ColEdge[m];

            for( int d = 0; d < m; d++)
            {
                if(DEBUG) System.out.println(COMMENT + " Reading edge " + (d + 1));
                record = br.readLine();
                String data[] = record.split(" ");
                if( data.length != 2 )
                {
                    System.out.println("Error! Malformed edge line: " + record);
                    System.exit(0);
                }
                e[d] = new ColEdge();

                e[d].u = Integer.parseInt(data[0]);
                e[d].v = Integer.parseInt(data[1]);

                seen[ e[d].u ] = true;
                seen[ e[d].v ] = true;

                if(DEBUG) System.out.println(COMMENT + " Edge: " + e[d].u + " " + e[d].v);

            }

            String surplus = br.readLine();
            if( surplus != null )
            {
                if( surplus.length() >= 2 ) if(DEBUG) System.out.println(COMMENT + " Warning: there appeared to be data in your file after the last edge: '" + surplus + "'");
            }

        }
        catch (IOException ex)
        {
            // catch possible io errors from readLine()
            System.out.println("Error! Problem reading file " + inputfile);
            System.exit(0);
        }

        for( int x = 1; x <= n; x++ )
        {
            if( seen[x] == false )
            {
                if(DEBUG) System.out.println(COMMENT + " Warning: vertex " + x + " didn't appear in any edge : it will be considered a disconnected vertex on its own.");
            }
        }

        //Measure the time needed to find the solution
        long start = System.nanoTime();
        backTrack(e, n);
        System.out.println("\nThe time needed to perform this analysis was: " + (System.nanoTime() - start) / 1000000.0 + " ms.");
    }

}
