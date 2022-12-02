/**
 * Object for comparing vertices by degree, the number of neighbours
 * Used for WP algorithm
 */
public class Vertex implements Comparable<Vertex> {
        int index, deg;
    
        Vertex(int index, int deg){
            this.index = index;
            this.deg = deg;
        }
    
        //compares by degree, ties broken by index
        public int compareTo(Vertex v) {
            if (deg > v.deg || deg == v.deg && index > v.index) return 1;
            return -1;
        }
}
