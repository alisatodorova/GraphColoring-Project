package coloring;

import java.util.*;

public class Hints {

  Checker checker = new Checker();
  Random rand = new Random();

  //Colours in words for hint messages
  public String[] colourStrings = {"white", "red", "green", "blue", "yellow", "light blue", "light purple", "olive",
  "dark purple", "orange", "gray blue", "gray", "aqua", "beige", "brown"};

  public Hints() {}

  /**
   * hint for mode 1
   * @param e edges
   * @param ac assigned colours
   * @param cn chromatic number
   * @param sol solution array
   * @return hint message
   */
  public String createHint1(ColEdge[] e, int[] ac, int cn, int[] sol) {
    String msg = null;

    //random for choosing one of two hints
    int rd = rand.nextInt(2);
    int max = 0;
    //checks how many colours are used
    for (int i=0; i<ac.length; i++) {
      if (ac[i] > max) {
        max = ac[i];
      }
    }
    max++;

    if (checker.check(e, ac).solve && (max == cn)) {
      return "You already solved the puzzle!";
    }

    //hint 1 looks at chromatic number
    if (rd == 0) {
      if (max > cn) {
        msg = "You are exceeding the chromatic number! Use at least " + (max - cn) + " less!";
      }
      else if (max < cn) {
        msg = "You can use " + (cn - max) + " more colour(s)!";
      }
      else {
        msg = "Don't use more colours!";
      }

    }

    //hint two recommends a colour for node based on solution array
    else if (rd == 1) {
      int rdNode = rand.nextInt(ac.length);
      if ((ac[rdNode]+1) == sol[rdNode]) {
        msg = "Node " + (rdNode + 1) + " is looking good!";
      }
      else {
        msg = "Try colouring node " + (rdNode + 1) + " with colour " + sol[rdNode] + " (" + colourStrings[(sol[rdNode]-1)] + ")!";
      }
    }

    return msg;
  }

  /**
   * hint for mode 2
   * @param e edges
   * @param ac assigned colours array
   * @param sol solution array
   * @return hint message
   */
  public String createHint2(ColEdge[] e, int[] ac, int[] sol) {
    String msg = null;
    int nonValid = -1;
    //rand for choosing on of two hints
    int rd = rand.nextInt(2);
    ArrayList<Integer> Choices = new ArrayList<Integer>();

    //finds first node that isn't validly coloured
    for (int i=0; i<ac.length; i++) {
      if (!checker.check(e, ac, i)) {
        nonValid = i;
        break;
      }
    }

    if (nonValid == -1)
      return "Your colouring is already valid!";

    //find a valid colour for that node
    //Array of booleans to see which colors are available
    boolean[] availableColors = new boolean[ac.length];
    //All colors are available at first
    Arrays.fill(availableColors, true);

    for (int i = 0; i < e.length; i++) {
        //Checks one endpoint, connected to the node
        if (e[i].u == nonValid) {
            //and the adjacent node can't have this color
            availableColors[ac[e[i].v]] = false;
        }
        //Checks the other endpoint, connected to the node
        if (e[i].v == nonValid) {
          availableColors[ac[e[i].u]] = false;
        }
    }

    if (rd == 0) {
      //look for the first three available colours and add them
      for (int j=0; j < availableColors.length; j++) {
        if (availableColors[j] && Choices.size() < 3) {
          Choices.add(j);
        }
      }

      msg = "Try assigning colour " + Choices.get(0) + " (" + colourStrings[Choices.get(0)] + "). or colour " +
      Choices.get(1) + " (" + colourStrings[Choices.get(1)] + "). or maybe even colour " + Choices.get(2) + " (" + colourStrings[Choices.get(2)] +
       ") to node " + (nonValid+1);
    }

    else if (rd == 1) {
      //look for the first three not available colours and add them
      for (int j=0; j < availableColors.length; j++) {
        if ((!availableColors[j]) && Choices.size() < 3) {
          Choices.add(j);
        }
      }
      if (Choices.size() == 1) {
        msg = "Forget about colour " + Choices.get(0) + " (" + colourStrings[Choices.get(0)] + ") for node " + (nonValid+1);
      }
      else if (Choices.size() == 2) {
        msg = "Forget about colour " + Choices.get(0) + " (" + colourStrings[Choices.get(0)] + ") and colour " +
        Choices.get(1) + " (" + colourStrings[Choices.get(1)] + ") for node " + (nonValid+1);

      }
      else {
        msg = "Forget about colour " + Choices.get(0) + " (" + colourStrings[Choices.get(0)] + "), colour " +
        Choices.get(1) + " (" + colourStrings[Choices.get(1)] + ") and colour " + Choices.get(2) + " (" + colourStrings[Choices.get(2)] +
         ") for node " + (nonValid+1);
       }
    }

    return msg;

  }

