import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class Main extends Application {
	double pixPIn = 10; 
	
	double centerX = 500;
	double centerY = 1000;
	
	@Override
	public void start(Stage primaryStage) {
		Group root = new Group();
		Scene scene = new Scene(root, 1000, 1000, Color.SKYBLUE);
	
		Plant test1 = new Plant("11001000111011010000010110110110111100110111100110111011001010011010100110");
		Plant test2 = new Plant("10111100111101101111110010011000000001110001011001101101110100111110000101");
		Plant test = test1.reproduce(test2);
		primaryStage.setScene(scene);
		primaryStage.show();
		drawPlant(test, root);
		/*
		double botX, botY, endX, endY, topX, topY;
		double theta = 0;
		double thetaD;
		
		for (int i = 0; i < 91; i++) {
			thetaD = theta * (Math.PI / 180);
			topX = (0 + 7 * Math.cos(thetaD));
			topY = (0 - 7 * Math.sin(thetaD));
			botX = (0 + 7 * Math.cos(thetaD));
			botY = (0 - 7 * Math.sin(thetaD));
			endX = botX + (150 * Math.sin(thetaD));
			endY = botY + (150 * Math.cos(thetaD));
			
			System.out.print("theta is " + thetaD);
			System.out.print("| Bottom is " + botX + ", " + botY);
			System.out.println("| Top is " + topX + ", " +  topY);
			theta += (1);
			}
		}
		/*
		root.getChildren().addAll(new Line(botX  * pixPIn, botY * pixPIn, endX * pixPIn, endY * pixPIn));
		
		Line2D line = new Line2D.Double(botX, botY, endX, endY);
		MyCircle circle = new MyCircle(60, 60, 7);
		circle.intersect(line);

		primaryStage.setScene(scene);
		primaryStage.show();
		Plant[] generation = new Plant[100];
		for(int i = 0; i < 100; i++) {
			Plant plant = new Plant();
			generation[i] = plant;
		}
		for (Plant plant : generation) {
			calculateFitness(plant);
		}
		Arrays.sort(generation);
		System.out.println(generation[0].stemLength() + " | " + generation[0].leafSize() + " | " + generation[0].stemAngle());
		*/
	}
	
	public void calculateFitness(Plant plant) {
		double thetaD = -90;
		double theta;
		for (int i = 0; i < 180; i++) {
			
		}
	}
	
	public void drawPlant(Plant plant, Group root) {
		ArrayList<Point> points = plant.processPlant();
		int i = 0;
		double x1 = 0;
		double y1 = 0;
		double x2 = 0;
		double y2 = 0;
		for (Point point : points) {
			if (i % 2 == 0) {
				x1 = point.x;
				y1 = point.y;
			}
			if (i % 2 == 1) {
				x2 = point.x;
				y2 = point.y;
				Line temp = new Line((x1 * pixPIn) + centerX, (-y1 * pixPIn) + centerY, (x2 * pixPIn) + centerX, (-y2 * pixPIn) + centerY);
				temp.setStroke(Color.BROWN);
				temp.setStrokeWidth(10);
				root.getChildren().addAll(temp);
				System.out.println(x1 + ", " + y1 + " | " + x2 + ", " + y2);
			}
			i++;
		}
		ArrayList<Point> leaves = plant.getLeafPos();
		for (Point leaf : leaves) {
			Circle newLeaf = new Circle((leaf.x * pixPIn) + centerX, (-leaf.y * pixPIn) + centerY, (plant.leafSize() / 2) * pixPIn);
			newLeaf.setFill(Color.GREEN);
			root.getChildren().addAll(newLeaf);
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}