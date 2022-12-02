package coloring;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Group;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.scene.shape.*;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.control.Control;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TextField;
import javafx.scene.Node;
import javafx.scene.paint.Color;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.SwingUtilities;
import javafx.embed.swing.SwingFXUtils;

import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;

public class Graph {

	public ColEdge[] edges;

	public int nodes;

	//set mode to 3 to trigger the allowed colours array on clicking
	public int mode = 0;

	public StackPane pane;

	//node buttons
	public Button[] nodebtn = null;

	public double centerX = 0;
	public double centerY = -20;
	public double radius = 250;

	public Rectangle r;

	public Line[] line;

	//these variables are for coloring
	public String[] node_colors = new String[15];

	public int colors_counter = 0;

	public int[] assignedColors;

	public int[] nodes_on_color = new int[15];

	//for mode 3
	public boolean[] allowedNodes;

	public Checker checker = new Checker();

	public Label mode3Warning = new Label();

	//Used only for mode 3, sees which of the two available nodes was coloured last
	int latestNodeColoured = 0;

	//Should be set in ThirdGui class if mode is 3. Should start with first node = node 0!!
	public int[] ordering = null;

	public String[] colours = {"-fx-background-color: #ffffff; ", "-fx-background-color: #ff0000; ", "-fx-background-color: #00FF00; ", "-fx-background-color: #0000FF; ",
		"-fx-background-color: #FFFF00; ", "-fx-background-color: #00FFFF; ", "-fx-background-color: #FF00FF; ",
		"-fx-background-color: #808000; ", "-fx-background-color: #800080; ", "-fx-background-color: #FF8C00; ", "-fx-background-color: #87CEEB; ",
		"-fx-background-color: #C0C0C0; ", "-fx-background-color: #7FFFD4; ", "-fx-background-color: #FFFACD; ", "-fx-background-color: #8B4513; "};


	public Graph(int nodes, ColEdge[] edges) {
		this.nodes = nodes;
		this.edges = edges;
	}

