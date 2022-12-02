public class Vertex implements Comparable<Vertex> {
        int index, deg;
    
        Vertex(int index, int deg){
            this.index = index;
            this.deg = deg;
        }
    
        public int compareTo(Vertex v) {
            if (deg > v.deg || deg == v.deg && index > v.index) return 1;
            return -1;
        }
}
