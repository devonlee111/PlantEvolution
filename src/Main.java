import java.awt.geom.Line2D;
import java.util.Arrays;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class Main extends Application {
	double pixPIn = 5; 
	@Override
	public void start(Stage primaryStage) {
		Group root = new Group();
		Scene scene = new Scene(root, 900, 900, Color.SKYBLUE);
		
		Circle center = new Circle (0 * pixPIn, 0 * pixPIn, 7 * pixPIn);
		center.setFill(Color.GREEN);
		Circle other = new Circle(60 * pixPIn, 60 * pixPIn, 7 * pixPIn);
		other.setFill(Color.GREEN);
		root.getChildren().addAll(center, other);
		
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
	}
	*/
	public void calculateFitness(Plant plant) {
		for (int i = 0; i < 180; i++) {
			
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}