	public void buildGraphics() {

		Random rand = new Random();
		nodebtn = new Button[nodes];
		assignedColors = new int[nodes];
		allowedNodes = new boolean[nodes];

		mode3Warning.setTranslateY(-315);
		mode3Warning.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
		mode3Warning.setTextFill(Color.web("#f5f5dc"));

		//if game mode is 3 (ordering not null),
		//set the first 2 colours in the ordering to be allowed to be coloured, rest not allowed
		if (ordering != null) {
			for (int i = 0; i<ordering.length; i++) {
				if (i == 0 || i == 1) {
					allowedNodes[ordering[i]] = true;
				}
				else {
					allowedNodes[ordering[i]] = false;
				}
			}
		}

		for (int i=0; i<nodes; i++) {
			assignedColors[i] = 0;
		}

		//rectangle around graph
		r = new Rectangle(centerX - radius - 20, centerY - radius - 20, 2 * radius + 40, 2 * radius + 40);
		r.setFill(Color.TRANSPARENT);
		r.setStroke(Color.BLACK);
		r.setTranslateX(centerX);
		r.setTranslateY(centerY);
		pane.getChildren().addAll(r);

		//Node colouring
		for (int i = 0; i < colours.length; i++) {
			node_colors[i] = colours[i];
			nodes_on_color[i] = 0;
		}

		// Node Positioning
		for (int i = 0, angle = 0; i < nodes; i++, angle += (360.0 / nodes)) {
			//Circular
			double xProj = Math.cos(angle * Math.PI / 180.0) * radius;
			double yProj = Math.sin(angle * Math.PI / 180.0) * radius;

			String str = Integer.toString(i + 1);
			nodebtn[i] = new Button(str);
			nodebtn[i].setShape(new Circle(40));
			nodebtn[i].setMinSize(20, 20);
			nodebtn[i].setTranslateX(centerX + xProj);
			nodebtn[i].setTranslateY(centerY + yProj);

			//all nodes get colour one
			int current_color = assignedColors[i];
			nodebtn[i].setStyle(node_colors[current_color]);
			nodes_on_color[current_color]++;

			final int id = i;
			nodebtn[i].setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {

					//mode is 3, so look at allowed nodes
					if (mode == 3) {
						//if this node is allowed to be coloured (one of two allowed nodes)
						if (allowedNodes[id] == true) {
							//check if this node is the first or second node allowed to be coloured
							//for that we have to look at the position of this node in the ordering array
							//and compare to it the latestNodeColoured var
							int currentNodeInOrdering = 50;
							for (int j=0; j<ordering.length; j++) {
								if (ordering[j] == id) {
									currentNodeInOrdering = j;
									break;
								}
							}
							//First node in ordering was coloured, so the same two nodes stay allowed.
							if (currentNodeInOrdering == latestNodeColoured) {
							}
							//second node is being coloured
							else if (currentNodeInOrdering == latestNodeColoured + 1) {
								//check if the first node isn't violating the rules
								if (!checker.check(edges, assignedColors, ordering[latestNodeColoured])) {
									pane.getChildren().removeAll(mode3Warning);
									mode3Warning.setText("You Cannot Yet Colour Node " + (ordering[currentNodeInOrdering] + 1) + " Because Node "
									+ (ordering[latestNodeColoured] + 1) + " Is Violating The Rules!");
									pane.getChildren().addAll(mode3Warning);
									return;
								}
								//not violating, so we cant colour the node before anymore
								allowedNodes[ordering[latestNodeColoured]] = false;
								latestNodeColoured++;
								//now we can colour the next node
								allowedNodes[ordering[latestNodeColoured + 1]] = true;
								pane.getChildren().removeAll(mode3Warning);
							}
							else {
								System.out.println(currentNodeInOrdering);
							}
						}
						else {return;}
					}

					int current_color = assignedColors[id];
					//if using the last colour
					if (current_color == colors_counter) {
						//if only one node has this colour
						if (nodes_on_color[current_color] == 1) {
							//goes to first colour
							nodes_on_color[current_color]--;
							current_color = -1;
							//no node on last colour
							colors_counter--;
						}

						else {
							//else introduce new colour
							nodes_on_color[current_color]--;
							colors_counter++;
						}
					}
					else {
						nodes_on_color[current_color]--;
					}

					current_color++;
					nodes_on_color[current_color]++;
					assignedColors[id] = current_color;

					//set style
					nodebtn[id].setStyle(node_colors[current_color]);

				}
			});
		}


		line = new Line[edges.length];
		for (int i = 0; i < edges.length; i++) {
			int u = edges[i].u, v = edges[i].v;

			//create lines
			double x1 = nodebtn[u].getTranslateX(), y1 = nodebtn[u].getTranslateY(),
			       x2 = nodebtn[v].getTranslateX(), y2 = nodebtn[v].getTranslateY();

			line[i] = new Line(x1, y1, x2, y2);

			double deltax = (x2 - x1) / 2.0;
			double deltay = (y2 - y1) / 2.0;

			line[i].setTranslateX(nodebtn[u].getTranslateX() + deltax);
			line[i].setTranslateY(nodebtn[u].getTranslateY() + deltay);

			line[i].setStrokeWidth(3);
			line[i].setStyle("-fx-background-color: #ff0000; ");
			pane.getChildren().addAll(line[i]);

		}

		for (int i = 0; i < nodes; i++) {
			//add all nodes
			pane.getChildren().addAll(nodebtn[i]);
		}
	}

	//removes graph parts
	public void undraw() {
		if (nodebtn == null)
			return;

		colors_counter = 0;

		for (Button b : nodebtn)
			pane.getChildren().remove(b);

		for (Line l : line)
			pane.getChildren().remove(l);

		pane.getChildren().remove(r);
	}
}
