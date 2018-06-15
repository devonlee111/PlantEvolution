import javafx.stage.Stage;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

public class Main extends Application {
	private double pxUnit = 3;			// Multiplier to make the size of the plants reasonable on the screen.
	private double bottomY;		// The distance of the ground from the top of the screen.
	private int xOffset = 150;
	private int yOffset = 0;

	// Keeps track of the max and min fitness for each generation.
	private double maxFitness = 0;
	private double minFitness = 0;
	private int windowHeight;
	private int windowWidth;
	private int generations = 0;		// Number of generations that have passed.
	private int maxGenerations = -1;	// Maximum number of generations until the simulation pauses.
	private int numSpecies = 0;			// The number of species in each generation.
	private int maxLeaves = 0;			// The maximum number of leaves on a single plant in each generation.
	private int numPlants = 100;
	
	// ArrayLists to keep all of the plants, which positions on the ground are not being used, and to group plants into their respective species.
	private Plant[] plants = new Plant[numPlants];
	private Plant[] fitSortPlants = new Plant[numPlants];
	private List<Leaf> leaves = new ArrayList<>();
	
	private Text t = new Text(100, 100, "");		// Text that will display information about generations and fitness to the screen.
	boolean stopped = true;					// Used to start and stop the simulation.
	
	private Pane root;
	private Scene scene;
	
	@Override
	public void start(Stage primaryStage) {
		Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
		windowHeight = (int)(screensize.height * .9);
		windowWidth = (int)(screensize.width * .9);
		bottomY = windowHeight;
		// Initialize the scene and root pane
		root = new Pane();
		scene = new Scene(root, windowWidth, windowHeight, Color.SKYBLUE);
		primaryStage.setScene(scene);
		primaryStage.show();
		
		// Start the simulation and do initial calculations to write onto the screen.
		initPlants(root);
		calculateFitness();
		speciesSeperation();
		t.setText("Generation Number: " + Integer.toString(generations) + "\nMax Leaves: " + Integer.toString(maxLeaves) + "\nMax Fitness " + Double.toString(maxFitness) + "\nMin Fitness " + Double.toString(minFitness) + "\nNum Species " + Integer.toString(numSpecies));
		root.getChildren().addAll(t);
		simulation(root);
	}
	
