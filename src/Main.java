import javafx.stage.Stage;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class Main extends Application {
	double pxUnit = 5;
	double centerY = 1000;
	int windowWidth = 210;
	ArrayList<Plant> plants = new ArrayList<Plant>();
	ArrayList<Double> plantPos = new ArrayList<Double>();
	
	@Override
	public void start(Stage primaryStage) {
		Pane root = new Pane();
		Scene scene = new Scene(root, windowWidth * pxUnit, 1000, Color.SKYBLUE);
		
		root.addEventHandler(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (event.getClickCount() == 1) {
                        	reproduce(root);
                        	drawAllPlants(root);
                        }
                    }
                });
		
		primaryStage.setScene(scene);
		primaryStage.show();
		initPlants(root);
		
		//simulation(root);
		/*
		int validGenes = 0;
		for (int i = 0; i < 20; i++) {
			Plant temp = new Plant((i + 1) * 10);
			drawPlant(temp, root);
			plants[i] = temp;
		}
		
		calculateFitness();
		Arrays.sort(plants);
		for (int i = 0; i < 20; i++) {
			System.out.println(plants[i].getFitness());
		}
		Plant child = new Plant(10);
		Circle test = new Circle ((windowWidth/2) * pxUnit, 0, 1);
		root.getChildren().addAll(test);
		*/
	}
	
	public void simulation(Pane root) {
		final AnimationTimer timer = new AnimationTimer() {
			int i = 0;
			@Override
			public void handle(long now) {
				drawAllPlants(root);
				reproduce(root);
			}
		};
		timer.start();
	}
	
	
	public void drawAllPlants(Pane root) {
		root.getChildren().clear();
		for (int i = 0; i < 20; i++) {
			drawPlant(plants.get(i), root);
		}
	}
	
	public void drawPlant(Plant plant, Pane root) {
		double theta, length;
		Point startPos = plant.groundPos();
		Point endPos;
		for (int i = 0; i < 20; i++) {
			int geneType = plant.geneType(i * 8);
			if (geneType == 0) {
				theta = plant.branchAngle(i * 8);
				length = plant.branchLength(i * 8);
				theta += 90;
				theta *= (Math.PI / 180);
				endPos = new Point((length * Math.cos(theta)) + startPos.x, (length * Math.sin(theta)) + startPos.y);
				Line line = new Line((startPos.x * pxUnit), -((startPos.y * pxUnit)) + centerY, (endPos.x * pxUnit), -((endPos.y * pxUnit)) + centerY);
				line.setStroke(Color.BROWN);
				line.setStrokeWidth(5);
				root.getChildren().addAll(line);
				startPos.x = endPos.x; 
				startPos.y = endPos.y;
			}
			else if (geneType == 1) {
				theta = plant.branchAngle(i * 8);
				length = plant.branchLength(i * 8);
				theta += 90;
				theta *= (Math.PI / 180);
				endPos = new Point((length * Math.cos(theta)) + startPos.x, (length * Math.sin(theta)) + startPos.y);
				Line line = new Line((startPos.x * pxUnit), -((startPos.y * pxUnit)) + centerY, (endPos.x * pxUnit), -((endPos.y * pxUnit)) + centerY);
				line.setStroke(Color.BROWN);
				line.setStrokeWidth(5);
				Circle leaf = new Circle((endPos.x * pxUnit), -((endPos.y * pxUnit)) + centerY, plant.LEAFSIZE * pxUnit);
				plant.addLeaf(endPos);
				leaf.setFill(Color.GREEN);
				root.getChildren().addAll(line, leaf);
			}
		}
	}
	
	public void initPlants(Pane root) {
		for (int i = 0; i < 20; i++) {
			Plant temp = new Plant((i + 1) * 10);
			/*
			if (i == 0) {
				temp.setGenome("CGCCCCAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACGGGAAAA");
			}
			*/
			drawPlant(temp, root);
			plants.add(temp);
		}
		plantPos.clear();
	}
	
	public void reproduce(Pane root) {
		calculateFitness(root);
		removeUnfit();
		ArrayList<Plant> temp = new ArrayList<Plant>(plants);
		for (int i = 0; i < temp.size(); i++) {
			Plant p1 = temp.get(i);
			Plant p2 = null;
			int index2 = -1;
			do {
				index2 = (int)(Math.random() * temp.size());
				p2 = temp.get(index2);
			} while(p2 != p1);
			int pos = (int)(Math.random() * plantPos.size());
			Plant child = new Plant(plantPos.get(pos), p1, p2);
			plantPos.remove(pos);
			plants.add(child);
		}
	}
	
	public void calculateFitness(Pane root) {
		for (Plant plant : plants) {
			plant.setFitness(0);
		}
		ArrayList<Light> lightPackets = new ArrayList<Light>();
		for (int i = 0; i < windowWidth + 1; i++) {
			Point lightPos = new Point(i * pxUnit, 1000);
			Light light = new Light(lightPos);
			lightPackets.add(light);
			System.out.println(i);
		}
		for (int i = 1000; i > 0; i--) {
			for (Light light : lightPackets) {
				if (i > 10000) {
					Circle temp = new Circle(light.pos.x, (light.pos.y) , 1);
					temp.setFill(Color.RED);
					root.getChildren().addAll(temp);
				}
				for (Plant plant : plants) {
					if (plant.pointInLeaf(light.pos)) {
						plant.addFitness(light.strength);
						light.strength /= 2;
					}
				}
				light.pos.y --;
				//light.strength -= light.strength/100000;
			}
		}
		Collections.shuffle(plants);
		Collections.sort(plants);
		
		for (Plant plant : plants) {
			System.out.println(plant.getFitness());
		}
		System.out.println();
		
	}
	
	public void removeUnfit() {
		int half = (plants.size() / 2) - 1;
		for (int i = plants.size() - 1; i > half; i--) {
			plantPos.add(plants.get(half).groundPos().x);
			plants.remove(half);
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}