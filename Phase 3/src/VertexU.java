/**
 * Object for comparing vertices by Au(v), the number of neighbours in U
 * Used for RLF and RLF-n algorithm
 */
public class VertexU implements Comparable<VertexU> {
    int index, degU;

    /**
     * index and degree in U
     */
    VertexU(int index, int degU){
        this.index = index;
        this.degU = degU;
    }

    /**
     * comparator
     * compares by degU, ties broken simply by index
     */
    public int compareTo(VertexU v) {
        if (degU > v.degU || degU == v.degU && index > v.index) 
            return -1;
        return 1;
    }
}