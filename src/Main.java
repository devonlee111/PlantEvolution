import javafx.stage.Stage;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class Main extends Application {
	double pxUnit = 5;
	double centerX = 500;
	double centerY = 1000;
	
	@Override
	public void start(Stage primaryStage) {
		Group root = new Group();
		Scene scene = new Scene(root, 1000, 1000, Color.SKYBLUE);
		primaryStage.setScene(scene);
		primaryStage.show();
		Plant p1 = new Plant(10);
		Plant p2 = new Plant(11);
		Plant child = new Plant(12, p1, p2);
		child.branchLength(0);
		drawPlant(child, root);
	}
	
	public void calculateFitness(Plant plant) {

	}
	
	public void drawPlant(Plant plant, Group root) {
		double theta, length;
		Point startPos = plant.groundPos();
		Point endPos;
		for (int i = 0; i < 20; i++) {
			int geneType = plant.geneType(i * 10);
			if (geneType == 0) {
				theta = plant.branchAngle(i * 10);
				length = plant.branchLength(i * 10);
				theta += 90;
				System.out.println(theta + ", " + length);
				theta *= (Math.PI / 180);
				endPos = new Point(-(length * Math.cos(theta) * pxUnit) + startPos.x + centerX, -(length * Math.sin(theta) * pxUnit) + startPos.y + centerY);
				Line line = new Line(startPos.x + centerX, startPos.y + centerY, endPos.x, endPos.y);
				line.setStroke(Color.BROWN);
				line.setStrokeWidth(10);
				root.getChildren().addAll(line);
				startPos = endPos;
			}
			else if (geneType == 1) {
				theta = plant.branchAngle(i * 10);
				length = plant.branchLength(i * 10);
				theta += 90;
				System.out.println(theta + ", " + length);
				theta *= (Math.PI / 180);
				endPos = new Point(-(length * Math.cos(theta) * pxUnit) + startPos.x + centerX, -(length * Math.sin(theta) * pxUnit) + startPos.y + centerY);
				Line line = new Line(startPos.x + centerX, startPos.y + centerY, endPos.x, endPos.y);
				line.setStroke(Color.BROWN);
				line.setStrokeWidth(10);
				Circle leaf = new Circle(endPos.x, endPos.y, 20);
				leaf.setFill(Color.GREEN);
				root.getChildren().addAll(line, leaf);
			}
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}