import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.Collections;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

public class Main extends Application {
	double pxUnit = 5;			// Multiplier to make the size of the plants reasonable on the screen.
	double bottomY = 1000;		// The distance of the ground from the top of the screen.

	// Keeps track of the max and min fitness for each generation.
	double maxFitness = 0;
	double minFitness = 0;
	
	int windowWidth = 300;
	int generations = 0;		// Keeps track of how many generations have passed.
	
	// ArrayLists to keep all of the plants and which positions on the ground are not being used.
	// There are specific locations on the ground in which plants start growing.
	ArrayList<Plant> plants = new ArrayList<Plant>();
	ArrayList<Double> plantPos = new ArrayList<Double>();
	
	Text t = new Text(100, 100, "");		// Text that will display information about generations and fitness to the screen.
	boolean stopped = true;		// Used to start and stop the simulation.
	
	@Override
	public void start(Stage primaryStage) {
		// Initialize the scene and root pane
		Pane root = new Pane();
		Scene scene = new Scene(root, windowWidth * pxUnit, 1000, Color.SKYBLUE);
		primaryStage.setScene(scene);
		primaryStage.show();
		
		// Start the simulation.
		initPlants(root);
		simulation(root);
	}
	
	// Start and run the simulation.
	public void simulation(Pane root) {
		final AnimationTimer timer = new AnimationTimer() {
			@Override
			public void handle(long now) {
				reproduce(root);
				drawAllPlants(root);
				generations++;
				// Write the generation and fitness info the the screen.
				t.setText("Generation Number: " + Integer.toString(generations) + "\nMax Fitness " + Double.toString(maxFitness) + "\nMin Fitness " + Double.toString(minFitness));
				root.getChildren().addAll(t);
				
				// Limit to the maximum generations if desired.
				if (generations == 1000000) {
					this.stop();
				}
				
				// Make a slight pause between generations if desired.
				try {
					Thread.sleep(0);
				}
				catch(InterruptedException e) {
				}
			}
		};
		
		// Check for mouse clicks and either pause or play the simulation.
		root.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (!stopped) {
					stopped = true;
					timer.stop();
				}
				else if (stopped) {
					stopped = false;
					timer.start();
				}
			}
        });
	}
	
	// Method the draw every plant to the screen
	public void drawAllPlants(Pane root) {
		// Clear the screen of old plants.
		root.getChildren().clear();
		
		// Draw every plant in the plants ArrayList.
		for (Plant plant : plants) {
			drawPlant(plant, root);
		}
	}
	
	// Draws a given plant to the root pane.
	public void drawPlant(Plant plant, Pane root) {
		// Values for the individual branches and trunks of each plant.
		double theta, length;
		Point startPos = plant.groundPos();
		Point endPos;
		
		// Go through every gene in the plant and draw it if it is valid.
		for (int i = 0; i < 20; i++) {
			// Check if the current gene is a trunk or a branch.
			// Add a leaf to the plant if it is a branch.
			int geneType = plant.geneType(i * 8);
			if (geneType != -1) {
				// Calculate the end point of the trunk
				theta = plant.branchAngle(i * 8);
				length = plant.branchLength(i * 8);
				theta += 90;
				theta *= (Math.PI / 180);
				endPos = new Point((length * Math.cos(theta)) + startPos.x, (length * Math.sin(theta)) + startPos.y);
				
				// Draw the line representing the trunk.
				Line line = new Line((startPos.x * pxUnit) + 150, -((startPos.y * pxUnit)) + bottomY, (endPos.x * pxUnit) + 150, -((endPos.y * pxUnit)) + bottomY);
				line.setStroke(Color.BROWN);
				line.setStrokeWidth(5);
				root.getChildren().addAll(line);
				
				// Check if the gene is a branch.
				if (geneType == 1) {
					// Add the leaf position to the plant.
					plant.addLeaf(endPos);
				}
				
				// Check if the gene is trunk.
				if (geneType == 0) {
					// Set the starting point of the next branch or trunk to the end point of this trunk.
					startPos.x = endPos.x; 
					startPos.y = endPos.y;
				}
			}
			// Get the leaf positions from the plant and draw them.
			// This is done after drawing the plant's skeleton so the leaves are drawn in front of the branches and trunks.
			ArrayList<Point> leaves = plant.getLeaves();
			for (Point leafToDraw : leaves) {
				Circle leaf = new Circle((leafToDraw.x * pxUnit) + 150, -((leafToDraw.y * pxUnit)) + bottomY, plant.LEAFSIZE * pxUnit);
				leaf.setFill(Color.GREEN);
				root.getChildren().addAll(leaf);
			}
		}
	}
	
	// Create the initial generation of plants with the basic genomes.
	public void initPlants(Pane root) {
		for (int i = 0; i < 20; i++) {
			Plant temp = new Plant((i + 1) * 10);
			drawPlant(temp, root);
			plants.add(temp);
		}
		plantPos.clear();
	}
	
	// Find the most fit plants and reproduce with them randomly.
	public void reproduce(Pane root) {
		calculateFitness(root);
		removeUnfit();
		
		// Temporary ArrayList of the most fit plants to avoid editing the original ArrayList while trying to reproduce.
		ArrayList<Plant> temp = new ArrayList<Plant>(plants);
		
		// Reproduce each plant with a random other plant.
		for (Plant p1 : temp) { 
			Plant p2 = null;
			int index2 = -1;
			
			// Choose a random other plant that is not p1.
			do {
				index2 = (int)(Math.random() * temp.size());
				p2 = temp.get(index2);
			} while(p2 != p1);
			
			// Choose a random position to grow the plant from.
			int pos = (int)(Math.random() * plantPos.size());
			
			// Create a new plant with parents p1 and p2.
			Plant child = new Plant(plantPos.get(pos), p1, p2);
			plantPos.remove(pos);
			plants.add(child);
		}
	}
	
	// Use a bunch of points to represent light streaming in from above.
	// These points of light will increase the fitness of a plant whose leaf it touches based on the light's current strength.
	public void calculateFitness(Pane root) {
		// Reset the fitness of every plant so that existing plants' fitnesses are calculated correctly 
		for (Plant plant : plants) {
			plant.setFitness(0);
		}
		
		// Initialize all the points of light.
		ArrayList<Light> lightPackets = new ArrayList<Light>();
		for (double i = 0; i <= 22 * 10; i += 1) {
			Point lightPos = new Point(i, 500);
			Light light = new Light(lightPos);
			lightPackets.add(light);
		}
		
		// Check each light to see if it hit a plant's leaf and then lower the point of light until it hits the ground.
		for (int i = 500; i > 0; i--) {
			for (Light light : lightPackets) {
				for (Plant plant : plants) {
					// Check if the point of the light is in a plant's leaf. (Does not account for being in 2 leaves at once.
					if (plant.pointInLeaf(light.pos)) {
						plant.addFitness(light.strength);		// Increase the plant's fitness based on the light's strength.
						light.strength /= 2;					// Halve the strength of the light so that further leaves absorb less light.
					}
				}
				light.pos.y --;						// Decrease the altitude of the point of light. 
				// Used to create a bias for taller or shorter plants if desired.
				//light.strength += 0.001;
			}
		}
		// Shuffle and sort the plants by fitness.
		// The shuffle is used to randomize the positions of plants with the same fitness score.
		Collections.shuffle(plants);
		Collections.sort(plants);
		
		maxFitness = plants.get(0).getFitness();
		minFitness = plants.get(plants.size() - 1).getFitness();
	}
	
	// Assume the plants ArrayList has been sorted.
	// Remove the half of the plants that are the least fit.
	public void removeUnfit() {
		int half = (plants.size() / 2);
		for (int i = plants.size() - 1; i >= half; i--) {
			// Add the position where the plants emerge from the ground to the ArrayList of open positions.
			plantPos.add(plants.get(half).groundPos().x);
			plants.remove(half);
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}