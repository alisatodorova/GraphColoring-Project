/**
 * requires 1 input file
 */

public class Tester {

    public static void main(String[] args) {
        FileRead reader = new FileRead();
        ReadData data = new ReadData();
        data = reader.read(args[0]);

        SpecialGraphs specCase = new SpecialGraphs();
        AdjMatrixCreator matrixCreator = new AdjMatrixCreator();
        int[][] graph = matrixCreator.create(data.edges, data.nodes);
        RLF rlf = new RLF(graph, data.degArray, data.edges);
        UpperBound up = new UpperBound();
        WelshPowell wp = new WelshPowell(graph, data.degArray);
        
        System.out.println(wp.solve());
        up.solve(data.edges, data.nodes);
    }
}
