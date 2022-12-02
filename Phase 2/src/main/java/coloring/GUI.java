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
import javafx.application.Platform;
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

import static java.util.concurrent.TimeUnit.*;
import java.util.concurrent.*;
import java.util.List;
import java.util.ArrayList;

import java.util.Collections;

import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;


import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.control.Slider;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;


public class GUI extends Application {


	//parents for scenes
	public StackPane parentMain = new StackPane();
	public StackPane parentMode1 = new StackPane();
	public StackPane parentMode2 = new StackPane();
	public StackPane parentMode3 = new StackPane();
	public StackPane parentModeConfig = new StackPane();

	public Scene scene1, scene2, scene3, scene4, scene5, sceneg;

	public Button start, start2, start3, finishMode1, finishMode2, finishMode3, menu1, menu2, menu3, menuConfig;
	public Label solved = new Label("SOLVED!");
	public Label failed = new Label("FAILED!");
	public Label tooMany = new Label("You Are Using Too Many Colours!");
	public Label warning = new Label();
	public Label hint = new Label();

	//clock for timer
	private ScheduledFuture<?> fancyClock;
	public Text timerText;
	public int seconds;
	final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	public FileRead fileReader = new FileRead();

	public int mode = 0;
	public int cnt = 0;

	//two possible graphs, generated or read in
	public Graph graph = null;
	public Graph readGraph = null;
	public ColEdge[] edges = null;
	//choice of number of nodes & edges
	public int nodesChoice = 0;
	public int edgesChoice = 0;

	int size;
	int cn;

	//graph solvers
	public Backtrack solver = new Backtrack();
	public UpperBound upperSolver = new UpperBound();
	public int[] possibleSol;
	public BacktrackSolution backTrackSol;
	public int[] exactSol;


	public Checker checker = new Checker();
	public Hints hintCreator = new Hints();
	public Adjacent adj = null;

	//order for mode 3
	public int[] rdOrdering;

	public String hintMsg;

	//boxes for showing ordering in mode 3
	public Rectangle[] miniBox;
	public Text[] orderTxt;

