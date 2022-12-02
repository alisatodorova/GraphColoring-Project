import java.io.*;
import java.util.*;

class ColEdge
{
    int u;
    int v;
}

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

    }

}
