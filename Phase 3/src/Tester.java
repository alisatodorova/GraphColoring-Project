/**
 * requires 1 input file
 */

public class Tester {

    public static void main(String[] args) {
        FileRead reader = new FileRead();

        // DIMACSRead reader = new DIMACSRead();

        ReadData data = new ReadData();
        data = reader.read(args[0]);

        SpecialGraphs specCase = new SpecialGraphs();
        AdjMatrixCreator matrixCreator = new AdjMatrixCreator();
        int[][] graph = matrixCreator.create(data.edges, data.nodes);

        RLF rlf = new RLF(graph, data.degArray, data.edges);
        MCP lowerCalc = new MCP(graph, data.degArray);
        DSaturBacktrack dsat = new DSaturBacktrack(graph, data.degArray);
        RLFn rlfn = new RLFn(graph, data.degArray, data.edges);

        int ub;
        int lb;

        int n = data.nodes; // |V|
        int m = data.edges.length; // |E|

        // if (specCase.isBipartite(graph, 0)) {System.out.println("CHROMATIC NUMBER = 2"); System.exit(0);}
        // if (specCase.isComplete(n, m)) {System.out.println("CHROMATIC NUMBER = " + n); System.exit(0);}

        lb = lowerCalc.search();
        System.out.println("NEW BEST LOWER BOUND = " + lb);

        ub = rlf.solve();
        System.out.println("NEW BEST UPPER BOUND = " + ub);

        if (ub == lb) {System.out.println("CHROMATIC NUMBER = " + ub); System.exit(0);};

        int tempUB;
        double incr;
        double P;
        long allowed;

        if (m < 100000) {
            P = 0.2;
            if (m < 10000) {allowed = 25000;}
            else if (m < 35000) {allowed = 40000;}
            else {
                allowed = 55000;
            }
        }

        else if (m < 200000) {
            P = 0.1;
            allowed = 70000;
        }

        else {
            P = 0.05;
            allowed = 70000;
        }

        if (m < 15000) {
            incr = 0.1;
        }
        else if (m < 25000) {
            incr = 0.15;
        }
        else if (m < 35000) {
            incr = 0.25;
        }
        else {
            incr = 0.25;
        }

        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < allowed && P <= 1.0) {
            tempUB = rlfn.solve(P);
            if (tempUB < ub) {
                if (ub == lb) {System.out.println("CHROMATIC NUMBER = " + ub); System.exit(0);};
                System.out.println("NEW BEST UPPER BOUND = " + tempUB);
                ub = tempUB;
            }
            else {
                System.out.println("RLF-"+P+" no benefit");
            }
            P = P + incr;
        }
        
        System.out.println("moving to dsat");
        int max = ub-1;

        while (ub != lb) {
            if (dsat.solve(max)) {
                System.out.println("NEW BEST UPPER BOUND = " + max);   
                if (max == lb) {System.out.println("BEST UB MATCHES LB, CHROMATIC NUMBER = " + max); System.exit(0);}; 
                ub = max;            
                max--;
            }
            else {
                System.out.println("couldnt solve for " + max);
                System.out.println("CHROMATIC NUMBER = " + (max+1));
                System.exit(0);
            }
        }
        
    }
}