	@Override
	public void start(Stage primaryStage) {

		primaryStage.setTitle("Chromatic Fun");
		primaryStage.setResizable(true);
		primaryStage.setMaximized(false);

		BufferedImage img = null;
		try {
		    img = ImageIO.read(new File("./res/Background.jpg"));
		} catch (IOException e) {
			System.out.println("failed to load the picture");
		}

		Image image = SwingFXUtils.toFXImage(img, null);

		solved.setTranslateY(-300);
		solved.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
		solved.setTextFill(Color.web("#00ff00"));

		tooMany.setTranslateY(-315);
		tooMany.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
		tooMany.setTextFill(Color.web("#ff0000"));

		warning.setTranslateY(-315);
		warning.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
		warning.setTextFill(Color.web("#ff0000"));

		hint.setTranslateY(-315);
		hint.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
		hint.setTextFill(Color.web("#f5f5dc"));

		failed.setTranslateY(-300);
		failed.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
		failed.setTextFill(Color.web("#ff0000"));

		Button ButtonH = new Button("Hint");
    	ButtonH.setTranslateY(200);
    	ButtonH.setTranslateX(430);

		//Hint Button Action
		ButtonH.setOnAction(e -> {
			int[] assignedColors = graph.assignedColors;
			//stores return of createHint method in String and sets text label to message
			if (mode == 1) {
				warning.setText("");
				hintMsg = hintCreator.createHint1(edges, assignedColors, cn, exactSol);
				hint.setText(hintMsg);
			}
			else if (mode == 2) {
				warning.setText("");
				hintMsg = hintCreator.createHint2(edges, assignedColors, possibleSol);
				hint.setText(hintMsg);
				parentMode2.getChildren().removeAll(hint);
				parentMode2.getChildren().addAll(hint);
			}
			else if (mode == 3) {
				warning.setText("");
				hintMsg = hintCreator.createHint3(edges, assignedColors, graph.latestNodeColoured, rdOrdering);
				hint.setText(hintMsg);
				parentMode3.getChildren().removeAll(hint);
				parentMode3.getChildren().addAll(hint);
			}
		});


		//-------------------------- SCENE 1 --------------------------------
		Label label = new Label("Welcome To The Chromatic Numbers Game");
		label.setTranslateY(-180);
		label.setFont(Font.font("Verdana", FontWeight.BOLD, 34));
		label.setTextFill(Color.web("#FFFF00"));

		Label label2 = new Label ("Please select a game mode");
		label2.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
		label2.setTextFill(Color.web("#000000"));
		label2.setTranslateY(-80);

		Button btn2 = new Button("To The Bitter End");
		btn2.setTranslateY(-40);

		Button btn3 = new Button("Best Upper Bound");
		btn3.setTranslateY(0);

		Button btn4 = new Button("Random Order");
		btn4.setTranslateY(40);

		Button btng = new Button("Graph Config");
		btng.setTranslateY(120);

		Label volume = new Label("Volume");
		volume.setFont(Font.font("Verdana", FontWeight.BOLD, 16));
		volume.setTranslateY(200);
		volume.setTranslateX(-500);

		// volume and background music
		String musicpath = "./res/backgroundmusic.mp3";
		Media media = new Media(new File(musicpath).toURI().toString());
		MediaPlayer mediaPlayer = new MediaPlayer(media);
		mediaPlayer.setAutoPlay(true);
		mediaPlayer.setVolume(0.6);

		//slider for setting volume
		Slider slider = new Slider();
		slider.setMaxWidth(200);
		slider.setTranslateY(220);
		slider.setTranslateX(-500);
		slider.setMin(0);
        slider.setMax(1);
		slider.adjustValue(0.6);
		slider.valueProperty().addListener(
             new ChangeListener<Number>() {

            public void changed(ObservableValue <? extends Number >
                      observable, Number oldValue, Number newValue)
            {

                mediaPlayer.setVolume(newValue.doubleValue());
            }
        });

		//Buttons for going to other scenes
		btn2.setOnAction(e -> { primaryStage.setScene(scene2); });
		btn3.setOnAction(e -> { primaryStage.setScene(scene3); });
		btn4.setOnAction(e -> { primaryStage.setScene(scene4); });
		btng.setOnAction(e -> { primaryStage.setScene(scene5); });


		ImageView mvMain = new ImageView(image);

		parentMain.getChildren().addAll(mvMain);
		parentMain.getChildren().addAll(label, btn2, label2, btn3, btn4, btng,volume,slider);

		scene1= new Scene(parentMain, 1920, 1080);


  	//-------------------------- SCENE 5 --------------------------------
		Label labelg= new Label("Graph Configuration");
		labelg.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
		labelg.setTranslateY(-350);
		labelg.setTextFill(Color.web("#f5f5dc"));

		// Input textfields + labels
		Label ta = new Label("Number of Nodes (Max. 25)");
		TextField a = new TextField();
		ta.setTranslateY(-230);
		ta.setTranslateX(-300);
		a.setTranslateY(-200);
		a.setTranslateX(-300);
		a.setMinWidth(50);
		a.setPrefWidth(50);
		a.setMaxWidth(100);

		Label tb = new Label("Number of Edges (Max. 25)");
		tb.setTranslateY(-130);
		tb.setTranslateX(-300);

		TextField b = new TextField();
		b.setTranslateX(-300);
		b.setTranslateY(-100);
		b.setMinWidth(50);
		b.setPrefWidth(50);
		b.setMaxWidth(100);

		Button submit = new Button("Submit");
		submit.setTranslateY(0);
		submit.setTranslateX(-300);

		Button choiceRd = new Button("Random Graph");
		choiceRd.setTranslateY(0);

		Label fileLabel = new Label("File Path");
		fileLabel.setTranslateY(-130);
		fileLabel.setTranslateX(300);

		TextField fileText = new TextField();
		fileText.setText("res\\exampleGraphs\\graph04_2020.txt");
		fileText.setTranslateX(300);
		fileText.setTranslateY(-100);
		fileText.setMinWidth(150);
		fileText.setPrefWidth(150);
		fileText.setMaxWidth(250);

		Button readSubmit = new Button("Read file");
		readSubmit.setTranslateY(0);
		readSubmit.setTranslateX(300);

		menuConfig = new Button("Main Menu");
		menuConfig.setTranslateY(200);
		menuConfig.setTranslateX(-300);
		menuConfig.setOnAction(e -> {
			primaryStage.setScene(scene1);
			a.clear();
			b.clear();
			if (graph != null) {
				graph.undraw();
			}
		});

		//Parses text fields to find nodes and edges choice
		submit.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				nodesChoice = Integer.parseInt("0" + a.getText());
	      		edgesChoice = Integer.parseInt("0" + b.getText());
				a.clear();
				b.clear();
				readGraph = null;
			}
    });

	//sets nodes and edges choice to 0 to ensure random graph
		choiceRd.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				nodesChoice = 0;
	      		edgesChoice = 0;
				readGraph = null;
			}
    });

	//parses text to find input file,
	//reads the data from that file with fileReader
	//sets readgraph to new graph generated with specified nodes and edges
		readSubmit.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				String inputFile = fileText.getText();
				ReadData readData = fileReader.read(inputFile);
				if (readData.edges != null) {
					readGraph = new Graph(readData.nodes, readData.edges);
				}
				fileText.clear();
			}
    });

		ImageView mvConfig = new ImageView(image);
		parentModeConfig.getChildren().addAll(mvConfig, labelg, menuConfig, a,b,ta,tb,submit, choiceRd, fileText, readSubmit, fileLabel);
		scene5 = new Scene(parentModeConfig,1920,1080);


		// ---------------------------------- Scene 2 ---------------------------
		StackPane parentMode1 = new StackPane();

		Label labelR= new Label("To The Bitter End");
		labelR.setTranslateY(-370);
		labelR.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
		labelR.setTextFill(Color.web("#f5f5dc"));

		Label Rules1 = new Label("Welcome to The Bitter End! \n You will be shown a random graph and your objective is to colour the graph as fast as possible. \n Click on a node to assign a colour to it. Each click on a node assigns it a new colour. \n You cannot finish the game until the minimum number of colours has been reached. \n There is no time limit for this game mode. \n Press Start to start playing. \n Good Luck!");
    Rules1.setFont(Font.font("Cambria", FontWeight.BOLD, 30));
		Rules1.setTextFill(Color.web("#000000"));


		//button for finishing game
		finishMode1 = new Button("Finish");
		finishMode1.setTranslateY(250);
		finishMode1.setTranslateX(-430);
		finishMode1.setOnAction(e -> {
			//stores current colouring
			int[] assignedColors = graph.assignedColors;
			int usedColors = graph.colors_counter + 1;

			//checks if valid
			adj = checker.check(edges, assignedColors);

			//if valid and # used colours is equal to chromatic num
			if (usedColors == cn & adj.solve == true) {
				solved.setText("You Solved The Puzzle!");
				parentMode1.getChildren().addAll(solved, start);
				parentMode1.getChildren().removeAll(finishMode1, tooMany, ButtonH, warning, hint);
				graph.undraw();

			}

			//not valid
			else if (adj.solve == false) {
				parentMode1.getChildren().removeAll(warning);
				hint.setText("");
				warning.setText("Node " + adj.u + " And Node " + adj.v + " Are Adjacent And Have The Same Colour!");
				parentMode1.getChildren().addAll(warning);
			}

			//too many colours
			else if (usedColors > cn) {
				hint.setText("");
				if (!parentMode1.getChildren().contains(tooMany))
					parentMode1.getChildren().addAll(tooMany);
			}
		});

		//button for starting game
		start = new Button("Start");
		start.setTranslateY(250);
		start.setTranslateX(-430);
		start.setOnAction(e -> {
			parentMode1.getChildren().removeAll(start, solved, tooMany);
			Rules1.setText("");
			parentMode1.getChildren().addAll(finishMode1, ButtonH);
			hint.setText("");
			if (!parentMode1.getChildren().contains(hint)) {
				parentMode1.getChildren().addAll(hint);
			}
			mode = 1;
			//removes graph if there is one
			if (graph != null)
				graph.undraw();

			//spawns new graph
			spawnGraph();

			//Builds graphics for graph
			graph.pane = parentMode1;
			graph.buildGraphics();
			//solves graph with backtrack method
			backTrackSol = solver.solve(edges, size);
			cn = backTrackSol.cn;
			exactSol = backTrackSol.correctColours;

		});

		ImageView mvMode1 = new ImageView(image);

		menu1 = new Button("Main Menu");
		menu1.setTranslateY(200);
		menu1.setTranslateX(-430);
		menu1.setOnAction(e -> {
			parentMode1.getChildren().removeAll(finishMode1, solved, tooMany, ButtonH, warning, hint);
			if (!parentMode1.getChildren().contains(start))
				parentMode1.getChildren().addAll(start);
			primaryStage.setScene(scene1);
			if (graph != null) {
				graph.undraw();
			}
		});

		hint.setText("");

		parentMode1.getChildren().addAll(mvMode1);
		parentMode1.getChildren().addAll(labelR, menu1, start, hint, Rules1);
		scene2 = new Scene(parentMode1,1920,1080);


		// -------------------------------- Scene 3 ------------------------
		Label labelB = new Label("Best Upper Bound");
		labelB.setTranslateY(-370);
		labelB.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
		labelB.setTextFill(Color.web("#f5f5dc"));

		Label Rules2 = new Label("Welcome to The Best Upper Bound! \n In this TIMED game mode, you have to colour the given graph using as few colours as possible in the given time limit. \n Click on a node to assign a colour to it. Each click on a node assigns it a new colour. \n Remember, you are always allowed to introduce a new colour. \n Press Start to start playing. Good Luck!");
	 	Rules2.setFont(Font.font("Cambria", FontWeight.BOLD, 30));
		Rules2.setTextFill(Color.web("#000000"));

		//button for finishing game
		finishMode2 = new Button("Finish");
		finishMode2.setTranslateY(250);
		finishMode2.setTranslateX(-430);
		finishMode2.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				//current colouring
				int[] assignedColors = graph.assignedColors;
				int usedColors = graph.colors_counter + 1;

				//check if valid
				adj = checker.check(edges, assignedColors);

				//if valid
				if (adj.solve == true) {
					solved.setText("You Solved The Puzzle Using " + usedColors + " Colours!");
					parentMode2.getChildren().addAll(solved, start2);
					fancyClock.cancel(true);
					parentMode2.getChildren().removeAll(finishMode2, warning, ButtonH, hint);
					graph.undraw();
				}

				//warning message if not valid
				else {
					hint.setText("");
					parentMode2.getChildren().remove(warning);
					warning.setText("Node " + adj.u + " And Node " + adj.v + " Are Adjacent And Have The Same Colour!");
					parentMode2.getChildren().addAll(warning);
				}
			}
    });

		start2 = new Button("Start");
		start2.setTranslateY(250);
		start2.setTranslateX(-430);
		start2.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				mode = 2;
				Rules2.setText("");
				parentMode2.getChildren().removeAll(start2, solved, failed, timerText);
				parentMode2.getChildren().addAll(finishMode2, ButtonH);
				if (graph != null)
					graph.undraw();

					//spawns graph and build graphics
				spawnGraph();
				graph.pane = parentMode2;
				graph.buildGraphics();

				//finds possible solution used for hints using greedy colouring
				possibleSol = upperSolver.solve(edges, size);

				//Time given to solve graph is a function of # nodes and edges
				double doubleSeconds = 30.0 + (0.2 * size) + (0.8 * (edges.length * 7));
 				seconds = (int) doubleSeconds;

				int mn = seconds / 60;
				int ss = seconds % 60;

				String currentTime = String.format("%d:%02d", mn, ss);
				timerText = new Text();
				timerText.setText(currentTime);

				//sets the timer text
				timerText.setFill(Color.WHITE);
				timerText.setFont(Font.font("Helvetica", FontWeight.BOLD, 100));
				timerText.setTranslateX(-430);
				timerText.setTranslateY(0);
				parentMode2.getChildren().add(timerText);

				//scheduler runs every second
				fancyClock = scheduler.scheduleAtFixedRate(new Runnable() {
					@Override
					public void run() {
						try {
							//if time is up
							if (seconds == 0) {
								Platform.runLater(new Runnable() {
									@Override
									public void run() {
										//display failed message
										parentMode2.getChildren().addAll(failed, start2);
										parentMode2.getChildren().removeAll(finishMode2, warning, ButtonH, hint);
										graph.undraw();
									}
								});
								fancyClock.cancel(true);
							}

							//sets new time
							int mn = seconds / 60;
							int ss = seconds % 60;

							String currentTime = String.format("%d:%02d", mn, ss);
							timerText.setText(currentTime);
							//one second off
							seconds--;
						} catch (Exception e) {
							e.printStackTrace();
							System.exit(0);
						}
					}
				}, 0, 1, SECONDS);
			}
    });

		parentMode2 = new StackPane();
		ImageView mv2 = new ImageView(image);

		menu2 = new Button("Main Menu");
		menu2.setTranslateY(200);
		menu2.setTranslateX(-430);
		menu2.setOnAction(e -> {
			//remove clock
			if (fancyClock != null)
				fancyClock.cancel(true);
			parentMode2.getChildren().removeAll(finishMode2, timerText, solved, warning, ButtonH, hint);
			if (!parentMode2.getChildren().contains(start2))
				parentMode2.getChildren().addAll(start2);
			primaryStage.setScene(scene1);
			if (graph != null) {
				graph.undraw();
			}
		});

		parentMode2.getChildren().addAll(mv2);
		parentMode2.getChildren().addAll(labelB, menu2, start2, Rules2);
		scene3 = new Scene(parentMode2,1920,1080);


		// --------------------------- scene 4 ---------------------------
		Label labelRO = new Label("Random Order");
		labelRO.setTranslateY(-370);
		labelRO.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
		labelRO.setTextFill(Color.web("#f5f5dc"));

		Label Rules3 = new Label("Welcome to Random Order! \n In this game mode, you will be shown a random ordering of the nodes. You have to colour the graph in that specific order. \n Click on each node to assign a colour to it. Each click on a node assigns a new colour to it. \n Remember, you can never get stuck as you are always allowed to introduce a new colour. \n Your goal is to use as few colours as possible. \n Press 'Start' to start playing. Good Luck!");
    Rules3.setFont(Font.font("Cambria",FontWeight.BOLD, 30));
		Rules3.setTextFill(Color.web("#000000"));

		finishMode3 = new Button("Finish");
		finishMode3.setTranslateY(250);
		finishMode3.setTranslateX(-460);
		finishMode3.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				int[] assignedColors = graph.assignedColors;
				int usedColors = graph.colors_counter + 1;

				adj = checker.check(edges, assignedColors);

				//if valid colouring
				if (adj.solve == true) {
					solved.setText("You Solved The Puzzle Using " + usedColors + " Colours!");
					parentMode3.getChildren().addAll(solved, start3);
					parentMode3.getChildren().removeAll(finishMode3, warning, graph.mode3Warning, ButtonH, hint);
					graph.undraw();
					for (int i=0; i<graph.nodes; i++) {
						parentMode3.getChildren().removeAll(orderTxt[i], miniBox[i]);
					}
				}

				//warning message if not valid
				else {
					hint.setText("");
					parentMode3.getChildren().remove(warning);
					warning.setText("Node " + adj.u + " And Node " + adj.v + " Are Adjacent And Have The Same Colour!");
					parentMode3.getChildren().addAll(warning);
				}
			}
    });

		start3 = new Button("Start");
		start3.setTranslateY(250);
		start3.setTranslateX(-460);
		start3.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				mode = 3;
				cnt = 0;
				Rules3.setText(" ");
				parentMode3.getChildren().removeAll(start3, solved, failed);
				parentMode3.getChildren().addAll(finishMode3, ButtonH);

				//draws graph
				if (graph != null)
					graph.undraw();

				spawnGraph();
				graph.pane = parentMode3;

				//create array of integers to be shuffled
				rdOrdering = new int[graph.nodes];
				for (int k=0; k<graph.nodes; k++) {
					rdOrdering[k] = k;
				}
				//shuffles array to get random order
				shuffle(rdOrdering);
				graph.ordering = rdOrdering;
				graph.mode = 3;

				graph.buildGraphics();

				//Build the board to see the ordering
				miniBox = new Rectangle[graph.nodes];
				orderTxt = new Text[graph.nodes];

				//set text boxes for ordering of nodes
				//size and position dependent on # of nodes
				double deltax = 540.0 / graph.nodes;
				for (int i = 0; i < graph.nodes; i++) {
					miniBox[i] = new Rectangle(50, deltax);
					miniBox[i].setTranslateX(-330);
					miniBox[i].setTranslateY(-290 + (deltax / 2) + deltax * i);
					miniBox[i].setStroke(Color.BLACK);
					miniBox[i].setFill(Color.BLUE);
					parentMode3.getChildren().addAll(miniBox[i]);


					orderTxt[i] = new Text(String.format("%d", (rdOrdering[i] + 1)));
					orderTxt[i].setFill(Color.WHITE);
					orderTxt[i].setFont(Font.font("Helvetica", FontWeight.BOLD, 20));
					orderTxt[i].setTranslateX(-330);
					orderTxt[i].setTranslateY(-290 + (deltax / 2) + deltax * i);
					parentMode3.getChildren().add(orderTxt[i]);
				}
			}
    });

		ImageView mv3 = new ImageView(image);

		menu3 = new Button("Main Menu");
		menu3.setTranslateY(200);
		menu3.setTranslateX(-460);
		menu3.setOnAction(e -> {
			parentMode3.getChildren().removeAll(finishMode3, solved, warning, ButtonH, hint);
			if (graph != null && graph.mode3Warning != null) {
				parentMode3.getChildren().removeAll(graph.mode3Warning);
			}
			if (!parentMode3.getChildren().contains(start3))
				parentMode3.getChildren().addAll(start3);
			primaryStage.setScene(scene1);
			if (graph != null) {
				graph.undraw();
			}
			for (int i=0; i<graph.nodes-1; i++) {
				parentMode3.getChildren().removeAll(orderTxt[i], miniBox[i]);
			}
		});

		parentMode3.getChildren().addAll(mv3, labelRO, menu3, start3, Rules3);
		scene4 = new Scene(parentMode3,1920,1080);



		primaryStage.setScene(scene1);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * Method for spawning graph
	 * If there is a read-in graph, use that one
	 * Else, create a new one using the choice for # of nodes and edges
	 */
	public void spawnGraph() {
		if (readGraph == null) {
			CreateGraph newRdGraph = new CreateGraph(nodesChoice, edgesChoice);
			edges = newRdGraph.graph;
			size = newRdGraph.size;
			graph = new Graph(size, edges);
		}

		else {
			graph = readGraph;
		}
	}

	/**
     	* Modified code from method java.util.Collections.shuffle();
		 * Used for mode 3 to create a random ordering.
     */
    public void shuffle(int[] array) {
        Random random = new Random();
        int count = array.length;
        for (int i = count; i > 1; i--) {
            swap(array, i - 1, random.nextInt(i));
        }
    }

	/**
	 * Helper method for shuffle
	 * @param array array being shuffled
	 * @param i eelement
	 * @param j 2nd element
	 */
    private void swap(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

}