	// Start and run the simulation.
	public void simulation(Pane root) {
		final AnimationTimer timer = new AnimationTimer() {
			@Override
			public void handle(long now) {
				drawAllPlants(root);
				// Write the generation and fitness info the the screen.
				t.setText("Generation Number: " + Integer.toString(generations) + "\nMax Leaves: " + Integer.toString(maxLeaves) + "\nMax Fitness " + Double.toString(maxFitness) + "\nMin Fitness " + Double.toString(minFitness) + "\nNum Species " + Integer.toString(numSpecies));
				root.getChildren().addAll(t);
				if (!stopped) {
					speciesReproduce(root);
					generations++;
					
					// Limit to the maximum generations if desired.
					if (generations == maxGenerations) {
						stopped = true;
					}
					
					// Make a slight pause between generations if desired.
					try {
						Thread.sleep(000);
					}
					catch(InterruptedException e) {
					}
				}
			}
		};
		
		// Check for mouse clicks and either pause or play the simulation.
		scene.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (!stopped) {
					stopped = true;
				}
				else if (stopped) {
					stopped = false;
				}
			}
        });
		
		scene.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				String key = event.getText();
				switch(key) {
					case "=":
						pxUnit += .25;
						break;
						
					case "-":
						pxUnit -= .25;
						break;
						
					case "w":
						yOffset += 5;
						break;
					case "s":
						yOffset -= 5;
						break;
						
					case "a":
						xOffset += 5;
						break;
						
					case "d":
						xOffset -= 5;
						break;
				}
			}	
		});
		timer.start();
	}
	
	// Method the draw every plant to the screen
	public void drawAllPlants(Pane root) {
		// Clear the screen of old plants.
		root.getChildren().clear();
		leaves.clear();
		
		// Draw every plant in the plants ArrayList.
		for (Plant plant : plants) {
			drawPlant(plant, root);
		}
		
		for (Leaf leaf : leaves) {
			Circle c = new Circle((leaf.getPos().getX() * pxUnit) + xOffset, -((leaf.getPos().getY() * pxUnit)) + bottomY + yOffset, leaf.SIZE * pxUnit);
			c.setFill(Color.GREEN);
			root.getChildren().addAll(c);
		}
	}
	
	// Draws a given plant to the root pane.
	public void drawPlant(Plant plant, Pane root) {
		
		// Values for the individual branches and trunks of each plant.
		double theta, length;
		Point startPos = new Point(plant.groundPos(), 0);
		Point endPos;
		int numLeaves = 0;
		// Go through every gene in the plant and draw it if it is valid.
		for (int i = 0; i < 1000; i++) {
			// Check if the current gene is a trunk or a branch.
			// Add a leaf to the plant if it is a branch.
			int geneType = plant.geneType(i * 	8);
			if (geneType != -1) {
				// Calculate the end point of the trunk
				theta = plant.branchAngle(i * 8);
				length = plant.branchLength(i * 8);
				theta += 90;
				theta *= (Math.PI / 180);
				endPos = new Point((length * Math.cos(theta)) + startPos.getX(), (length * Math.sin(theta)) + startPos.getY());
				
				// Draw the line representing the trunk.
				Line line = new Line((startPos.getX() * pxUnit) + xOffset, -((startPos.getY() * pxUnit)) + bottomY + yOffset, (endPos.getX() * pxUnit) + xOffset, -((endPos.getY() * pxUnit)) + bottomY + yOffset);
				line.setStroke(Color.BROWN);
				line.setStrokeWidth(1 * pxUnit);
				root.getChildren().addAll(line);
				
				// Check if the gene is a branch.
				if (geneType == 1) {					
					// Add the leaf position to the plant.
					Leaf leaf = new Leaf(plant, endPos);
					leaves.add(leaf);
					numLeaves++;
				}
				
				// Check if the gene is trunk.
				if (geneType == 0) {
					// Set the starting point of the next branch or trunk to the end point of this trunk.
					startPos.setX(endPos.getX()); 
					startPos.setY(endPos.getY());
				}
			}
		}
		if (numLeaves > maxLeaves) {
			maxLeaves = numLeaves;
		}
	}
	
	// Create the initial generation of plants with the basic genome.
	public void initPlants(Pane root) {
		for (int i = 0; i < plants.length; i++) {
			Plant temp = new Plant((i + 2) * 20);
			plants[i] = temp;
			temp.index = i;
		}
		drawAllPlants(root);
	}

	// Remove the unfit plants and use the remaining plants to re-populate the next generation.
	// Plants will only reproduce with plants of the same species or it will clone itself.
	public void speciesReproduce(Pane root) {
		calculateFitness();
		removeUnfit();
		//speciesSeperation();
		
		int offspringRadius = 4;
		boolean done = false;
				
		while(!done) {
			// For each different species check how many plants belong to it.
			// Clone the plant if there is only one plant.
			// Randomly select a partner to reproduce with every plant that belongs to a species with more than one plant.
			for (int i = 0; i < fitSortPlants.length; i++) {
				if (fitSortPlants[i].isDead()) {
					continue;
				}
				Plant p1 = fitSortPlants[i];
				Plant p2 = null;
				int pos = -1;
				
				// Find a plant to reproduce with, within a certain range.
				for (int j = 1; j < 3; j++) {
					if (p1.index - j >= 0 && !plants[p1.index - j].isDead()) {
						p2 = plants[p1.index - j];
						break;
					}
					if (p1.index + j < plants.length && !plants[p1.index + j].isDead()) {
						p2 = plants[p1.index + j];
						break;
					}
				}
				
				// Find location for offspring
				int j = 1;
				while(j < offspringRadius) {
					if (p1.index - j >= 0 && plants[p1.index - j].isDead()) {
						pos = p1.index - j;
						break;
					}
					if (p1.index + j < plants.length && plants[p1.index + j].isDead()) {
						pos = p1.index + j;
						break;
					}
					if (p1.index - j < 0 && p1.index + j >= plants.length) {
						break;
					}
					j++;
				}
				
				if (pos == -1) {
					if (offspringRadius > plants.length / 2) {
						done = true;
					}
					break;
				}
				if (p2 == null) {
					plants[pos] = new Plant(plants[pos].groundPos(), p1);
				}
				else {
					plants[pos] = new Plant(plants[pos].groundPos(), p1, p2);
				}
				plants[pos].index = pos;
			}
			offspringRadius += 2;
		}
	}
	
	// Use a bunch of points to represent light streaming in from above.
	// These points of light will increase the fitness of a plant whose leaf it touches based on the light's current strength.
	public void calculateFitness() {
		// Reset the fitness of every plant so that existing plants' fitnesses are calculated correctly 
		for (Plant plant : plants) {
			plant.resetFitness();
		}
		
		IntervalTree leafIntervals = new IntervalTree();
		Collections.shuffle(leaves);
		Collections.sort(leaves);
		//double prevHeight = -1;
		//List<Leaf> treeAdditions = new ArrayList<>();
		for (Leaf leaf : leaves) {
			/*if (prevHeight != leaf.getPos().getY()) {
				prevHeight = leaf.getPos().getY();
				for (Leaf addLeaf : treeAdditions) {
					Interval addInterval = new Interval(addLeaf.getPos().getX() - addLeaf.SIZE, addLeaf.getPos().getX() + addLeaf.SIZE);
					leafIntervals.root = leafIntervals.insert(leafIntervals.root, addInterval);
				}
				treeAdditions.clear();
			}*/
			//treeAdditions.add(leaf);
			Interval interval = new Interval(leaf.getPos().getX() - leaf.SIZE, leaf.getPos().getX() + leaf.SIZE);
			int numLeavesAbove = leafIntervals.numOverlaps(leafIntervals.root, interval);
			leaf.getPlant().addFitness(Math.max(0, (40  - (10 * numLeavesAbove)+ (.1 * leaf.getPos().getY()))));
			leafIntervals.root = leafIntervals.insert(leafIntervals.root, interval);
		}
		
		// Shuffle and sort the plants by fitness.
		// The shuffle is used to randomize the positions of plants with the same fitness score.
		for (int i = 0; i < plants.length; i++) {
			fitSortPlants[i] = plants[i];
		}
		shufflePlants(fitSortPlants);
		Arrays.sort(fitSortPlants);
		
		// Set the weights of the plants for weighted removal.
		for (int i = 0; i < fitSortPlants.length; i++) { 
			fitSortPlants[i].setWeight(i * .1);
		}
		
		maxFitness = fitSortPlants[0].getFitness();
		minFitness = fitSortPlants[fitSortPlants.length - 1].getFitness();
	}
	
	/* Assume the plants ArrayList has been sorted.
	 * Remove any and all plants with a fitness <= 0.
	 * Remove the half of the plants based on random weighted selection.
	 * Plants with a lower fitness are given higher weights to be removed.
	*/
	public void removeUnfit() {
		int half = (int)(plants.length * .1);
		int numRemoved = 0;
		// Check every plant to see if it has a fitness <= 0.
		for (int i = 0; i < fitSortPlants.length; i++) {
			if (fitSortPlants[i].getFitness() <= 0) {
				numRemoved++;
				fitSortPlants[i].kill();
			}
		}
		// Use weighted random selection to remove the remaining plants that need to be removed.
		for (int removed = numRemoved; removed < half; removed++) {
			// Add the position where the plants emerge from the ground to the ArrayList of open positions.
			Plant toRemove = chooseWeightedPlant();
			toRemove.kill();
		}
	}
	
	// Algorithm for randomly selecting a plant based on its weight.
	public Plant chooseWeightedPlant() {
		int totalWeight = 0;
		for (Plant plant : fitSortPlants) {
			if (!plant.isDead()) {
				totalWeight += plant.weight();
			}
		}
		int random = (int)(Math.random() * totalWeight) + 1;
		for (int i = 0; i < fitSortPlants.length; i++) {
			if(!fitSortPlants[i].isDead()) {
				random -= fitSortPlants[i].weight();
				if (random <= 0) {
					return fitSortPlants[i];
				}
			}
		}
		return null;
	}
	
	// Separate the species into their own ArrayLists
	public void speciesSeperation() {
		
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public void shufflePlants(Plant[] plants) {
		for(int i = 0; i < plants.length; i++) {
			int swap = (int)(Math.random() * (i + 1));
			Plant temp = plants[i];
			plants[i] = plants[swap];
			plants[swap] = temp;
		}
	}
}