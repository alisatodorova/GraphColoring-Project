package coloring;

import java.util.Random;

public class CreateGraph
{

    //The graph
    public ColEdge[] graph;
    //The graph's size
    public int size;

    /**
    * Constructs a graph with n nodes and m edges.
    *
    * @param n the number of nodes
    * @param m the number of edges
    */
    public CreateGraph(int n, int m)
    {
        //Edge
        ColEdge e[] = null;
        //Random variable
        Random rand = new Random();

        //Creates a graph with a random edge
        if (n == 0 || m == 0)
        {
            n = rand.nextInt(10) + 5;
            m = n + rand.nextInt(10);
        }

        e = new ColEdge[m];

        for (int i = 0; i < m; i++)
        {
            //Creates a new edge
            e[i] = new ColEdge();

            //Checks if the new edge already exists
            if (i == 0)
            {
                //Creates new random edges
                e[i].u = rand.nextInt(n);
                e[i].v = rand.nextInt(n);

                //Checks if the two connecting nodes are the same
                while (e[i].u == e[i].v)
                {
                    //Creates new random edges
                    e[i].u = rand.nextInt(n);
                    e[i].v = rand.nextInt(n);
                }
            }

            //Checks if the new edge already exists
            else if (i > 0)
            {
                for(int k = 0; k < i; k++)
                {
                    //Checks if the connecting nodes are the same
                    while ((e[i].u == e[k].u && e[i].v == e[k].v) || (e[i].u == e[k].v && e[i].v == e[k].u) || (e[i].u == e[i].v))
                    {
                        //Creates new random edges
                        e[i].u = rand.nextInt(n);
                        e[i].v = rand.nextInt(n);
                    }

                }
            }
        }

        graph = e;
        size = n;
    }
}