  /**
   * hint for mode 3
   * @param e edges
   * @param ac assigned colours array
   * @param current current node being coloured
   * @param ordering order of colouring
   * @return hint message
   */
  public String createHint3(ColEdge[] e, int[] ac, int current, int[] ordering) {
    String msg = null;
    ArrayList<Integer> Choices = new ArrayList<Integer>();
    int rd = rand.nextInt(2);
    System.out.println(rd);

    //Checks if the graph colouring is already valid
    Adjacent checkerReturn = checker.check(e, ac);
    if (checkerReturn.solve) {
      return "Your colouring is already valid!";
    }

    //the current node in ordering
    int nodeBeingColoured = ordering[current];

    //If this node already has a valid colouring, move on to next node in ordering
    if (checker.check(e, ac, nodeBeingColoured)) {
      nodeBeingColoured = ordering[(current+1)];
    }

    //Array of booleans to see which colors are available
    boolean[] availableColors = new boolean[ac.length];
    //All colors are available at first
    Arrays.fill(availableColors, true);

    for (int i = 0; i < e.length; i++) {
        //Checks one endpoint, connected to the node
        if (e[i].u == nodeBeingColoured) {
            //and the adjacent node can't have this color
            availableColors[ac[e[i].v]] = false;
        }
        //Checks the other endpoint, connected to the node
        if (e[i].v == nodeBeingColoured) {
          availableColors[ac[e[i].u]] = false;
        }
    }

    if (rd == 0) {
      //look for the first three available colours and add them
      for (int j=0; j < availableColors.length; j++) {
        if (availableColors[j] && Choices.size() < 3) {
          Choices.add(j);
        }
      }

      msg = "Try assigning colour " + Choices.get(0) + " (" + colourStrings[Choices.get(0)] + "), or colour " +
      Choices.get(1) + " (" + colourStrings[Choices.get(1)] + "), or maybe even colour " + Choices.get(2) + " (" + colourStrings[Choices.get(2)] +
       ") to node " + (nodeBeingColoured+1);
    }

    else if (rd == 1) {
      //look for the first three not available colours and add them
      for (int j=0; j < availableColors.length; j++) {
        if ((!availableColors[j]) && Choices.size() < 3) {
          Choices.add(j);
        }
      }
      if (Choices.size() == 1) {
        msg = "Forget about colour " + Choices.get(0) + " (" + colourStrings[Choices.get(0)] + ") for node " + (nodeBeingColoured+1);
      }
      else if (Choices.size() == 2) {
        msg = "Forget about colour " + Choices.get(0) + " (" + colourStrings[Choices.get(0)] + ") and colour " +
        Choices.get(1) + " (" + colourStrings[Choices.get(1)] + ") for node " + (nodeBeingColoured+1);

      }
      else {
        msg = "Forget about colour " + Choices.get(0) + " (" + colourStrings[Choices.get(0)] + "), colour " +
        Choices.get(1) + " (" + colourStrings[Choices.get(1)] + ") and colour " + Choices.get(2) + " (" + colourStrings[Choices.get(2)] +
         ") for node " + (nodeBeingColoured+1);
       }
    }

    return msg;
  }

}
