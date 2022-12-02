package coloring;

class Adjacent {
  Boolean solve;
  int u;
  int v;
}

public class Checker {

  Adjacent adj = new Adjacent();

  public Checker() {}

  /**
   * For checking whether a graph is valid
   * @param e edges
   * @param ac assigned colors
   * @return Adjacent with boolean and edges if not valif
   */
  public Adjacent check(ColEdge[] e, int[] ac) {

    for (int i = 0; i < e.length; i++) {
      int u = e[i].u;
      int v = e[i].v;
      int posU = u+1;
      int posV = v+1;
      if (ac[u] == ac[v]) {
        adj.u = posU;
        adj.v = posV;
        adj.solve = false;
        return adj;
      }
    }

    adj.u = 0;
    adj.v = 0;
    adj.solve = true;
    return adj;
  }
  /**
   * For checking whether a node is valid
   * @param e edges
   * @param ac assigned colors
   * @param node node checked
   * @return boolean
   */
  public boolean check(ColEdge[] e, int[] ac, int node) {
    for (int i=0; i<e.length; i++) {
      if (e[i].u == node) {
        if (ac[e[i].v] == ac[node])
          return false;
      }
      else if (e[i].v == node) {
        if (ac[e[i].u] == ac[node])
          return false;
      }
    }
    return true;
  }
}
