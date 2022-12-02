import java.io.*;
import java.util.*;

class ColEdge
{
    int u;
    int v;
}

class TimedCall {
  //Uses the built-in Timer class
  Timer timer = new Timer();
  //This is called after the upperBound() call
  //To ensure that if the backTrack() call is too slow
  //The system exits.
  TimerTask callApp = new TimerTask() {
  public void run() {
      System.out.println("Could not find exact chromatic number, too computationally intensive.");
      System.exit(0);
      }
  };

  public void timedCall() {
    timer.schedule(callApp, new Date(System.currentTimeMillis()+5*1000));
  }

}

public class Coloring
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
            if (e[i].u - 1 == node)
            {
                //If the adjacent node has no color, ignore it
                if (colors[e[i].v - 1] != -1)
                {
                    //and the adjacent node can't have this color
                    availableColors[colors[e[i].v - 1]] = false;
                }
            }

            //Checks the other endpoint, connected to the node
            if (e[i].v - 1 == node)
            {
                //If the adjacent node has no color, ignore it
                if (colors[e[i].u - 1] != -1)
                {
                    availableColors[colors[e[i].u - 1]] = false;
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
    private static void upperBound(ColEdge[] e, int n)
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
                    System.out.println("Node " + j + " has color " + colorsArray[j]);
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

        upperBound(e, n);
        System.out.println("\nThe time needed to perform this analysis was: " + (System.nanoTime() - start) / 1000000.0 + " ms.");
        System.out.println("");
        TimedCall timer = new TimedCall();
        timer.timedCall();
        backTrack(e, n);
        System.out.println("\nThe time needed to perform this analysis was: " + (System.nanoTime() - start) / 1000000.0 + " ms.");
        System.exit(0);
    }

}
