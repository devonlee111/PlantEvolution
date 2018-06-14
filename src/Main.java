import javafx.stage.Stage;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;
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
	
	// ArrayLists to keep all of the plants, which positions on the ground are not being used, and to group plants into their respective species.
	private List<Plant> plants = new ArrayList<>();
	private List<Leaf> leaves = new ArrayList<>();
	private List<Double> plantPos = new ArrayList<>();
	private ArrayList<ArrayList<Plant>> species = new ArrayList<ArrayList<Plant>>();
	
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
		numSpecies = species.size();
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
					numSpecies = species.size();
					
					// Limit to the maximum generations if desired.
					if (generations == maxGenerations) {
						stopped = true;
					}
					
					// Make a slight pause between generations if desired.
					try {
						Thread.sleep(0);
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
		plant.setNumLeaves(0);
		
		// Values for the individual branches and trunks of each plant.
		double theta, length;
		Point startPos = plant.groundPos();
		Point endPos;
		int numLeaves = 0;
		// Go through every gene in the plant and draw it if it is valid.
		for (int i = 0; i < 20; i++) {
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
					plant.addLeafCount();
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
		for (int i = 0; i < 200; i++) {
			Plant temp = new Plant((i + 2) * 20);
			plants.add(temp);
		}
		drawAllPlants(root);
		plantPos.clear();
	}

	// Remove the unfit plants and use the remaining plants to re-populate the next generation.
	// Plants will only reproduce with plants of the same species or it will clone itself.
	public void speciesReproduce(Pane root) {
		calculateFitness();
		removeUnfit();
		speciesSeperation();
		
		// For each different species check how many plants belong to it.
		// Clone the plant if there is only one plant.
		// Randomly select a partner to reproduce with every plant that belongs to a species with more than one plant.
		for (ArrayList<Plant> currentSpecies : species) {
			if (currentSpecies.size() == 1) {
				// Choose a random position to grow the plant from.
				int pos = (int)(Math.random() * plantPos.size());
				
				// Create a new plant with parents p1 and p2.
				Plant child = new Plant(plantPos.get(pos), currentSpecies.get(0));
				plantPos.remove(pos);
				plants.add(child);
			}
			else {
				for (Plant p1 : currentSpecies) {
					Plant p2 = null;
					int index2 = -1;
					
					// Choose a random other plant that is not p1.
					do {
						index2 = (int)(Math.random() * currentSpecies.size());
						p2 = currentSpecies.get(index2);
					} while(p2 != p1);
					
					// Choose a random position to grow the plant from.
					int pos = (int)(Math.random() * plantPos.size());
					
					// Create a new plant with parents p1 and p2.
					Plant child = new Plant(plantPos.get(pos), p1, p2);
					plantPos.remove(pos);
					plants.add(child);
				}
			}
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
		Collections.sort(leaves);
		double prevHeight = -1;
		List<Leaf> treeAdditions = new ArrayList<>();
		for (Leaf leaf : leaves) {
			if (prevHeight != leaf.getPos().getY()) {
				prevHeight = leaf.getPos().getY();
				for (Leaf addLeaf : treeAdditions) {
					Interval addInterval = new Interval(addLeaf.getPos().getX() - addLeaf.SIZE, addLeaf.getPos().getX() + addLeaf.SIZE);
					leafIntervals.root = leafIntervals.insert(leafIntervals.root, addInterval);
				}
				treeAdditions.clear();
			}
			treeAdditions.add(leaf);
			Interval interval = new Interval(leaf.getPos().getX() - leaf.SIZE, leaf.getPos().getX() + leaf.SIZE);
			int numLeavesAbove = leafIntervals.numOverlaps(leafIntervals.root, interval);
			leaf.getPlant().addFitness(Math.max(0, (40  - (10 * numLeavesAbove) + (.01 * leaf.getPos().getY()))));
		}
		
		// Shuffle and sort the plants by fitness.
		// The shuffle is used to randomize the positions of plants with the same fitness score.
		Collections.shuffle(plants);
		Collections.sort(plants);
		
		// Set the weights of the plants for weighted removal.
		for (Plant plant : plants) { 
			plant.setWeight((int)(Math.pow(plants.indexOf(plant), 2)));
		}
		
		maxFitness = plants.get(0).getFitness();
		minFitness = plants.get(plants.size() - 1).getFitness();
	}
	
	/* Assume the plants ArrayList has been sorted.
	 * Remove any and all plants with a fitness <= 0.
	 * Remove the half of the plants based on random weighted selection.
	 * Plants with a lower fitness are given higher weights to be removed.
	*/
	public void removeUnfit() {
		int half = (plants.size() / 2);
		int numRemoved = 0;
		ArrayList<Plant> dead = new ArrayList<Plant>();		// ArrayList of plants deemed dead so they can be removed.
		
		// Check every plant to see if it has a fitness <= 0.
		for (Plant plant : plants) {
			if (plant.getFitness() <= 0) {
				numRemoved++;
				plantPos.add(plant.groundPos().getX());
				dead.add(plant);
			}
		}
		
		// Remove all dead plants.
		for (Plant plant : dead) {
			plants.remove(plant);
		}
		
		// Use weighted random selection to remove the remaining plants that need to be removed.
		for (numRemoved = numRemoved; numRemoved < half; numRemoved++) {
			// Add the position where the plants emerge from the ground to the ArrayList of open positions.
			Plant toRemove = chooseWeightedPlant();
			plantPos.add(toRemove.groundPos().getX());
			plants.remove(toRemove);
		}
	}
	
	// Algorithm for randomly selecting a plant based on its weight.
	public Plant chooseWeightedPlant() {
		int totalWeight = 0;
		for (Plant plant : plants) {
			totalWeight += plant.weight();
		}
		//System.out.println(totalWeight + "\n");
		int random = (int)(Math.random() * totalWeight) + 1;
		//System.out.println(random);
		for (Plant plant : plants) {
			random -= plant.weight();
			//System.out.println(random + " : " + plant.weight());
			if (random <= 0) {
				return plant;
			}
		}
		return null;
	}
	
	// Separate the species into their own ArrayLists
	public void speciesSeperation() {
		species.clear();
		ArrayList<Plant> temp = new ArrayList<Plant>(plants);
		/* For every plant, check if there is an existing species that it belongs to.
		 * If there isn't an existing species create a new ArrayList of that species.
		 * The first plant in a species is the base plant for which other plants are compared to determine if they are the same species.
		*/
		for (Plant plant : temp) {
			boolean speciesExists = false;
			for (ArrayList<Plant> existingSpecies : species) {
				if (existingSpecies.get(0).sameSpecies(plant)) {
					existingSpecies.add(plant);
					speciesExists = true;
					break;
				}
			}
			if (!speciesExists) {
				ArrayList<Plant> newSpecies = new ArrayList<Plant>();
				newSpecies.add(plant);
				species.add(newSpecies);
			}
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}